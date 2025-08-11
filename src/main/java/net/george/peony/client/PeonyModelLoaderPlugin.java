package net.george.peony.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.george.peony.Peony;
import net.minecraft.client.util.ModelIdentifier;

@Environment(EnvType.CLIENT)
public class PeonyModelLoaderPlugin implements ModelLoadingPlugin {
    public static final PeonyModelLoaderPlugin INSTANCE = new PeonyModelLoaderPlugin();
    public static final ModelIdentifier MILLSTONE_TOP = new ModelIdentifier(Peony.id("block/millstone_top"), "fabric_resource");

    @Override
    public void onInitializeModelLoader(Context context) {
        context.addModels(MILLSTONE_TOP.id());
    }
}
