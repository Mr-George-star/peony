package net.george.peony.mixin;

import net.george.peony.Peony;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
    @Inject(method = "getGroupForRecipe", at = @At("HEAD"), cancellable = true)
    private static void peony$unableWarning(RecipeEntry<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        Identifier id = Registries.RECIPE_TYPE.getId(recipe.value().getType());
        if (id != null && id.getNamespace().equals(Peony.MOD_ID)) {
            cir.setReturnValue(RecipeBookGroup.UNKNOWN);
        }
    }
}
