package net.george.peony.item;

import net.george.peony.recipe.ParingRecipe;
import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.sound.PeonySoundEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class ParingKnifeItem extends ToolItem {
    protected static final RecipeManager.MatchGetter<SingleStackRecipeInput, ParingRecipe> MATCH_GETTER =
            RecipeManager.createCachedMatchGetter(PeonyRecipes.PARING_TYPE);

    public ParingKnifeItem(ToolMaterial material, Settings settings) {
        super(material, settings.component(DataComponentTypes.TOOL, createToolComponent())
                .attributeModifiers(createDefaultAttributeModifiers(material)));
    }

    private static ToolComponent createToolComponent() {
        return new ToolComponent(List.of(ToolComponent.Rule.of(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
    }

    public static AttributeModifiersComponent createDefaultAttributeModifiers(ToolMaterial material) {
        return createAttributeModifiers(material, 8.0F, -2.4F);
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        baseAttackDamage + material.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID,
                        attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(2, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.pass(heldStack);
        }

        ItemStack offHandStack = user.getOffHandStack();
        SingleStackRecipeInput input = new SingleStackRecipeInput(offHandStack);
        Optional<RecipeEntry<ParingRecipe>> matchedRecipe = MATCH_GETTER.getFirstMatch(input, world);

        if (matchedRecipe.isPresent()) {
            ItemStack result = matchedRecipe.get().value().craft(input, world.getRegistryManager());
            user.giveItemStack(result);
            offHandStack.decrementUnlessCreative(1, user);
            world.playSound(user, user.getBlockPos(), PeonySoundEvents.ITEM_PARING, SoundCategory.PLAYERS, 1.0F, 1.0F);
            heldStack.damage(1, user, EquipmentSlot.MAINHAND);
            return TypedActionResult.success(heldStack);
        } else {
            return TypedActionResult.pass(heldStack);
        }
    }
}
