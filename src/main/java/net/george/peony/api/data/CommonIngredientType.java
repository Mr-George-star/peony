package net.george.peony.api.data;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.george.peony.Peony;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

@SuppressWarnings({"rawtypes"})
public class CommonIngredientType<T extends CommonIngredient> {
    public static final ItemApiLookup<CommonIngredientType, Void> LOOKUP = ItemApiLookup.
            get(Peony.id("common_ingredient_types"), CommonIngredientType.class, Void.class);
    public static final RegistryKey<Registry<CommonIngredientType<?>>> REGISTRY_KEY = 
            RegistryKey.ofRegistry(Peony.id("common_ingredient_type"));
    public static final Registry<CommonIngredientType<?>> REGISTRY =
            FabricRegistryBuilder.createSimple(REGISTRY_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    private final MapCodec<T> codec;
    private final PacketCodec<RegistryByteBuf, T> packetCodec;
    private final Identifier id;
    private final Supplier<T> instanceSupplier;

    public CommonIngredientType(Identifier id, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec, Supplier<T> instanceSupplier) {
        this.id = id;
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.instanceSupplier = instanceSupplier;
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

    public T createInstance() {
        return this.instanceSupplier.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CommonIngredientType<?> that)) {
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
        return "CommonIngredientType[" + this.id + "]";
    }

    public static <T extends CommonIngredient> CommonIngredientType<T> register(String name, MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec, Supplier<T> factory) {
        Identifier id = Peony.id(name);
        CommonIngredientType<T> CommonIngredientType = new CommonIngredientType<>(id, codec, packetCodec, factory);
        return Registry.register(REGISTRY, id, CommonIngredientType);
    }
}
