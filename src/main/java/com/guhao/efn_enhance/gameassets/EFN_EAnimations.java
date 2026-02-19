package com.guhao.efn_enhance.gameassets;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.gameassets.animations.EFN_ESekiroAnimations;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;

@SuppressWarnings("all")
@Mod.EventBusSubscriber(modid = EFN_E.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFN_EAnimations {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(EFN_E.MODID, EFN_EAnimations::build);

    }
    public static void build(AnimationManager.AnimationBuilder builder) {
        EFN_ESekiroAnimations.build(builder);
    }
}
