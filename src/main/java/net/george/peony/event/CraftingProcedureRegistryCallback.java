package net.george.peony.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.peony.util.math.Size;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface CraftingProcedureRegistryCallback {
    Event<CraftingProcedureRegistryCallback> EVENT = EventFactory.createArrayBacked(CraftingProcedureRegistryCallback.class,
            listeners -> guideSizes -> {
                for (CraftingProcedureRegistryCallback callback : listeners) {
                    callback.interact(guideSizes);
                }
            });

    void interact(Map<Identifier, Size> guideSizes);
}
