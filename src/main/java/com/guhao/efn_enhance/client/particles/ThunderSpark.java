package com.guhao.efn_enhance.client.particles;

import com.hm.efn.client.particle.rendertype.EFNHitParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ThunderSpark extends EFNHitParticle {
    public ThunderSpark(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
        super(world, x, y, z, animatedSprite);
        this.lifetime = 8;
        Random rand = new Random();
        float angle = (float)Math.toRadians(60.0F + (rand.nextFloat() - 0.5F) * 60.0F + (rand.nextBoolean() ? 0.0F : 180.0F));
        this.oRoll = angle;
        this.roll = angle;
        this.quadSize = 0.12F * rand.nextFloat(1.0f,2.0f);
    }
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ThunderSpark(worldIn, x, y, z, spriteSet);
        }
    }
}
