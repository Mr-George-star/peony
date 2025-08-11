package net.george.peony.block.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.peony.block.entity.PotStandWithCampfireBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

@Environment(EnvType.CLIENT)
public class PotStandWithCampfireBlockEntityRenderer extends AbstractPotStandRenderer<PotStandWithCampfireBlockEntity> {
    public PotStandWithCampfireBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
}
