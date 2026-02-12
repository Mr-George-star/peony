package net.george.peony.api.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public interface NbtSerializable extends NbtWriteable, NbtReadable {
    @Override
    void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries);

    @Override
    void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries);
}
