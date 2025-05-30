package net.george.peony.data.json;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface RecipeJsonBuilder {
    Item getOutputItem();

    void offerTo(RecipeExporter exporter, Identifier recipeId);

    default void offerTo(RecipeExporter exporter) {
        this.offerTo(exporter, getItemId(this.getOutputItem()));
    }

    default void offerTo(RecipeExporter exporter, String recipePath) {
        Identifier outputItemId = getItemId(this.getOutputItem());
        Identifier recipePathId = Identifier.of(recipePath);
        if (recipePathId.equals(outputItemId)) {
            throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
        } else {
            this.offerTo(exporter, recipePathId);
        }
    }

    static Identifier getItemId(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem());
    }
}
