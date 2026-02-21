package com.guhao.efn_enhance.client;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.client.particles.DYBloomTrailParticle;
import com.guhao.efn_enhance.client.particles.EFNEParticles;
import com.guhao.efn_enhance.client.particles.SparkHit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EFN_E.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EFNEParticles.SPARK_HIT.get(), SparkHit.Provider::new);
        event.registerSpriteSet(EFNEParticles.DY_BLOOM_TRAIL.get(), DYBloomTrailParticle.Provider::new);
    }
}
