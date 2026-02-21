package com.guhao.efn_enhance.mixin;

import com.guhao.efn_enhance.register.EFNEEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow
    public abstract boolean hasEffect(MobEffect var1);

    @Inject(
            method = {"canAttack*"},
            remap = true,
            at = {@At("HEAD")},
            cancellable = true
    )
    public void CMcanAttack(LivingEntity p_21171_, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(EFNEEffects.THUNDER.get())) {
            cir.setReturnValue(false);
        }

    }
}
