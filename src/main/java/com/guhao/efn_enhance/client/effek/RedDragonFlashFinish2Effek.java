package com.guhao.efn_enhance.client.effek;

import com.guhao.efn_enhance.EFN_E;
import com.hm.efn.particle.EFNEffekParticle;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

@SuppressWarnings("removal")
public class RedDragonFlashFinish2Effek {
    public record Type(
            ResourceLocation effekId,
            float intrinsicRadius
    ) {
        public static final RedDragonFlashFinish2Effek.Type LEVEL1 = new RedDragonFlashFinish2Effek.Type(RED_DRAGONFLASHFINISH_EFFEK, 1.0f);
    }


    public static final ResourceLocation RED_DRAGONFLASHFINISH_EFFEK = new ResourceLocation(EFN_E.MODID, "red_dragon_flash_finish_only");



    public static void playDragonFlashFinish(RedDragonFlashFinish2Effek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius) {
        var info = ParticleEmitterInfo.create(level, type.effekId())
                .position(x, y, z)
                .rotation(rx,ry,rz)
                .scale(radius / type.intrinsicRadius());
        AAALevel.addParticle(level, true, info);
    }

    public static Particle createParticleWrapper(
            RedDragonFlashFinish2Effek.Type type, ClientLevel level,
            double x, double y, double z,
            double dx, double dy, double dz,
            float radius
    ) {
        var scale = radius / type.intrinsicRadius();
        var particle = new EFNEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
        if (particle.getEmitter().isPresent()) {
            particle.getEmitter().get().setScale(scale, scale, scale);
        }
        return particle;
    }

}