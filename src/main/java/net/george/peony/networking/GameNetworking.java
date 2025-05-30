package net.george.peony.networking;

import com.google.common.base.Preconditions;
import dev.architectury.networking.SpawnEntityPacket;
import dev.architectury.utils.GameInstance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.george.peony.networking.payload.BuffedPayload;
import net.george.peony.networking.transform.PacketTransmissionType;
import net.george.peony.networking.transform.PacketTransmitter;
import net.george.peony.networking.transform.PacketsCollector;
import net.george.peony.networking.transform.SinglePacketCollector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class GameNetworking {
    public static final Logger LOGGER = LogManager.getLogger("GameNetworking");
    public static final Map<Identifier, CustomPayload.Id<?>> C2S_TYPE = new HashMap<>();
    public static final Map<Identifier, CustomPayload.Id<?>> S2C_TYPE = new HashMap<>();
    public static final Map<Identifier, PayloadReceiver<?>> C2S_RECEIVER = new HashMap<>();
    public static final Map<Identifier, PayloadReceiver<?>> S2C_RECEIVER = new HashMap<>();
    public static final Map<Identifier, PacketCodec<ByteBuf, ?>> C2S_CODECS = new HashMap<>();
    public static final Map<Identifier, PacketCodec<ByteBuf, ?>> S2C_CODECS = new HashMap<>();

    public static <T extends CustomPayload> void registerReceiver(PacketTransmissionType type,
                                                                  CustomPayload.Id<T> id,
                                                                  PacketCodec<? super RegistryByteBuf, T> codec,
                                                                  PayloadReceiver<T> receiver) {
        if (type == PacketTransmissionType.C2S) {
            registerC2SReceiver(id, codec, receiver);
        } else if (type == PacketTransmissionType.S2C) {
            registerS2CReceiver(id, codec, receiver);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPayload> void registerC2SReceiver(CustomPayload.Id<T> id,
                                                                     PacketCodec<? super RegistryByteBuf, T> codec,
                                                                     PayloadReceiver<T> receiver) {
        checkPreconditions(id, receiver);
        C2S_RECEIVER.put(id.id(), receiver);
        C2S_CODECS.put(id.id(), (PacketCodec<ByteBuf, ?>) codec);
        registerC2S(convert(id), BuffedPayload.streamCodec(id), (payload, context) -> {
            RegistryByteBuf buf = new RegistryByteBuf(Unpooled.wrappedBuffer(payload.payloadData()), context.getRegistryManager());
            PayloadReceiver<T> payloadReceiver = (PayloadReceiver<T>) C2S_RECEIVER.get(id.id());
            if (payloadReceiver == null) {
                throw new IllegalArgumentException("Payload Receiver not found! " + id.id());
            } else {
                T actualPayload = codec.decode(buf);
                payloadReceiver.receive(actualPayload, context);
            }
            buf.release();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPayload> void registerS2CReceiver(CustomPayload.Id<T> id,
                                                                      PacketCodec<? super RegistryByteBuf, T> codec,
                                                                      PayloadReceiver<T> receiver) {
        checkPreconditions(id, receiver);
        S2C_RECEIVER.put(id.id(), receiver);
        S2C_CODECS.put(id.id(), (PacketCodec<ByteBuf, ?>) codec);
        registerS2C(convert(id), BuffedPayload.streamCodec(id), (payload, context) -> {
            RegistryByteBuf buf = new RegistryByteBuf(Unpooled.wrappedBuffer(payload.payloadData()), context.getRegistryManager());
            PayloadReceiver<T> payloadReceiver = (PayloadReceiver<T>) S2C_RECEIVER.get(id.id());
            if (payloadReceiver == null) {
                throw new IllegalArgumentException("Payload Receiver not found! " + id.id());
            } else {
                T actualPayload = codec.decode(buf);
                payloadReceiver.receive(actualPayload, context);
            }
            buf.release();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPayload> void sendPacket(PacketTransmitter transmitter, PacketTransmissionType transmissionType, T payload, DynamicRegistryManager registryManager) {
        CustomPayload.Id<T> type = (CustomPayload.Id<T>) payload.getId();
        PacketCodec<ByteBuf, T> codec = (PacketCodec<ByteBuf, T>) (transmissionType == PacketTransmissionType.C2S ? C2S_CODECS.get(type.id()) : S2C_CODECS.get(type.id()));
        RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), registryManager);
        codec.encode(buf, payload);
        transmitter.send(toPacket(transmissionType, new BuffedPayload<>(type, ByteBufUtil.getBytes(buf))));
        buf.release();
    }

    public static <T extends CustomPayload> Packet<?> toPacket(PacketTransmissionType type, T payload) {
        if (type == PacketTransmissionType.C2S) {
            return toC2SPacket(payload);
        } else if (type == PacketTransmissionType.S2C) {
            return toS2CPacket(payload);
        } else {
            throw new IllegalArgumentException("Invalid side: " + type);
        }
    }

    public static <T extends CustomPayload> Packet<?> toC2SPacket(T payload) {
        return ClientPlayNetworking.createC2SPacket(payload);
    }

    public static <T extends CustomPayload> Packet<?> toS2CPacket(T payload) {
        return ServerPlayNetworking.createS2CPacket(payload);
    }

    public static <T extends CustomPayload> Packet<?> toPacket(PacketTransmissionType type, T payload, DynamicRegistryManager registryManager) {
        SinglePacketCollector transmitter = new SinglePacketCollector(null);
        sendPacket(transmitter, type, payload, registryManager);
        return transmitter.getPacket();
    }

    public static <T extends CustomPayload> List<Packet<?>> toPackets(PacketTransmissionType side, T payload, DynamicRegistryManager registryManager) {
        PacketsCollector transmitter = new PacketsCollector(null);
        sendPacket(transmitter, side, payload, registryManager);
        return transmitter.getPackets();
    }

    public static <T extends CustomPayload> void sendToPlayer(ServerPlayerEntity player, T payload) {
        sendPacket(PacketTransmitter.fromPlayer(player), PacketTransmissionType.S2C, payload, player.getRegistryManager());
    }

    public static <T extends CustomPayload> void sendToPlayers(Iterable<ServerPlayerEntity> players, T payload) {
        Iterator<ServerPlayerEntity> iterator = players.iterator();
        if (iterator.hasNext()) {
            sendPacket(PacketTransmitter.fromPlayers(players), PacketTransmissionType.S2C, payload, iterator.next().getRegistryManager());
        }
    }

    @Environment(EnvType.CLIENT)
    public static <T extends CustomPayload> void sendToServer(T payload) {
        ClientPlayNetworkHandler networkHandler = GameInstance.getClient().getNetworkHandler();
        if (networkHandler != null) {
            sendPacket(PacketTransmitter.fromClient(), PacketTransmissionType.C2S, payload, networkHandler.getRegistryManager());
        }
    }

    @Environment(EnvType.CLIENT)
    public static boolean canServerReceive(Identifier id) {
        return ClientPlayNetworking.canSend(id);
    }

    public static boolean canPlayerReceive(ServerPlayerEntity player, CustomPayload.Id<?> type) {
        return canPlayerReceive(player, type.id());
    }

    public static boolean canPlayerReceive(ServerPlayerEntity player, Identifier id) {
        return ServerPlayNetworking.canSend(player, id);
    }

    public static Packet<ClientPlayPacketListener> createAddEntityPacket(Entity entity, EntityTrackerEntry serverEntity) {
        return SpawnEntityPacket.create(entity, serverEntity);
    }

    public static <T extends CustomPayload, R extends CustomPayload> CustomPayload.Id<R> convert(CustomPayload.Id<T> givenId) {
        return new CustomPayload.Id<>(givenId.id());
    }

    /* PRIVATE */

    private static <T extends CustomPayload> void checkPreconditions(CustomPayload.Id<T> id, PayloadReceiver<T> receiver) {
        Preconditions.checkNotNull(id, "Cannot register receiver with a null type!");
        Preconditions.checkNotNull(receiver, "Cannot register a null receiver!");
    }

    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec, PayloadReceiver<T> receiver) {
        GameNetworking.LOGGER.info("Registering C2S receiver with id {}", id.id());
        PayloadTypeRegistry.playC2S().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, (payload, fabricContext) -> {
            PayloadContext context = GameNetworking.context(fabricContext.player(), fabricContext.server(), false);
            receiver.receive(payload, context);
        });
    }

    @Environment(EnvType.CLIENT)
    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec, PayloadReceiver<T> receiver) {
        GameNetworking.LOGGER.info("Registering S2C receiver with id {}", id.id());
        PayloadTypeRegistry.playS2C().register(id, codec);
        ClientPlayNetworking.registerGlobalReceiver(id, (payload, fabricContext) -> {
            PayloadContext context = GameNetworking.context(fabricContext.player(), fabricContext.client(), true);
            receiver.receive(payload, context);
        });
    }

    private static PayloadContext context(final PlayerEntity player, final ThreadExecutor<?> taskQueue, final boolean isClient) {
        return new PayloadContext() {
            @Override
            public PlayerEntity getPlayer() {
                return player;
            }

            @Override
            public EnvType getEnvironment() {
                return isClient ? EnvType.CLIENT : EnvType.SERVER;
            }

            @Override
            public PacketTransmissionType getTransmissionType() {
                return PacketTransmissionType.to(getEnvironment());
            }

            @Override
            public DynamicRegistryManager getRegistryManager() {
                return player.getRegistryManager();
            }

            @Override
            public void runInClient(ThreadRunner<MinecraftClient> action) {
                if (taskQueue instanceof MinecraftClient client) {
                    client.execute(() -> action.run(client));
                }
            }

            @Override
            public void runInServer(ThreadRunner<MinecraftServer> action) {
                if (taskQueue instanceof MinecraftServer server) {
                    server.execute(() -> action.run(server));
                }
            }

            //            @Override
//            public void queue(Runnable program) {
//                taskQueue.execute(program);
//            }
        };
    }

    @FunctionalInterface
    public interface PayloadReceiver<T extends CustomPayload> {
        void receive(T payload, PayloadContext context);
    }

    public interface PayloadContext {
        PlayerEntity getPlayer();

        EnvType getEnvironment();

        PacketTransmissionType getTransmissionType();

        DynamicRegistryManager getRegistryManager();

//        void queue(Runnable program);

        void runInClient(ThreadRunner<MinecraftClient> action);

        void runInServer(ThreadRunner<MinecraftServer> action);
    }

    @FunctionalInterface
    public interface ThreadRunner<T extends ThreadExecutor<?>> {
        void run(T thread);
    }
}
