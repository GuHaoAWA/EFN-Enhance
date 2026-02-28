package com.guhao.efn_enhance.client.shaderpasses;

import com.hm.efn.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Vector3f;

import java.io.IOException;

import static com.hm.efn.client.pipeline.PostEffectPipelines.shaderOrthoMatrix;

public class SphereMaskComposite extends PostPassBase {

    public SphereMaskComposite(String resourceLocation, ResourceManager resmgr) throws IOException {
        super(resourceLocation, resmgr);
    }

    public void process(RenderTarget mainTarget,
                        RenderTarget maskTarget,
                        RenderTarget outTarget,
                        Vector3f backgroundColor) {

        prevProcess(mainTarget, outTarget);
        mainTarget.unbindWrite();

        RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);

        // 设置采样器
        this.effect.setSampler("DiffuseSampler", mainTarget::getColorTextureId);
        this.effect.setSampler("MaskSampler", maskTarget::getColorTextureId);

        // 基本uniforms
        this.effect.safeGetUniform("ProjMat").set(shaderOrthoMatrix);
        this.effect.safeGetUniform("OutSize").set((float) outTarget.width, (float) outTarget.height);
        this.effect.safeGetUniform("BackgroundColor").set(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f);

        this.effect.apply();
        pushVertex(mainTarget, outTarget);
        this.effect.clear();

        outTarget.unbindWrite();
        mainTarget.unbindRead();
        maskTarget.unbindRead();
    }
}