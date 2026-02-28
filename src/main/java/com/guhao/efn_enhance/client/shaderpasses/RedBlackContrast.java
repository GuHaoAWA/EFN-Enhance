package com.guhao.efn_enhance.client.shaderpasses;

import com.hm.efn.client.pipeline.PostEffectPipelines;
import com.hm.efn.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

import static com.hm.efn.client.pipeline.PostEffectPipelines.shaderOrthoMatrix;

public class RedBlackContrast extends PostPassBase {

    public RedBlackContrast(String resourceLocation, ResourceManager resmgr) throws IOException {
        super(resourceLocation, resmgr);
    }

    public void process(RenderTarget inTarget, RenderTarget outTarget,
                        float intensity, float time, float redIntensity) {
        prevProcess(inTarget, outTarget);
        inTarget.unbindWrite();

        RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
        this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
        this.effect.setSampler("DepthSampler", PostEffectPipelines.depth::getColorTextureId);

        this.effect.safeGetUniform("ProjMat").set(shaderOrthoMatrix);
        this.effect.safeGetUniform("OutSize").set((float) outTarget.width, (float) outTarget.height);
        this.effect.safeGetUniform("Intensity").set(intensity);
        this.effect.safeGetUniform("Time").set(time);
        this.effect.safeGetUniform("RedIntensity").set(redIntensity);

        this.effect.apply();

        pushVertex(inTarget, outTarget);

        this.effect.clear();
        outTarget.unbindWrite();
        inTarget.unbindRead();
    }
    public void processColor(RenderTarget inTarget, RenderTarget outTarget,
                             float intensity, float skyHueMin, float skyHueMax,
                             float skySaturationMin, float skyValueMin) {
        prevProcess(inTarget, outTarget);
        inTarget.unbindWrite();

        RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
        this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
        // 注意：这里不需要 DepthSampler

        this.effect.safeGetUniform("ProjMat").set(shaderOrthoMatrix);
        this.effect.safeGetUniform("OutSize").set((float) outTarget.width, (float) outTarget.height);
        this.effect.safeGetUniform("Intensity").set(intensity);
        this.effect.safeGetUniform("SkyHueMin").set(skyHueMin);
        this.effect.safeGetUniform("SkyHueMax").set(skyHueMax);
        this.effect.safeGetUniform("SkySaturationMin").set(skySaturationMin);
        this.effect.safeGetUniform("SkyValueMin").set(skyValueMin);

        this.effect.apply();
        pushVertex(inTarget, outTarget);
        this.effect.clear();
        outTarget.unbindWrite();
        inTarget.unbindRead();
    }
}