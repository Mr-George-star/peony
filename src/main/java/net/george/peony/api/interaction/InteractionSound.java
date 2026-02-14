package net.george.peony.api.interaction;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record InteractionSound(SoundEvent sound, float volume, float pitch) {
    public static final InteractionSound DEFAULT =
            new InteractionSound(
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    0.3F,
                    1.0F
            );

    public void play(World world, BlockPos pos) {
        world.playSound(null, pos,
                this.sound, SoundCategory.BLOCKS, this.volume, this.pitch);
    }
}
