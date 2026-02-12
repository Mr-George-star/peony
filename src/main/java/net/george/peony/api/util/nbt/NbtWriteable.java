package net.george.peony.api.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public interface NbtWriteable {
    void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries);
}
