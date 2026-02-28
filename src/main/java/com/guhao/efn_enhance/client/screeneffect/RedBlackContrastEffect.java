package com.guhao.efn_enhance.client.screeneffect;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.gameassets.animations.EFN_EBroadBladeAnimations;
import com.guhao.efn_enhance.register.EFNEPostPasses;
import com.hm.efn.client.pipeline.PostEffectPipelines;
import com.hm.efn.client.screeneffect.ScreenEffectBase;
import com.hm.efn.client.targets.TargetManager;
import com.hm.efn.registries.PostPasses;
import com.hm.efn.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class RedBlackContrastEffect extends ScreenEffectBase {
    static ResourceLocation red_black_contrast = OjangUtils.newRL(EFN_E.MODID, "red_black_contrast");
    public final SE_Pipeline ppl;

    public float intensity = 1.0f;        // 效果强度
    public float redIntensity = 1.2f;     // 红色强度
    public float pulseSpeed = 2.0f;       // 脉冲速度

    public RedBlackContrastEffect(Vec3 pos) {
        this(pos, 60, 1.0f, 1.2f, 2.0f);
    }
    public boolean shouldPost(Camera camera, Frustum clippingHelper) {
        super.shouldPost(camera,clippingHelper);
        LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getLocalPlayerPatch(Minecraft.getInstance().player);
        if (localPlayerPatch == null || localPlayerPatch.getAnimator().getPlayerFor(null).getAnimation() == null) return false;
        if (localPlayerPatch.getAnimator().getPlayerFor(null).getAnimation() == EFN_EBroadBladeAnimations.BROADBLADE_EXECUTED) return true;
        return localPlayerPatch.getAnimator().getPlayerFor(null).getAnimation() == EFN_EBroadBladeAnimations.BROADBLADE_EXECUTE;
    }
    public RedBlackContrastEffect(Vec3 pos, int lifetime, float intensity, float redIntensity, float pulseSpeed) {
        super(red_black_contrast, pos);
        this.ppl = new RedBlack_Pipeline(this);
        this.lifetime = lifetime;
        this.intensity = intensity;
        this.redIntensity = redIntensity;
        this.pulseSpeed = pulseSpeed;
    }

    @Override
    public PostEffectPipelines.Pipeline getPipeline() {
        return ppl;
    }

    public static class RedBlack_Pipeline extends SE_Pipeline<RedBlackContrastEffect> {
        static ResourceLocation temp_target = OjangUtils.newRL(EFN_E.MODID, "red_black_temp");

        public RedBlack_Pipeline(RedBlackContrastEffect effect) {
            super(red_black_contrast, effect);
            priority = 100; // 高优先级
        }

        @Override
        public void PostEffectHandler() {
            RenderTarget tmp = TargetManager.getTarget(temp_target);
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();

            PostPasses.blit.process(mainTarget, tmp);

            float normalizedAge = effect.getNormalizedAgeWithPartialTicks();
            float intensity = calculateIntensity(normalizedAge);
            float time = 0;

            EFNEPostPasses.red_black_contrast.process(
                    tmp,
                    mainTarget,
                    intensity,
                    time,
                    effect.redIntensity // 红色强度，如果着色器不用也可以忽略
            );
        }

        private float calculateIntensity(float progress) {
            return effect.intensity; // 始终不变
        }

//    /summon minecraft:zombie ~ ~ ~ {Attributes:[{Name:"generic.max_health",Base:9999999}],Health:99f}
    }
}