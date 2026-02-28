package com.guhao.efn_enhance.client.shaderpasses;

import com.hm.efn.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

import static com.hm.efn.client.pipeline.PostEffectPipelines.shaderOrthoMatrix;

public class SphereMaskGenerator extends PostPassBase {

    public SphereMaskGenerator(String resourceLocation, ResourceManager resmgr) throws IOException {
        super(resourceLocation, resmgr);
    }

    public void process(RenderTarget depthTarget,
                        RenderTarget outTarget,
                        Vector3f sphereCenter,
                        float sphereRadius) {

        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();

        prevProcess(depthTarget, outTarget);
        depthTarget.unbindWrite();

        RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);

        // 设置采样器
        this.effect.setSampler("DepthSampler", depthTarget::getColorTextureId);

        // 设置基本uniforms
        this.effect.safeGetUniform("ProjMat").set(shaderOrthoMatrix);
        this.effect.safeGetUniform("OutSize").set((float) outTarget.width, (float) outTarget.height);

        // 设置球体参数
        this.effect.safeGetUniform("SphereCenter").set(sphereCenter.x, sphereCenter.y, sphereCenter.z);
        this.effect.safeGetUniform("SphereRadius").set(sphereRadius);

        // 设置相机位置
        Vec3 cameraPos = camera.getPosition();
        this.effect.safeGetUniform("CameraPos").set(
                (float)cameraPos.x,
                (float)cameraPos.y,
                (float)cameraPos.z
        );

        // 获取正确的投影矩阵
        Matrix4f projectionMatrix = mc.gameRenderer.getProjectionMatrix(mc.getFrameTime());
        Matrix4f inverseProj = new Matrix4f(projectionMatrix).invert();
        this.effect.safeGetUniform("InverseProjectionMatrix").set(inverseProj);

        // 构建视图矩阵
        Vector3f lookVec = camera.getLookVector();
        Vec3 upVec = new Vec3(0, 1, 0);

        // 计算视图矩阵：从相机位置看向视线方向
        Matrix4f viewMatrix = new Matrix4f().lookAt(
                new Vector3f((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z),
                new Vector3f((float)(cameraPos.x + lookVec.x),
                        (float)(cameraPos.y + lookVec.y),
                        (float)(cameraPos.z + lookVec.z)),
                new Vector3f((float)upVec.x, (float)upVec.y, (float)upVec.z)
        );

        Matrix4f inverseView = new Matrix4f(viewMatrix).invert();
        this.effect.safeGetUniform("InverseViewMatrix").set(inverseView);

        this.effect.apply();
        pushVertex(depthTarget, outTarget);
        this.effect.clear();

        outTarget.unbindWrite();
        depthTarget.unbindRead();
    }
}