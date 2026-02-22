package com.guhao.efn_enhance.entity.fakeman;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("removal")
public class FakeManRender extends HumanoidMobRenderer<FakeManEntity, HumanoidModel<FakeManEntity>> {

    public FakeManRender(EntityRendererProvider.Context pContext) {
        super(pContext, new HumanoidModel<>(pContext.bakeLayer(ModelLayers.PLAYER)), 0);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                pContext.getModelManager()));

    }

    @Override
    protected RenderType getRenderType(@NotNull FakeManEntity entity, boolean visible, boolean invisibleToPlayer, boolean glowing) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }

    @Override
    public void render(FakeManEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
//        if (entity.isAlive()) {
//            float swing = entity.tickCount + partialTicks;
//            poseStack.pushPose();
//            poseStack.translate(0.0F, (float) Math.sin(swing * 0.1F) * 0.05F, 0.0F);
//            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//            poseStack.popPose();
//        } else {
//            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
//        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FakeManEntity fakeManEntity) {
        var owner = fakeManEntity.getOwner();
        if (owner != null) {
            if (owner instanceof AbstractClientPlayer clientPlayer) {
                return clientPlayer.getSkinTextureLocation();
            } else {
                return DefaultPlayerSkin.getDefaultSkin();
            }
        }

        return DefaultPlayerSkin.getDefaultSkin();
    }



}