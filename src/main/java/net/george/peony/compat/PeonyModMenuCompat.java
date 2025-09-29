package net.george.peony.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.george.peony.PeonyConfig;

public class PeonyModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PeonyConfig::makeScreen;
    }
}
