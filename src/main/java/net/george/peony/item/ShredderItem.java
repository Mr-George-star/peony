package net.george.peony.item;

import net.george.peony.recipe.PeonyRecipes;
import net.george.peony.recipe.ShreddingRecipe;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public class ShredderItem extends ToolItem {
    protected static final RecipeManager.MatchGetter<SingleStackRecipeInput, ShreddingRecipe> MATCH_GETTER =
            RecipeManager.createCachedMatchGetter(PeonyRecipes.SHREDDING_TYPE);

    public ShredderItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.pass(heldStack);
        }

        ItemStack offHandStack = user.getOffHandStack();
        SingleStackRecipeInput input = new SingleStackRecipeInput(offHandStack);
        Optional<RecipeEntry<ShreddingRecipe>> matchedRecipe = MATCH_GETTER.getFirstMatch(input, world);

        if (matchedRecipe.isPresent()) {
            ShreddingRecipe recipe = matchedRecipe.get().value();

            ItemStack result = recipe.craft(input, world.getRegistryManager());
            user.giveItemStack(result);
            offHandStack.decrementUnlessCreative(1, user);
            heldStack.damage(recipe.durationDecrement(), user, EquipmentSlot.MAINHAND);
            return TypedActionResult.success(heldStack);
        } else {
            return TypedActionResult.pass(heldStack);
        }
    }
}
