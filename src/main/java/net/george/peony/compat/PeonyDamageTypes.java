package net.george.peony.compat;

import net.george.peony.Peony;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class PeonyDamageTypes {
    public static final RegistryKey<DamageType> SCALD = key("scald");

    public static RegistryKey<DamageType> key(String name) {
        return Peony.key(RegistryKeys.DAMAGE_TYPE, name);
    }

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static void bootstrap(Registerable<DamageType> context) {
        context.register(SCALD, new DamageType("scald", 0.0F));
    }
}
