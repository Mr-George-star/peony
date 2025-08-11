package net.george.peony.item;

import net.george.peony.Peony;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class PeonyFoodComponents {
    public static final FoodComponent LARD = new FoodComponent.Builder().nutrition(2).saturationModifier(3).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600), 1).build();

    public static void register() {
        Peony.debug("Food Components");
    }
}
