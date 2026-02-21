package com.guhao.efn_enhance.mixin;

import com.guhao.efn_enhance.entity.fakeman.FakeManEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin(value = ServerPlayerPatch.class, remap = false)
public class ServerPlayerPatchMixin {
    @Shadow
    private LivingEntity attackTarget;
    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        ServerPlayerPatch localPlayerPatch = (ServerPlayerPatch) (Object) this;
        if (attackTarget instanceof FakeManEntity fakeMan && fakeMan.getOwner().equals(localPlayerPatch.getOriginal())) {
            attackTarget = null;
        }
    }
}
