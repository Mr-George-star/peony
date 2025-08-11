package net.george.peony.util.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class RegistryDataUtils {
    public static final Toolkit<Item> ITEM = createToolkit(Registries.ITEM, Item.class);
    public static final Toolkit<Block> BLOCK = createToolkit(Registries.BLOCK, Block.class);

    public static <T> List<T> filterToList(Registry<T> registry, Predicate<? super T> condition) {
        return filter(registry, condition).toList();
    }

    public static <T> T[] filterToArray(Registry<T> registry, Predicate<? super T> condition, IntFunction<T[]> toArrayFunction) {
        return filterToList(registry, condition).toArray(toArrayFunction);
    }

    public static <T> Toolkit<T> createToolkit(Registry<T> registry, Class<T> clazz) {
        return new Toolkit<>() {
            @Override
            public List<T> filterToList(Predicate<? super T> condition) {
                return RegistryDataUtils.filterToList(registry, condition);
            }

            @Override
            public T[] filterToArray(Predicate<? super T> condition) {
                List<T> list = this.filterToList(condition);
                @SuppressWarnings("unchecked")
                T[] array = (T[]) Array.newInstance(clazz, list.size());
                return list.toArray(array);
            }

            @Override
            public T[] filterToArray(Predicate<? super T> condition, IntFunction<T[]> toArrayFunction) {
                return RegistryDataUtils.filterToArray(registry, condition, toArrayFunction);
            }
        };
    }

    static <T> Stream<T> filter(Registry<T> registry, Predicate<? super T> condition) {
        return registry.stream().filter(condition);
    }

    public interface Toolkit<T> {
        List<T> filterToList(Predicate<? super T> condition);

        T[] filterToArray(Predicate<? super T> condition);

        T[] filterToArray(Predicate<? super T> condition, IntFunction<T[]> toArrayFunction);
    }
}
