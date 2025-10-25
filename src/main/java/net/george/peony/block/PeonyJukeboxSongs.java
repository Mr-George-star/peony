package net.george.peony.block;

import net.george.peony.Peony;
import net.george.peony.sound.PeonySoundEvents;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class PeonyJukeboxSongs {
    public static final RegistryKey<JukeboxSong> SURPRISE_KEY = key("surprise");

    public static void register(Registerable<JukeboxSong> context, RegistryKey<JukeboxSong> key,
                                RegistryEntry.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        context.register(key, new JukeboxSong(soundEvent,
                Text.translatable(Util.createTranslationKey("jukebox_song", key.getValue())),
                (float) lengthInSeconds, comparatorOutput));
    }

    public static RegistryKey<JukeboxSong> key(String name) {
        return Peony.key(RegistryKeys.JUKEBOX_SONG, name);
    }

    public static void boostrap(Registerable<JukeboxSong> context) {
        register(context, SURPRISE_KEY, PeonySoundEvents.MUSIC_DISC_SURPRISE, 212, 15);
    }
}
