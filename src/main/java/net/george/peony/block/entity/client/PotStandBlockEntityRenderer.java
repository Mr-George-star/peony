package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.PotStandBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

@Environment(EnvType.CLIENT)
public class PotStandBlockEntityRenderer extends AbstractPotStandRenderer<PotStandBlockEntity> {
    public PotStandBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
}
