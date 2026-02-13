package net.george.peony.api.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InteractionContext {
    public World world;
    public BlockPos pos;
    public PlayerEntity user;
    public Hand hand;

    private InteractionContext(World world, BlockPos pos, PlayerEntity user, Hand hand) {
        this.world = world;
        this.pos = pos;
        this.user = user;
        this.hand = hand;
    }

    public static InteractionContext create(World world, BlockPos pos, PlayerEntity user, Hand hand) {
        return new InteractionContext(world, pos, user, hand);
    }

    /**
     * @return true if the player is currently sneaking
     */
    public boolean isSneaking() {
        return this.user.isSneaking();
    }
}
