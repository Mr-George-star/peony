package net.george.peony.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;

import java.util.Map;

public interface PotStandFamilyRegistryCallback {
    Event<PotStandFamilyRegistryCallback> EVENT = EventFactory.createArrayBacked(PotStandFamilyRegistryCallback.class,
            listeners -> entries -> {
                for (PotStandFamilyRegistryCallback callback : listeners) {
                    callback.interact(entries);
                }
            });

    void interact(Map<Block, Block> entries);
}
