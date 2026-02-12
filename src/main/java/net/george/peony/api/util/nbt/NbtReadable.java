package net.george.peony.api.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public interface NbtReadable {
    void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);
}
