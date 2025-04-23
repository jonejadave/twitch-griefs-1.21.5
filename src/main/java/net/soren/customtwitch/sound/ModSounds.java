package net.soren.customtwitch.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.soren.customtwitch.TemplateMod;

public class ModSounds {

    public static final SoundEvent CHICKEN_JOCKEY_SPAWN = registerSoundEvent("chicken_jockey");

    private static SoundEvent registerSoundEvent(String id){
        Identifier identifier = Identifier.of(TemplateMod.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(identifier));
    }

    public static void initialize(){
        TemplateMod.LOGGER.info("Registering Sounds for " + TemplateMod.MOD_ID);
    }

}
