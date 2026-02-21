package com.guhao.efn_enhance.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ThunderEffect extends MobEffect {
    public ThunderEffect() {
        super(MobEffectCategory.NEUTRAL, 16777215);
    }

    public boolean isDurationEffectTick(int duration, int lv) {
        return true;
    }

    public void applyEffectTick(LivingEntity owner, int lv) {
        owner.setDeltaMovement(Vec3.ZERO);
        owner.setPos(owner.xOld, owner.yOld, owner.zOld);
        owner.setSpeed(0.0F);
    }
}
