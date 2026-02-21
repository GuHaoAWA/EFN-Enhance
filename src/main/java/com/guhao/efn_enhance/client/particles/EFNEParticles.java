package com.guhao.efn_enhance.client.particles;

import com.guhao.efn_enhance.EFN_E;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.particle.HitParticleType;

public class EFNEParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EFN_E.MODID);
    public static final RegistryObject<HitParticleType> SPARK_HIT = PARTICLES.register("spark_hit", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.CENTER_OF_TARGET));
    public static final RegistryObject<SimpleParticleType> DY_BLOOM_TRAIL = PARTICLES.register("dy_bloom_trail", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> THUNDER_SLICE = PARTICLES.register("thunder_slice", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> THUNDER_SPARK = PARTICLES.register("thunder_spark", () -> new SimpleParticleType(true));
}
