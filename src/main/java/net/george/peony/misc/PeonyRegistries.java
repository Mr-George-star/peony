package net.george.peony.misc;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.george.peony.Peony;
import net.george.peony.block.*;
import net.george.peony.block.entity.*;
import net.george.peony.fluid.PeonyFluids;
import net.george.peony.item.KitchenKnifeItem;
import net.george.peony.item.PeonyItems;
import net.george.peony.util.BlockPlacements;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.george.peony.item.PeonyItems.CONDIMENT_BOTTLE;
import static net.minecraft.item.Items.BUCKET;
import static net.minecraft.item.Items.GLASS_BOTTLE;
import static net.minecraft.server.command.CommandManager.literal;

public class PeonyRegistries {
    public static final Identifier SHORT_GRASS_LOOT = Identifier.ofVanilla("blocks/short_grass");
    public static final Identifier PIG_LOOT = Identifier.ofVanilla("entities/pig");

    private static void registerCompostingChances() {
        CompostingChanceRegistry registry = CompostingChanceRegistry.INSTANCE;

        registry.add(PeonyItems.BARLEY, 0.65F);
        registry.add(PeonyItems.BARLEY_SEEDS, 0.3F);
        registry.add(PeonyItems.PEANUT, 0.2F);
        registry.add(PeonyItems.PEANUT_KERNEL, 0.1F);
        registry.add(PeonyItems.TOMATO, 0.5F);
        registry.add(PeonyBlocks.DOUGH, 0.6F);
        registry.add(PeonyBlocks.FLATBREAD, 0.6F);
    }

    private static void registerBurning() {
        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();

        Registries.BLOCK.stream().forEach(block -> {
            if (block instanceof CuttingBoardBlock board) {
                instance.add(board, 5, 5);
            }
            if (block instanceof LogStickBlock logStick) {
                instance.add(logStick, 10, 5);
            }
            if (block instanceof PotStandBlock potStand) {
                instance.add(potStand, 5, 5);
            }
        });
    }

    private static void registerNonBlockRenderingItems() {
        NonBlockRenderingItems instance = NonBlockRenderingItems.getInstance();

        Registries.ITEM.stream().forEach(item -> {
            if (Registries.ITEM.getId(item).getNamespace().equals(Peony.MOD_ID)) {
                if (item instanceof AliasedBlockItem) {
                    instance.register(item);
                }
            }
        });
    }

    private static void registerCarvedRenderingItems() {
        CarvedRenderingItems instance = CarvedRenderingItems.getInstance();

        Registries.ITEM.stream().forEach(item -> {
            if (item instanceof PickaxeItem || item instanceof HoeItem) {
                instance.register(item, 225F);
            } else if (item instanceof TridentItem) {
                instance.register(item, 135F);
            } else if (item instanceof KitchenKnifeItem) {
                instance.register(item, 180F);
            }
        });
    }

    private static void modifyLootTables() {
        LootTableEvents.MODIFY.register((registryKey, builder, lootTableSource, wrapperLookup) -> {
            RegistryWrapper.Impl<Enchantment> enchantmentImpl = wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

            if (registryKey.getValue().equals(SHORT_GRASS_LOOT)) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.125F))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(enchantmentImpl.getOrThrow(Enchantments.FORTUNE), 2))
                        .with(ItemEntry.builder(PeonyItems.BARLEY_SEEDS));
                builder.pool(pool);
            } else if (registryKey.getValue().equals(PIG_LOOT)) {
                LootPool.Builder lardPool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(3, 5))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(enchantmentImpl.getOrThrow(Enchantments.FORTUNE), 2))
                        .with(ItemEntry.builder(PeonyItems.LARD));
                LootPool.Builder porkTenderloinPool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1, 2))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(enchantmentImpl.getOrThrow(Enchantments.FORTUNE), 2))
                        .with(ItemEntry.builder(PeonyItems.PORK_TENDERLOIN));
                builder.pool(lardPool);
                builder.pool(porkTenderloinPool);
            }
        });
    }

    private static void registerEvents() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();

            if (stack.isOf(Items.EGG)) {
                BlockState state = world.getBlockState(pos);

                if (state.isOf(PeonyBlocks.SKILLET)) {
                    return ActionResult.FAIL;
                }
            } else if (stack.isOf(Items.BOWL)) {
                BlockState state = world.getBlockState(pos);
                if (state.isOf(PeonyBlocks.SKILLET)) {
                    return ActionResult.PASS;
                }
                ItemPlacementContext ctx = new ItemPlacementContext(world, player, hand, stack, hitResult);
                return BlockPlacements.placeBlock(ctx, PeonyBlocks.BOWL);
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient) {
                ItemStack stack = player.getStackInHand(hand);
                BlockPos pos = hitResult.getBlockPos();

                if (stack.getItem() instanceof BucketItem) {
                    if (world.getBlockState(pos).getBlock() instanceof FermentationTankBlock) {
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        if (blockEntity instanceof FermentationTankBlockEntity cauldron) {
                            AccessibleInventory.InteractionContext context = AccessibleInventory.createContext(
                                    world, pos, player, hand);

                            ItemActionResult result = AccessibleInventory.access(
                                    cauldron, context, ItemDecrementBehaviour.createDefault());

                            return result.isAccepted() ? ActionResult.SUCCESS : ActionResult.PASS;
                        }
                    }
                }
            }

            return ActionResult.PASS;
        });
    }

    private static void registerFluidApi() {
        CauldronFluidContent.registerCauldron(PeonyBlocks.LARD_CAULDRON, PeonyFluids.STILL_LARD, FluidConstants.BOTTLE, LeveledCauldronBlock.LEVEL);

        registerFluidItem(PeonyItems.LARD_BUCKET, BUCKET, PeonyFluids.STILL_LARD, FluidConstants.BUCKET);
        registerFluidItem(PeonyItems.LARD_BOTTLE, GLASS_BOTTLE, PeonyFluids.STILL_LARD, FluidConstants.BOTTLE);
        registerFluidItem(PeonyItems.SOY_SAUCE_BUCKET, BUCKET, PeonyFluids.STILL_SOY_SAUCE, FluidConstants.BUCKET);
        registerFluidItem(PeonyItems.SOY_SAUCE, CONDIMENT_BOTTLE, PeonyFluids.STILL_SOY_SAUCE, FluidConstants.BOTTLE);
    }

    private static void registerFluidItem(Item fluidItem, Item container, Fluid fluid, long amount) {
        FluidStorage.combinedItemApiProvider(fluidItem).register(context ->
                new FullItemFluidStorage(context, item -> ItemVariant.of(container), FluidVariant.of(fluid), amount)
        );
        FluidStorage.combinedItemApiProvider(container).register(context ->
                new EmptyItemFluidStorage(context, item -> ItemVariant.of(fluidItem), fluid, amount)
        );
    }

    private static void registerDebugCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("checkGasConnection")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerWorld world = source.getWorld();
                    BlockPos pos = BlockPos.ofFloored(source.getPosition().add(0, -1, 0));

                    if (world.getBlockEntity(pos) instanceof GasStoveBlockEntity stove) {
                        List<BlockPos> possiblePositions = stove.getPossibleConnectionPositions();
                        source.sendFeedback(() -> Text.literal("Possible connection positions:"), false);
                        for (BlockPos checkPos : possiblePositions) {
                            boolean hasCylinder = world.getBlockState(checkPos).getBlock() instanceof GasCylinderBlock;
                            source.sendFeedback(() -> Text.literal("  " + checkPos.toShortString() +
                                    " - " + (hasCylinder ? "Gas Cylinder ✓" : "Empty")), false);
                        }

                        stove.getConnectedCylinderPos().ifPresentOrElse(
                                connectedPos -> source.sendFeedback(() ->
                                        Text.literal("Connected to: " + connectedPos.toShortString()), false),
                                () -> source.sendFeedback(() ->
                                        Text.literal("Not connected to any gas cylinder"), false)
                        );
                    } else {
                        source.sendFeedback(() -> Text.literal("This is not a gas stove block"), false);
                    }

                    return 1;
                })));
    }

    public static void register() {
        Peony.debug("Combats");
        registerCompostingChances();
        registerBurning();
        registerNonBlockRenderingItems();
        registerCarvedRenderingItems();
        registerFluidApi();
        modifyLootTables();
        registerEvents();
        ItemExchangeBehaviour.registerBehaviours();

        if (Peony.getConfig().debugCommands) {
            registerDebugCommands();
        }
    }
}
