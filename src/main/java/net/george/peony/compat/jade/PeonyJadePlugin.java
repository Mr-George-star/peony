package net.george.peony.compat.jade;

import net.george.peony.Peony;
import net.george.peony.block.GasCylinderBlock;
import net.george.peony.block.GasStoveBlock;
import net.george.peony.block.PotStandWithCampfireBlock;
import net.george.peony.block.SkilletBlock;
import net.minecraft.util.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PeonyJadePlugin implements IWailaPlugin {
    public static final Identifier SKILLET_ID = Peony.id("skillet_component");
    public static final Identifier DEFAULT_HEAT_SOURCE_ID = Peony.id("default_heat_source_component");
    public static final Identifier DEFAULT_OPENABLE_ID = Peony.id("default_openable_component");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SkilletComponentProvider.INSTANCE, SkilletBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(SkilletComponentProvider.INSTANCE, SkilletBlock.class);
        registration.registerBlockComponent(HeatSourceComponentProvider.DEFAULT, PotStandWithCampfireBlock.class);
        registration.registerBlockComponent(OpenableComponentProvider.DEFAULT, GasCylinderBlock.class);
        registration.registerBlockComponent(OpenableComponentProvider.DEFAULT, GasStoveBlock.class);
        registration.registerBlockComponent(HeatSourceComponentProvider.DEFAULT, GasStoveBlock.class);
    }
}
