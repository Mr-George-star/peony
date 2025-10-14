package net.george.peony.util.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.function.Predicate;

public interface TagRegistration<T> {
    default TagKey<T> of(String path) {
        return this.of(this.getModId(), path);
    }

    default TagKey<T> of(String namespace, String path) {
        return this.of(Identifier.of(namespace, path));
    }

    default TagKey<T> of(Identifier id) {
        return TagKey.of(this.getRegistryKey(), id);
    }

    RegistryKey<Registry<T>> getRegistryKey();

    default String getModId() {
        Class<?> clazz = this.getClass();

        if (clazz.isAnnotationPresent(Registration.class)) {
            Registration registration = clazz.getAnnotation(Registration.class);
            return registration.modId();
        } else {
            return "none";
        }
    }

    static Predicate<BlockState> blockIsInPredicate(TagKey<Block> key) {
        return state -> state.isIn(key);
    }

    static Predicate<Collection<BlockState>> blocksIsInPredicate(TagKey<Block> key) {
        return blockStates -> {
            Predicate<BlockState> predicate = blockIsInPredicate(key);
            for (BlockState state : blockStates) {
                if (!predicate.test(state)) {
                    return false;
                }
            }
            return true;
        };
    }
}
