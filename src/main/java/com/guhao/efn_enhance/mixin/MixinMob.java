package com.guhao.efn_enhance.mixin;

import com.guhao.efn_enhance.entity.fakeman.FakeManEntity;
import com.guhao.efn_enhance.register.EFNEEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class)
public abstract class MixinMob {

    @Inject(at = @At(value = "HEAD"), method = "serverAiStep", cancellable = true)
    private void serverAiStep(CallbackInfo info) {
        Mob self = (Mob) ((Object) this);
        MobEffectInstance stopEffect = self.getEffect(EFNEEffects.THUNDER.get());
        if (stopEffect != null) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tickHeadTurn", cancellable = true)
    private void efn$tickHeadTurn(float p_21538_, float p_21539_, CallbackInfoReturnable<Float> callback) {
        Mob self = (Mob) ((Object) this);
        MobEffectInstance yamatoStunEffect = self.getEffect(EFNEEffects.THUNDER.get());

        if (yamatoStunEffect != null) {
            callback.setReturnValue(0.0F);
        }
    }

    @Inject(at = @At("HEAD"), method = "createBodyControl", cancellable = true)
    protected void efn$createBodyControl(CallbackInfoReturnable<BodyRotationControl> cir) {
        Mob self = (Mob) ((Object) this);
        MobEffectInstance yamatoStunEffect = self.getEffect(EFNEEffects.THUNDER.get());

        if (yamatoStunEffect != null) {
            cir.setReturnValue(null);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    protected void efn$tick(CallbackInfo ci) {
        Mob self = (Mob) ((Object) this);

        if (self.getTarget() == self || (self instanceof FakeManEntity fakeMan && self.getTarget() == fakeMan.getOwner())) {
            self.setTarget(null);
        }

    }

    @Inject(at = @At("HEAD"), method = "doHurtTarget", cancellable = true)
    private void efn$doHurtTarget(net.minecraft.world.entity.Entity target, CallbackInfoReturnable<Boolean> cir) {
        Mob self = (Mob) ((Object) this);
        MobEffectInstance yamatoStunEffect = self.getEffect(EFNEEffects.THUNDER.get());

        if (yamatoStunEffect != null) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
