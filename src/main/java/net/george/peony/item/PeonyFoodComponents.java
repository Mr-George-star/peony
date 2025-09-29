package net.george.peony.item;

import net.george.peony.Peony;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class PeonyFoodComponents {
    public static final FoodComponent LARD = new FoodComponent.Builder().nutrition(2).saturationModifier(3).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600), 1).build();
    public static final FoodComponent PEANUT_KERNEL = new FoodComponent.Builder().nutrition(1).saturationModifier(1).alwaysEdible().snack().build();
    public static final FoodComponent ROASTED_PEANUT_KERNEL = new FoodComponent.Builder().nutrition(2).saturationModifier(1.5F).alwaysEdible().snack().build();
    public static final FoodComponent CRUSHED_PEANUTS = new FoodComponent.Builder().nutrition(2).saturationModifier(1).build();

    public static void register() {
        Peony.debug("Food Components");
    }
}
