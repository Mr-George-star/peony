package net.george.peony.item;

import net.george.peony.Peony;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

public class PeonyFoodComponents {
    // Saturation Modifier: <given value> / 10
    public static final FoodComponent LARD = new FoodComponent.Builder().nutrition(2).saturationModifier(3).alwaysEdible().snack().statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 600), 1).build();
    public static final FoodComponent PEANUT_KERNEL = new FoodComponent.Builder().nutrition(1).saturationModifier(1).snack().build();
    public static final FoodComponent ROASTED_PEANUT_KERNEL = new FoodComponent.Builder().nutrition(2).saturationModifier(1.5F).snack().build();
    public static final FoodComponent CRUSHED_PEANUTS = new FoodComponent.Builder().nutrition(2).saturationModifier(1).snack().build();
    public static final FoodComponent TOMATO = new FoodComponent.Builder().nutrition(3).saturationModifier(0.5F).snack().build();
    public static final FoodComponent HAM = new FoodComponent.Builder().nutrition(1).saturationModifier(0.1F).build();
    public static final FoodComponent BAKED_FLATBREAD = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6F).build();
    public static final FoodComponent TOMATO_SAUCE = createStew(6).build();
    public static final FoodComponent SCRAMBLED_EGGS = createStew(7).build();
    public static final FoodComponent SCRAMBLED_EGGS_WITH_TOMATOES = createPlate().nutrition(10).saturationModifier(1F).build();
    public static final FoodComponent FRIED_SHREDDED_POTATOES = createPlate().nutrition(8).saturationModifier(0.8F).build();
    public static final FoodComponent CHEESE = new FoodComponent.Builder().nutrition(3).saturationModifier(0.2F).build();

    public static FoodComponent.Builder createStew(int hunger) {
        return new FoodComponent.Builder().nutrition(hunger).saturationModifier(0.6F).usingConvertsTo(Items.BOWL);
    }

    public static FoodComponent.Builder createPlate() {
        return new FoodComponent.Builder().usingConvertsTo(PeonyItems.WOODEN_PLATE);
    }
    
    public static void register() {
        Peony.debug("Food Components");
    }
}
