package com.guhao.efn_enhance.register;

import com.guhao.efn_enhance.EFN_E;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EFNESounds {
    public static final DeferredRegister<SoundEvent> EFNESound = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EFN_E.MODID);
    public static final RegistryObject<SoundEvent> BAZLEY = registerSound("bazley");
    private static RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(EFN_E.MODID, name);
        return EFNESound.register(name, () -> SoundEvent.createVariableRangeEvent(resourceLocation));
    }
}
