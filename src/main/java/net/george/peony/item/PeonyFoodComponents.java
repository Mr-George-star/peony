package net.george.peony.item;

import net.george.peony.Peony;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

public class PeonyFoodComponents {
    public static final FoodComponent LARD = new FoodComponent.Builder().nutrition(2).saturationModifier(3).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600), 1).build();
    public static final FoodComponent PEANUT_KERNEL = new FoodComponent.Builder().nutrition(1).saturationModifier(1).snack().build();
    public static final FoodComponent ROASTED_PEANUT_KERNEL = new FoodComponent.Builder().nutrition(2).saturationModifier(1.5F).snack().build();
    public static final FoodComponent CRUSHED_PEANUTS = new FoodComponent.Builder().nutrition(2).saturationModifier(1).snack().build();
    public static final FoodComponent TOMATO = new FoodComponent.Builder().nutrition(3).saturationModifier(0.5F).snack().build();
    public static final FoodComponent TOMATO_SAUCE = createStew(6).build();
    public static final FoodComponent SCRAMBLED_EGGS = createStew(7).build();
    
    public static FoodComponent.Builder createStew(int hunger) {
        return new FoodComponent.Builder().nutrition(hunger).saturationModifier(0.6F).usingConvertsTo(Items.BOWL);
    }
    
    public static void register() {
        Peony.debug("Food Components");
    }
}
