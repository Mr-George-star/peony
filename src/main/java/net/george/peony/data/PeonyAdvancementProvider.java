package net.george.peony.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.george.peony.Peony;
import net.george.peony.advancement.criterion.CookingFinishedCriterion;
import net.george.peony.block.PeonyBlocks;
import net.george.peony.block.entity.PeonyBlockEntities;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.PeonyTags;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PeonyAdvancementProvider extends FabricAdvancementProvider {
    protected PeonyAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registries, Consumer<AdvancementEntry> exporter) {
        AdvancementEntry root = Advancement.Builder.create()
                .display(
                        PeonyItems.SPATULA,
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_ROOT_TITLE),
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_ROOT_DESCRIPTION),
                        Peony.id("textures/gui/advancements/backgrounds/peony.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("get_spatula", InventoryChangedCriterion.Conditions.items(PeonyItems.SPATULA))
                .build(exporter, "peony:peony/root");

        // entries
        AdvancementEntry cookingOil = Advancement.Builder.create()
                .parent(root)
                .display(
                        PeonyItems.LARD,
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_COOKING_OIL_TITLE),
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_COOKING_OIL_DESCRIPTION),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("get_any_cooking_oil", InventoryChangedCriterion.Conditions
                        .items(ItemPredicate.Builder.create().tag(PeonyTags.Items.COOKING_OIL)))
                .build(exporter, "peony:peony/cooking_oil");

        // cooking stuffs
        AdvancementEntry kitchenware = Advancement.Builder.create()
                .parent(root)
                .display(
                        PeonyBlocks.SKILLET,
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_KITCHENWARE_TITLE),
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_KITCHENWARE_DESCRIPTION),
                        null,
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("get_any_kitchenware", InventoryChangedCriterion.Conditions
                        .items(ItemPredicate.Builder.create().tag(PeonyTags.Items.KITCHENWARE)))
                .build(exporter, "peony:peony/kitchenware");
        AdvancementEntry skillet = Advancement.Builder.create()
                .parent(kitchenware)
                .display(
                        PeonyBlocks.SKILLET,
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_SKILLET_TITLE),
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_SKILLET_DESCRIPTION),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        true
                )
                .criterion("get_skillet", InventoryChangedCriterion.Conditions
                        .items(PeonyBlocks.SKILLET))
                .build(exporter, "peony:peony/kitchenware/skillet");
        AdvancementEntry skilletCookingSucceed = Advancement.Builder.create()
                .parent(skillet)
                .display(
                        PeonyItems.SCRAMBLED_EGGS_WITH_TOMATOES,
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_SKILLET_COOKING_SUCCEED_TITLE),
                        Text.translatable(PeonyTranslationKeys.ADVANCEMENT_SKILLET_COOKING_SUCCEED_DESCRIPTION),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        true
                )
                .criterion("cooking_succeed", CookingFinishedCriterion.Conditions
                        .create(PeonyBlockEntities.SKILLET, CookingFinishedCriterion.Conditions.FinishingType.SUCCESS))
                .build(exporter, "peony:peony/kitchenware/skillet/skillet_cooking_succeed");
    }
}
