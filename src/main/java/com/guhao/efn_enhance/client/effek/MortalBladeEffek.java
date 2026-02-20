package com.guhao.efn_enhance.client.effek;

import com.guhao.efn_enhance.EFN_E;
import com.hm.efn.particle.EFNEffekParticle;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Random;

@SuppressWarnings("removal")
public class MortalBladeEffek {
    public record Type(
            ResourceLocation effekId,
            float intrinsicRadius
    ) {
        public static final MortalBladeEffek.Type LEVEL1 = new MortalBladeEffek.Type(MORTALBLADEEFFEK, 2.5f);
        public static final MortalBladeEffek.Type LEVEL2 = new MortalBladeEffek.Type(MORTALBLADEEFFEK, 1.0f);
        public static final MortalBladeEffek.Type LEVEL3 = new MortalBladeEffek.Type(MORTALBLADEEFFEK, 0.8f);
    }


    public static final ResourceLocation MORTALBLADEEFFEK = new ResourceLocation(EFN_E.MODID, "mortal_blade");



    public static void playMortalBlade(MortalBladeEffek.Type type, Level level, double x, double y, double z, float radius) {
        Random random = new Random();
        var info = ParticleEmitterInfo.create(level, type.effekId())
                .position(x, y, z)
                .rotation(0,random.nextFloat(-90.0f,90.0f),0)
                .scale(radius / type.intrinsicRadius());
        AAALevel.addParticle(level, true, info);

    }

    public static Particle createParticleWrapper(
            MortalBladeEffek.Type type, ClientLevel level,
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