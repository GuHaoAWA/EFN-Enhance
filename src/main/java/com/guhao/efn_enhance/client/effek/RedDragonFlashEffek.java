package com.guhao.efn_enhance.client.effek;

import com.guhao.efn_enhance.EFN_E;
import com.hm.efn.particle.EFNEffekParticle;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class RedDragonFlashEffek {
    public record Type(
            ResourceLocation effekId,
            float intrinsicRadius
    ) {
        public static final RedDragonFlashEffek.Type LEVEL1 = new RedDragonFlashEffek.Type(RED_DRAGONFLASH_EFFEK, 2.0f);
        public static final RedDragonFlashEffek.Type LEVEL2 = new RedDragonFlashEffek.Type(RED_DRAGONFLASH_EFFEK, 1.75f);
        public static final RedDragonFlashEffek.Type LEVEL3 = new RedDragonFlashEffek.Type(RED_DRAGONFLASH_EFFEK, 1.25f);
    }


    public static final ResourceLocation RED_DRAGONFLASH_EFFEK = new ResourceLocation(EFN_E.MODID, "red_dragon_flash");



    public static void playRedDragonFlash(RedDragonFlashEffek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius, Entity entity) {
        var info = ParticleEmitterInfo.create(level, type.effekId())
                .position(x, y, z)
                .bindOnEntity(entity)
                .rotation(rx,ry,rz)
                .scale(radius / type.intrinsicRadius());
        AAALevel.addParticle(level, true, info);
    }

    public static Particle createParticleWrapper(
            RedDragonFlashEffek.Type type, ClientLevel level,
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