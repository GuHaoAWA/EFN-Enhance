package com.guhao.efn_enhance.register;


import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.effects.ThunderEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EFNEEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EFN_E.MODID);
    public static final RegistryObject<MobEffect> THUNDER = EFFECTS.register("thunder", ThunderEffect::new);
}
