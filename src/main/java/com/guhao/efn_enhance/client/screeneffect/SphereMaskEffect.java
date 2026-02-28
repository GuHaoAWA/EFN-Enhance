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
import org.joml.Vector3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class SphereMaskEffect extends ScreenEffectBase {
    static ResourceLocation sphere_effect = OjangUtils.newRL(EFN_E.MODID, "sphere_effect");
    public final SE_Pipeline ppl;

    private Vec3 sphereCenter;
    private float sphereRadius;

    public SphereMaskEffect(Vec3 center, float radius) {
        super(sphere_effect, center);
        this.sphereCenter = center;
        this.sphereRadius = radius;
        this.ppl = new SphereMaskPipeline(this);
        this.lifetime = 75;
    }

    @Override
    public boolean shouldPost(Camera camera, Frustum clippingHelper) {
        super.shouldPost(camera,clippingHelper);
        LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getLocalPlayerPatch(Minecraft.getInstance().player);
        if (localPlayerPatch == null || localPlayerPatch.getAnimator().getPlayerFor(null).getAnimation() == null) return false;
        if (localPlayerPatch.getAnimator().getPlayerFor(null).getAnimation() == EFN_EBroadBladeAnimations.BROADBLADE_EXECUTED) return true;
        return localPlayerPatch.getAnimator().getPlayerFor(null).getAnimation() == EFN_EBroadBladeAnimations.BROADBLADE_EXECUTE;
    }

    @Override
    public PostEffectPipelines.Pipeline getPipeline() {
        return ppl;
    }

    public static class SphereMaskPipeline extends SE_Pipeline<SphereMaskEffect> {
        static ResourceLocation temp_main = OjangUtils.newRL(EFN_E.MODID, "temp_main");
        static ResourceLocation temp_mask = OjangUtils.newRL(EFN_E.MODID, "temp_mask");

        public SphereMaskPipeline(SphereMaskEffect effect) {
            super(sphere_effect, effect);
            priority = 9999;
        }

        @Override
        public void PostEffectHandler() {
            Minecraft mc = Minecraft.getInstance();
            RenderTarget mainTarget = mc.getMainRenderTarget();

            RenderTarget tempMain = TargetManager.getTarget(temp_main);
            RenderTarget tempMask = TargetManager.getTarget(temp_mask);

            tempMain.clear(Minecraft.ON_OSX);
            tempMask.clear(Minecraft.ON_OSX);

            // 复制当前帧
            PostPasses.blit.process(mainTarget, tempMain);

            // 生成球体遮罩
            EFNEPostPasses.sphere_mask_generator.process(
                    PostEffectPipelines.depth,
                    tempMask,
                    new Vector3f((float)effect.sphereCenter.x, (float)effect.sphereCenter.y, (float)effect.sphereCenter.z),
                    effect.sphereRadius
            );


            float progress = effect.getNormalizedAgeWithPartialTicks();

            float fadeInEnd = 0.1f;
            float fadeOutStart = 0.95f;
            float intensity;

            if (progress < fadeInEnd) {
                float t = progress / fadeInEnd;
                intensity = t * t * t;
            } else if (progress > fadeOutStart) {
                float t = (progress - fadeOutStart) / (1.0f - fadeOutStart);
                intensity = 1.0f - t * t * t;
            } else {
                intensity = 1.0f;
            }

            Vector3f bgColor = new Vector3f(intensity, 0.0f, 0.0f);

            EFNEPostPasses.sphere_mask_composite.process(
                    tempMain,
                    tempMask,
                    mainTarget,
                    bgColor
            );
        }
    }
}