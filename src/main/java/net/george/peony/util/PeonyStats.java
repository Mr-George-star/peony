package net.george.peony.util;

import net.george.peony.Peony;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class PeonyStats {
    public static final Identifier SKILLET_COOKING_SUCCESS = register("skillet_cooking_success", StatFormatter.DEFAULT);
    public static final Identifier SKILLET_COOKING_FAILURE = register("skillet_cooking_failure", StatFormatter.DEFAULT);

    public static Identifier register(String name, StatFormatter formatter) {
        Identifier id = Peony.id(name);
        Registry.register(Registries.CUSTOM_STAT, name, id);
        Stats.CUSTOM.getOrCreateStat(id, formatter);
        return id;
    }

    public static void register() {
        Peony.debug("Stats");
    }
}
