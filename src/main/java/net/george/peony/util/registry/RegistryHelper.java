package net.george.peony.util.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public interface RegistryHelper<T> {
    Registry<T> getRegistry();

    RegistryKey<Registry<T>> getRegistryKey();

    static <T> RegistryHelper<T> of(Registry<T> registry, RegistryKey<Registry<T>> registryKey) {
        return new RegistryHelper<>() {
            @Override
            public Registry<T> getRegistry() {
                return registry;
            }

            @Override
            public RegistryKey<Registry<T>> getRegistryKey() {
                return registryKey;
            }
        };
    }

    default <O extends T> O register(String name, O object) {
        return Registry.register(this.getRegistry(), Identifier.of(this.getModId(), name), object);
    }

    default RegistryKey<? super T> key(String path) {
        return RegistryKey.of(this.getRegistryKey(), id(path));
    }

    default Identifier id(String name) {
        return Identifier.of(this.getModId(), name);
    }

    default String getModId() {
        Class<?> clazz = this.getClass();

        if (clazz.isAnnotationPresent(Registration.class)) {
            Registration registration = clazz.getAnnotation(Registration.class);
            return registration.modId();
        } else {
            return "none";
        }
    }
}
