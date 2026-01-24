package net.george.peony.api.fluid;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FluidRecord {
    private final String translationKey;
    private final Text fluidDisplayName;
    private final long amount;
    private final long totalCapacity;
    private final long ratio;
    private final FluidVariant variant;
    private final Identifier fluidTextureId;

    FluidRecord(FluidStack stack, long totalCapacity) {
        this.translationKey = stack.getTranslationKey();
        this.fluidDisplayName = FluidVariantAttributes.getName(stack.getFluid());
        this.amount = stack.getAmount();
        Preconditions.checkState(this.amount <= totalCapacity, "Amount cannot be granter than the total capacity");
        this.totalCapacity = totalCapacity;
        this.ratio = this.amount / totalCapacity;

        FluidVariant variant = stack.getFluid();
        FluidRenderHandler renderHandler = FluidRenderHandlerRegistry.INSTANCE.get(variant.getFluid());
        Identifier fluidTextureId = Identifier.ofVanilla("block/water_still");

        if (renderHandler != null) {
            Sprite[] sprites = renderHandler.getFluidSprites(null, null,
                    variant.getFluid().getDefaultState());
            if (sprites != null && sprites.length > 0 && sprites[0] != null) {
                fluidTextureId = sprites[0].getContents().getId();
            }
        }
        this.variant = variant;
        this.fluidTextureId = fluidTextureId;
    }

    public static FluidRecord record(FluidStack stack, long totalCapacity) {
        return new FluidRecord(stack, totalCapacity);
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public Text getFluidDisplayName() {
        return this.fluidDisplayName;
    }

    public long getAmount() {
        return this.amount;
    }

    public long getTotalCapacity() {
        return this.totalCapacity;
    }

    public long getRatio() {
        return this.ratio;
    }

    public FluidVariant getVariant() {
        return this.variant;
    }

    public Identifier getFluidTextureId() {
        return this.fluidTextureId;
    }
}
