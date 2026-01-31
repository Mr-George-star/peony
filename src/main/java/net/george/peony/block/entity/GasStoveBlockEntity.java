package net.george.peony.block.entity;

import net.george.peony.api.heat.Heat;
import net.george.peony.api.heat.HeatLevel;
import net.george.peony.api.heat.HeatProvider;
import net.george.peony.block.GasCylinderBlock;
import net.george.peony.block.GasStoveBlock;
import net.george.peony.block.data.Openable;
import net.george.peony.util.math.Range;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class GasStoveBlockEntity extends BlockEntity implements AccessibleInventory, Openable, HeatProvider, BlockEntityTickerProvider {
    @Nullable
    private BlockPos connectedCylinderPos;
    private boolean opened;
    private int usageCountdown = 0;
    private static final int[][][] CONNECTION_OFFSETS = {
            {{0, -1, 0}, {0, 1, 0}, {0, 0, -1}},
            {{0, -1, 0}, {0, 0, 1}, {0, 1, 0}},
            {{0, 1, 0}, {0, -1, 0}, {0, 0, 1}},
            {{0, 1, 0}, {0, 0, -1}, {0, -1, 0}}
    };

    public GasStoveBlockEntity(BlockPos pos, BlockState state) {
        super(PeonyBlockEntities.GAS_STOVE, pos, state);
        this.opened = false;
    }

    public Optional<BlockPos> getConnectedCylinderPos() {
        return Optional.ofNullable(this.connectedCylinderPos);
    }

    public void setConnectedCylinderPos(BlockPos pos) {
        this.connectedCylinderPos = pos;
        markDirty();
    }

    public void clearConnection() {
        this.connectedCylinderPos = null;
        markDirty();
    }

    private int[][] getConnectionOffsetsForDirection(Direction direction) {
        return CONNECTION_OFFSETS[direction.getHorizontal()];
    }

    protected void resetCountdown() {
        this.usageCountdown = 10;
    }

    protected boolean isCountdownOver() {
        return this.usageCountdown <= 0;
    }

    @Override
    public boolean isOpened() {
        return this.opened;
    }

    @Override
    public void open() {
        this.opened = true;
    }

    @Override
    public void close() {
        this.opened = false;
    }

    private void updateOpeningState(@NotNull World world) {
        world.setBlockState(this.pos, world.getBlockState(this.pos).with(GasStoveBlock.OPENED, this.opened));
    }

    /* HEAT */

    @Override
    public Heat getHeat() {
        return this.opened ?
                Heat.create(Range.create(500, 700), HeatLevel.HIGH) :
                Heat.NONE;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (this.connectedCylinderPos != null) {
            nbt.put("ConnectedCylinder", NbtHelper.fromBlockPos(this.connectedCylinderPos));
        }
        nbt.putBoolean("Opened", this.opened);
        nbt.putInt("UsageCountdown", this.usageCountdown);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("ConnectedCylinder")) {
            this.connectedCylinderPos = NbtHelper.toBlockPos(nbt, "ConnectedCylinder").orElse(null);
        }
        this.opened = nbt.getBoolean("Opened");
        this.usageCountdown = nbt.getInt("UsageCountdown");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    @Override
    public InsertResult insertItemSpecified(InteractionContext context, ItemStack givenStack) {
        if (givenStack.isOf(Items.STICK)) {
            if (this.isCountdownOver() && this.connectedCylinderPos != null &&
                    context.world.getBlockEntity(this.connectedCylinderPos) instanceof GasCylinderBlockEntity gasCylinder &&
                    gasCylinder.isOpened()) {
                if (this.isOpened()) {
                    this.close();
                } else {
                    this.open();
                }
                this.markDirty();
                this.resetCountdown();
                this.updateOpeningState(context.world);
                return AccessibleInventory.createResult(true, 0, false);
            }
        }
        return AccessibleInventory.createResult(false, -1);
    }

    private void checkAndUpdateConnections() {
        if (this.world == null || this.world.isClient) {
            return;
        }

        BlockState state = getCachedState();
        Direction facing = state.get(GasStoveBlock.FACING);
        int[][] offsets = this.getConnectionOffsetsForDirection(facing);

        for (int[] offset : offsets) {
            BlockPos checkPos = this.pos.add(offset[1], offset[0], offset[2]);

            if (this.world.getBlockState(checkPos).getBlock() instanceof GasCylinderBlock &&
                    this.world.getBlockEntity(checkPos) instanceof GasCylinderBlockEntity gasCylinder) {
                if (this.connectedCylinderPos == null || !this.connectedCylinderPos.equals(checkPos)) {
                    this.connectedCylinderPos = checkPos;
                    gasCylinder.setConnectedStovePos(this.pos);
                    this.markDirty();
                }
                return;
            }
        }

        if (this.connectedCylinderPos != null &&
                this.world.getBlockEntity(this.connectedCylinderPos) instanceof GasCylinderBlockEntity gasCylinder) {
            this.connectedCylinderPos = null;
            gasCylinder.clearConnection();
            this.markDirty();
        }
    }

    public List<BlockPos> getPossibleConnectionPositions() {
        List<BlockPos> positions = new ArrayList<>();
        if (this.world == null) {
            return positions;
        }

        BlockState state = getCachedState();
        Direction facing = state.get(GasStoveBlock.FACING);
        int[][] offsets = getConnectionOffsetsForDirection(facing);

        for (int[] offset : offsets) {
            positions.add(this.pos.add(offset[1], offset[0], offset[2]));
        }

        return positions;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        this.usageCountdown--;
        this.checkAndUpdateConnections();

        if (this.connectedCylinderPos != null && world.getBlockEntity(this.connectedCylinderPos)
                instanceof GasCylinderBlockEntity cylinder) {
            if (cylinder.isOpened() && this.isOpened()) {
                cylinder.consumeGas(world);
            }
            if (!cylinder.isOpened() && this.isOpened()) {
                this.close();
                this.markDirty();
                this.resetCountdown();
                this.updateOpeningState(world);
            }
        }
    }

    @Override
    public void clientTick(World world, BlockPos pos, BlockState state) {
    }
}
