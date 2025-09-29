package net.george.peony;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.george.peony.util.PeonyTranslationKeys;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PeonyConfig {
    public static ConfigClassHandler<PeonyConfig> HANDLER = ConfigClassHandler.createBuilder(PeonyConfig.class)
            .id(Peony.id("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("peony.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static Screen makeScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
                .title(Text.translatable(PeonyTranslationKeys.CONFIG_SCREEN_TITLE))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable(PeonyTranslationKeys.CONFIG_CATEGORY_COMMON))
                        .tooltip(Text.translatable(PeonyTranslationKeys.CONFIG_CATEGORY_DESCRIPTION_COMMON))
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable(PeonyTranslationKeys.OPTION_LARD_SLOWNESS_DURATION_TICKS))
                                .description(OptionDescription.of(Text.translatable(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_SLOWNESS_DURATION_TICKS)))
                                .binding(
                                        defaults.lardSlownessDurationTicks,
                                        () -> config.lardSlownessDurationTicks,
                                        value -> config.lardSlownessDurationTicks = value
                                )
                                .controller(option -> IntegerSliderControllerBuilder.create(option)
                                        .range(20, 200)
                                        .step(20)
                                        .formatValue(seconds -> Text.translatable(PeonyTranslationKeys.SECOND, seconds))
                                )
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable(PeonyTranslationKeys.OPTION_LARD_FIRE_EXTENSION_TICKS))
                                .description(OptionDescription.of(Text.translatable(PeonyTranslationKeys.OPTION_DESCRIPTION_LARD_FIRE_EXTENSION_TICKS)))
                                .binding(
                                        defaults.lardFireExtensionTicks,
                                        () -> config.lardFireExtensionTicks,
                                        value -> config.lardFireExtensionTicks = value
                                )
                                .controller(option -> IntegerSliderControllerBuilder.create(option)
                                        .range(20, 200)
                                        .step(20)
                                        .formatValue(seconds -> Text.translatable(PeonyTranslationKeys.SECOND, seconds))
                                )
                                .build())
                        .build()))
                .generateScreen(parent);
    }

    @SerialEntry(comment = "The duration of the slowness effect in lard. When the player is in lard fluid, the slowness effect will be applied. The following durations are in ticks (1 second = 20 ticks).")
    public int lardSlownessDurationTicks = 100;
    @SerialEntry(comment = "If the player is on fire, entering or jumping into lard fluid will extend the fire duration (including cauldrons containing lard). The following durations are in ticks (1 second = 20 ticks).")
    public int lardFireExtensionTicks = 100;
}
