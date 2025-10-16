package net.george.peony.api.action;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.george.peony.Peony;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@SuppressWarnings("ClassCanBeRecord")
public final class ActionType<T extends Action> {
    public static final RegistryKey<Registry<ActionType<?>>> REGISTRY_KEY =
            RegistryKey.ofRegistry(Peony.id("action_type"));
    public static final Registry<ActionType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    private final MapCodec<T> codec;
    private final PacketCodec<RegistryByteBuf, T> packetCodec;
    private final Identifier id;

    public ActionType(Identifier id, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
        this.id = id;
        this.codec = codec;
        this.packetCodec = packetCodec;
    }

    public MapCodec<T> getCodec() {
        return this.codec;
    }

    public PacketCodec<RegistryByteBuf, T> getPacketCodec() {
        return this.packetCodec;
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionType<?> that)) {
            return false;
        }
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "ActionType[" + this.id + "]";
    }

    public static <T extends Action> ActionType<T> register(String name, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
        Identifier id = Peony.id(name);
        ActionType<T> actionType = new ActionType<>(id, codec, packetCodec);
        return Registry.register(REGISTRY, id, actionType);
    }
}
