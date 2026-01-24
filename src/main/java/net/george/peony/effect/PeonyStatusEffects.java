package net.george.peony.effect;

import net.george.peony.Peony;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class PeonyStatusEffects {
    public static final RegistryEntry<StatusEffect> BITTER = register("bitter", new BitterStatusEffect());

    public static RegistryEntry<StatusEffect> register(String name, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Peony.id(name), effect);
    }

    public static void register() {
        Peony.debug("Status Effects");
    }
}
