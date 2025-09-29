package net.george.peony.mixin;

import net.george.peony.recipe.PeonyRecipes;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
    @Inject(method = "getGroupForRecipe", at = @At("HEAD"), cancellable = true)
    private static void peony$unableWarning(RecipeEntry<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        RecipeType<?> type = recipe.value().getType();
        if (type == PeonyRecipes.MILLING_TYPE) {
            cir.setReturnValue(RecipeBookGroup.UNKNOWN);
        } else if (type == PeonyRecipes.SEQUENTIAL_CRAFTING_TYPE) {
            cir.setReturnValue(RecipeBookGroup.UNKNOWN);
        } else if (type == PeonyRecipes.SEQUENTIAL_COOKING_TYPE) {
            cir.setReturnValue(RecipeBookGroup.UNKNOWN);
        }
    }
}
