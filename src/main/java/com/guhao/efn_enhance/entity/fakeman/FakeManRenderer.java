package com.guhao.efn_enhance.entity.fakeman;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.PatchedElytraLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeadLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class FakeManRenderer extends PatchedLivingEntityRenderer<FakeManEntity, LivingEntityPatch<FakeManEntity>, HumanoidModel<FakeManEntity>, LivingEntityRenderer<FakeManEntity, HumanoidModel<FakeManEntity>>, HumanoidMesh> {

    private final AssetAccessor<HumanoidMesh> mesh;

    public FakeManRenderer(AssetAccessor<HumanoidMesh> mesh, EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(context, entityType);
        this.mesh = mesh;

        this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer<>());
        this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
        this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(mesh, false, context.getModelManager()));
        this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
    }

    @Override
    public void render(FakeManEntity entity, LivingEntityPatch<FakeManEntity> entitypatch,
                       LivingEntityRenderer<FakeManEntity, HumanoidModel<FakeManEntity>> renderer,
                       MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {

        Minecraft mc = Minecraft.getInstance();
        Armature armature = entitypatch.getArmature();

        if (armature == null) {
            return;
        }

        HumanoidMesh mesh = this.mesh.get();
        if (mesh == null) {
            return;
        }

        poseStack.pushPose();

        this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
        this.setArmaturePose(entitypatch, armature, partialTicks);

        ResourceLocation baseTexture = new ResourceLocation("efn:textures/entity/doppelganger.png");
        mesh.draw(poseStack, buffer, RenderType.entityTranslucent(baseTexture), packedLight,
                1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY,
                entitypatch.getArmature(), armature.getPoseMatrices());

        this.renderLayer(renderer, entitypatch, entity, armature.getPoseMatrices(),
                buffer, poseStack, packedLight, partialTicks);

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
        }

        poseStack.popPose();
    }

    @Override
    public AssetAccessor<HumanoidMesh> getDefaultMesh() {
        return this.mesh;
    }

    public void setJointTransforms(LivingEntityPatch<FakeManEntity> entitypatch, Armature armature, Pose pose, float partialTicks) {
        if (entitypatch.getOriginal().isBaby()) {
            pose.orElseEmpty("Head").frontResult(JointTransform.scale(new Vec3f(1.25F, 1.25F, 1.25F)), OpenMatrix4f::mul);
        }
    }

    protected float getDefaultLayerHeightCorrection() {
        return 0.75F;
    }
}