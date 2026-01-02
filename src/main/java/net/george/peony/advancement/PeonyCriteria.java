package net.george.peony.advancement;

import net.george.peony.Peony;
import net.george.peony.advancement.criterion.CookingFinishedCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PeonyCriteria {
    public static final CookingFinishedCriterion COOKING_FINISHED = register("cooking_finished", new CookingFinishedCriterion());

    public static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Peony.id(name), criterion);
    }

    public static void register() {
        Peony.debug("Criteria");
    }
}
