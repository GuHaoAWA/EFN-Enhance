package com.guhao.efn_enhance.effects;

import com.guhao.efn_enhance.EFN_E;
import com.hm.efn.particle.EFNEffekParticle;
import com.hm.efn.particle.EFNParticles;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Random;

@SuppressWarnings("removal")
public class SparkBaEffek {
    public record Type(
            ResourceLocation effekId,
            float intrinsicRadius
    ) {
        public static final SparkBaEffek.Type LEVEL1 = new SparkBaEffek.Type(SPARKBAEFFEK, 2.0f);
        public static final SparkBaEffek.Type LEVEL2 = new SparkBaEffek.Type(SPARKBAEFFEK, 1.0f);
        public static final SparkBaEffek.Type LEVEL3 = new SparkBaEffek.Type(SPARKBAEFFEK, 0.36f);
    }


    public static final ResourceLocation SPARKBAEFFEK = new ResourceLocation(EFN_E.MODID, "spark_ba");



    public static void playSparkBa(SparkBaEffek.Type type, Level level, double x, double y, double z, float radius) {
        Random random = new Random();
        var info = ParticleEmitterInfo.create(level, type.effekId())
                .position(x, y, z)
                .rotation(0,random.nextFloat(-90.0f,90.0f),0)
                .scale(radius / type.intrinsicRadius());
        AAALevel.addParticle(level, true, info);
        level.addAlwaysVisibleParticle(EFNParticles.TRIGGER.get(),x,y,z,0,0,0);

    }

    public static Particle createParticleWrapper(
            SparkBaEffek.Type type, ClientLevel level,
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