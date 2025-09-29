package net.george.peony.compat.jade;

import net.george.peony.Peony;
import net.george.peony.block.SkilletBlock;
import net.minecraft.util.Identifier;
import snownee.jade.api.*;

@WailaPlugin
public class PeonyJadePlugin implements IWailaPlugin {
    public static final Identifier SKILLET_ID = Peony.id("skillet_component");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SkilletComponentProvider.INSTANCE, SkilletBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SkilletComponentProvider.INSTANCE, SkilletBlock.class);
    }
}
