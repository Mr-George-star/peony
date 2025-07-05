package net.george.peony.util;

import net.george.peony.Peony;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class PeonyBlockTags {
    public static final TagKey<Block> INCORRECT_FOR_KITCHEN_KNIFE = of("incorrect_for_kitchen_knife");

    protected static TagKey<Block> of(String path) {
        return of(Peony.id(path));
    }

    protected static TagKey<Block> of(Identifier id) {
        return TagKey.of(RegistryKeys.BLOCK, id);
    }
}
