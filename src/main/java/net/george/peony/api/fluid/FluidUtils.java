package net.george.peony.api.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;

public class FluidUtils {
    public static boolean hasFluidInItem(ItemStack stack) {
        ContainerItemContext context = ContainerItemContext.withConstant(stack);
        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, context);

        if (fluidStorage != null) {
            try (Transaction ignored = Transaction.openOuter()) {
                for (StorageView<FluidVariant> view : fluidStorage.nonEmptyViews()) {
                    if (!view.isResourceBlank() && view.getAmount() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
