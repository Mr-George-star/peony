package net.george.peony.api.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public interface NbtSerializable {
    void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);

    void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);
}
