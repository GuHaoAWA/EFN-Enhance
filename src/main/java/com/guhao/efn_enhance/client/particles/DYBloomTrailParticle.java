package com.guhao.efn_enhance.client.particles;

import com.google.common.collect.Lists;
import com.guhao.fancy_trail.client.pipeline.PostEffectPipelines;
import com.guhao.fancy_trail.client.render.custom.BloomParticleRenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.physics.bezier.CubicBezierCurve;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.AbstractTrailParticle;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class DYBloomTrailParticle extends AbstractTrailParticle<LivingEntityPatch<?>> {

    private final Joint joint;
    private final AssetAccessor<? extends StaticAnimation> animation;
    private final List<TrailEdge> invisibleTrailEdges;
    private final List<ResourceLocation> textures;
    private final float frameInterval;
    private float startEdgeCorrection = 0.0F;

    private static final DynamicBloomRenderType DYNAMIC_BLOOM_TYPE = new DynamicBloomRenderType();

    protected DYBloomTrailParticle(ClientLevel level, LivingEntityPatch<?> owner, Joint joint,
                                   AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo,
                                   List<ResourceLocation> textures) {
        super(level, owner, trailInfo);
        this.joint = joint;
        this.animation = animation;
        this.textures = textures;
        this.invisibleTrailEdges = Lists.newLinkedList();
        this.frameInterval = 0.05F;

        Pose prevPose = owner.getAnimator().getPose(0.0F);
        Pose middlePose = owner.getAnimator().getPose(0.5F);
        Pose currentPose = owner.getAnimator().getPose(1.0F);

        Vec3 posOld = owner.getOriginal().getPosition(0.0F);
        Vec3 posMid = owner.getOriginal().getPosition(0.5F);
        Vec3 posCur = owner.getOriginal().getPosition(1.0F);

        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z)
                .rotateDeg(180.0F, Vec3f.Y_AXIS).mulBack(owner.getModelMatrix(0.0F));
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
                .rotateDeg(180.0F, Vec3f.Y_AXIS).mulBack(owner.getModelMatrix(0.5F));
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
                .rotateDeg(180.0F, Vec3f.Y_AXIS).mulBack(owner.getModelMatrix(1.0F));

        OpenMatrix4f prevJointTf = owner.getArmature().getBoundTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = owner.getArmature().getBoundTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = owner.getArmature().getBoundTransformFor(currentPose, this.joint).mulFront(curModelTf);

        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, trailInfo.start());
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, trailInfo.end());
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, trailInfo.start());
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, trailInfo.end());
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, trailInfo.start());
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, trailInfo.end());

        this.invisibleTrailEdges.add(new TrailEdge(prevStartPos, prevEndPos, this.trailInfo.trailLifetime()));
        this.invisibleTrailEdges.add(new TrailEdge(middleStartPos, middleEndPos, this.trailInfo.trailLifetime()));
        this.invisibleTrailEdges.add(new TrailEdge(currentStartPos, currentEndPos, this.trailInfo.trailLifetime()));

        this.rCol = Math.max(this.trailInfo.rCol(), 0.0F);
        this.gCol = Math.max(this.trailInfo.gCol(), 0.0F);
        this.bCol = Math.max(this.trailInfo.bCol(), 0.0F);
    }

    @Override
    protected boolean canContinue() {
        yesman.epicfight.api.animation.AnimationPlayer animPlayer = this.owner.getAnimator().getPlayerFor(this.animation);
        return this.owner.getOriginal().isAlive() && this.animation == animPlayer.getRealAnimation() &&
                animPlayer.getElapsedTime() <= this.trailInfo.endTime();
    }

    @Override
    protected boolean canCreateNextCurve() {
        yesman.epicfight.api.animation.AnimationPlayer animPlayer = this.owner.getAnimator().getPlayerFor(this.animation);
        return (!TrailInfo.isValidTime(this.trailInfo.fadeTime()) || !(this.trailInfo.endTime() < animPlayer.getElapsedTime())) && super.canCreateNextCurve();
    }

    @Override
    protected void createNextCurve() {
        yesman.epicfight.api.animation.AnimationPlayer animPlayer = this.owner.getAnimator().getPlayerFor(this.animation);
        boolean isTrailInvisible = animPlayer.getAnimation().get().isLinkAnimation() || animPlayer.getElapsedTime() <= this.trailInfo.startTime();
        boolean isFirstTrail = this.trailEdges.isEmpty();
        boolean needCorrection = !isTrailInvisible && isFirstTrail;

        if (needCorrection) {
            float startCorrection = Math.max((this.trailInfo.startTime() - animPlayer.getPrevElapsedTime()) /
                    (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()), 0.0F);
            this.startEdgeCorrection = (float)(this.trailInfo.interpolateCount() * 2) * startCorrection;
        }

        Pose prevPose = this.owner.getAnimator().getPose(0.0F);
        Pose currentPose = this.owner.getAnimator().getPose(1.0F);
        Pose middlePose = this.owner.getAnimator().getPose(0.5F);

        Vec3 posOld = this.owner.getOriginal().getPosition(0.0F);
        Vec3 posCur = this.owner.getOriginal().getPosition(1.0F);
        Vec3 posMid = MathUtils.lerpVector(posOld, posCur, 0.5F);

        OpenMatrix4f prevModelMatrix = this.owner.getModelMatrix(0.0F);
        OpenMatrix4f curModelMatrix = this.owner.getModelMatrix(1.0F);

        JointTransform lastTransform = JointTransform.fromMatrix(curModelMatrix);
        JointTransform currentTransform = JointTransform.fromMatrix(curModelMatrix);

        OpenMatrix4f prvmodelTf = OpenMatrix4f.createTranslation((float)posOld.x, (float)posOld.y, (float)posOld.z)
                .rotateDeg(180.0F, Vec3f.Y_AXIS).mulBack(prevModelMatrix);
        OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
                .rotateDeg(180.0F, Vec3f.Y_AXIS).mulBack(JointTransform.interpolate(lastTransform, currentTransform, 0.5F).toMatrix());
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
                .rotateDeg(180.0F, Vec3f.Y_AXIS).mulBack(curModelMatrix);

        OpenMatrix4f prevJointTf = this.owner.getArmature().getBoundTransformFor(prevPose, this.joint).mulFront(prvmodelTf);
        OpenMatrix4f middleJointTf = this.owner.getArmature().getBoundTransformFor(middlePose, this.joint).mulFront(middleModelTf);
        OpenMatrix4f currentJointTf = this.owner.getArmature().getBoundTransformFor(currentPose, this.joint).mulFront(curModelTf);

        Vec3 prevStartPos = OpenMatrix4f.transform(prevJointTf, this.trailInfo.start());
        Vec3 prevEndPos = OpenMatrix4f.transform(prevJointTf, this.trailInfo.end());
        Vec3 middleStartPos = OpenMatrix4f.transform(middleJointTf, this.trailInfo.start());
        Vec3 middleEndPos = OpenMatrix4f.transform(middleJointTf, this.trailInfo.end());
        Vec3 currentStartPos = OpenMatrix4f.transform(currentJointTf, this.trailInfo.start());
        Vec3 currentEndPos = OpenMatrix4f.transform(currentJointTf, this.trailInfo.end());

        List<Vec3> finalStartPositions;
        List<Vec3> finalEndPositions;
        List<TrailEdge> dest;

        if (isTrailInvisible) {
            finalStartPositions = Lists.newArrayList();
            finalEndPositions = Lists.newArrayList();
            finalStartPositions.add(prevStartPos);
            finalStartPositions.add(middleStartPos);
            finalEndPositions.add(prevEndPos);
            finalEndPositions.add(middleEndPos);
            this.invisibleTrailEdges.clear();
            dest = this.invisibleTrailEdges;
        } else {
            List<Vec3> startPosList = Lists.newArrayList();
            List<Vec3> endPosList = Lists.newArrayList();

            TrailEdge edge1;
            TrailEdge edge2;

            if (isFirstTrail) {
                int lastIdx = this.invisibleTrailEdges.size() - 1;
                edge1 = this.invisibleTrailEdges.get(lastIdx);
                edge2 = new TrailEdge(prevStartPos, prevEndPos, -1);
            } else {
                edge1 = this.trailEdges.get(this.trailEdges.size() - (this.trailInfo.interpolateCount() / 2 + 1));
                edge2 = this.trailEdges.get(this.trailEdges.size() - 1);
                ++edge2.lifetime;
            }

            startPosList.add(edge1.start);
            endPosList.add(edge1.end);
            startPosList.add(edge2.start);
            endPosList.add(edge2.end);
            startPosList.add(middleStartPos);
            endPosList.add(middleEndPos);
            startPosList.add(currentStartPos);
            endPosList.add(currentEndPos);

            finalStartPositions = CubicBezierCurve.getBezierInterpolatedPoints(startPosList, 1, 3, this.trailInfo.interpolateCount());
            finalEndPositions = CubicBezierCurve.getBezierInterpolatedPoints(endPosList, 1, 3, this.trailInfo.interpolateCount());

            if (!isFirstTrail) {
                finalStartPositions.remove(0);
                finalEndPositions.remove(0);
            }

            dest = this.trailEdges;
        }

        this.makeTrailEdges(finalStartPositions, finalEndPositions, dest);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        if (this.trailEdges.isEmpty() || textures == null || textures.isEmpty()) {
            return;
        }

        if (PostEffectPipelines.isActive()) {
            // 计算当前纹理
            float totalTime = (this.age + partialTick) * this.frameInterval;
            int textureIndex = (int)(totalTime / this.frameInterval) % textures.size();
            ResourceLocation currentTexture = textures.get(textureIndex);

            // 设置当前纹理到动态渲染类型
            DYNAMIC_BLOOM_TYPE.setCurrentTexture(currentTexture);

            // 调用pipeline
            DYNAMIC_BLOOM_TYPE.callPipeline();

            // 获取相机位置
            Vec3 cameraPos = camera.getPosition();

            // 计算粒子位置（相对于相机）
            float x = (float)(Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
            float y = (float)(Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
            float z = (float)(Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

            // 渲染trail edges
            int edges = this.trailEdges.size() - 1;
            boolean startFade = this.trailEdges.get(0).lifetime == 1;
            boolean endFade = this.trailEdges.get(edges).lifetime == this.trailInfo.trailLifetime();

            float startEdge = (startFade ? (float)(this.trailInfo.interpolateCount() * 2) * partialTick : 0.0F) + this.startEdgeCorrection;
            float endEdge = endFade ? Math.min((float)edges - (float)(this.trailInfo.interpolateCount() * 2) * (1.0F - partialTick), (float)(edges - 1)) : (float)(edges - 1);

            float interval = 1.0F / (endEdge - startEdge);
            float fading = 1.0F;

            if (this.shouldRemove) {
                if (TrailInfo.isValidTime(this.trailInfo.fadeTime())) {
                    fading = (float)(this.lifetime - this.age) / (float)this.trailInfo.trailLifetime();
                } else {
                    fading = Mth.clamp(((float)(this.lifetime - this.age) + (1.0F - partialTick)) / (float)this.trailInfo.trailLifetime(), 0.0F, 1.0F);
                }
            }

            float partialStartEdge = interval * (startEdge % 1.0F);
            float from = -partialStartEdge;
            float to = -partialStartEdge + interval;

            int light = this.getLightColor(partialTick);

            for(int i = (int)startEdge; i < (int)endEdge + 1; ++i) {
                TrailEdge e1 = this.trailEdges.get(i);
                TrailEdge e2 = this.trailEdges.get(i + 1);

                Vector4f pos1 = new Vector4f((float)(e1.start.x - cameraPos.x()), (float)(e1.start.y - cameraPos.y()), (float)(e1.start.z - cameraPos.z()), 1.0F);
                Vector4f pos2 = new Vector4f((float)(e1.end.x - cameraPos.x()), (float)(e1.end.y - cameraPos.y()), (float)(e1.end.z - cameraPos.z()), 1.0F);
                Vector4f pos3 = new Vector4f((float)(e2.end.x - cameraPos.x()), (float)(e2.end.y - cameraPos.y()), (float)(e2.end.z - cameraPos.z()), 1.0F);
                Vector4f pos4 = new Vector4f((float)(e2.start.x - cameraPos.x()), (float)(e2.start.y - cameraPos.y()), (float)(e2.start.z - cameraPos.z()), 1.0F);

                float alphaFrom = Mth.clamp(from, 0.0F, 1.0F);
                float alphaTo = Mth.clamp(to, 0.0F, 1.0F);

                float r = this.rCol;
                float g = this.gCol;
                float b = this.bCol;
                float a = this.alpha * fading;

                // 确保每个顶点都有完整的数据：位置、颜色、纹理坐标、光照
                vertexConsumer.vertex(pos1.x(), pos1.y(), pos1.z())
                        .color(r, g, b, a * alphaFrom)
                        .uv(from, 1.0F)
                        .uv2(light)
                        .endVertex();

                vertexConsumer.vertex(pos2.x(), pos2.y(), pos2.z())
                        .color(r, g, b, a * alphaFrom)
                        .uv(from, 0.0F)
                        .uv2(light)
                        .endVertex();

                vertexConsumer.vertex(pos3.x(), pos3.y(), pos3.z())
                        .color(r, g, b, a * alphaTo)
                        .uv(to, 0.0F)
                        .uv2(light)
                        .endVertex();

                vertexConsumer.vertex(pos4.x(), pos4.y(), pos4.z())
                        .color(r, g, b, a * alphaTo)
                        .uv(to, 1.0F)
                        .uv2(light)
                        .endVertex();

                from += interval;
                to += interval;
            }
        }
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return DYNAMIC_BLOOM_TYPE;
    }

    private static class DynamicBloomRenderType extends BloomParticleRenderType {
        private ResourceLocation currentTexture;

        public DynamicBloomRenderType() {
            super(new ResourceLocation("efn_enhance", "dynamic_bloom"), null);
        }

        public void setCurrentTexture(ResourceLocation texture) {
            this.currentTexture = texture;
        }

        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            if (currentTexture != null) {
                RenderSystem.setShaderTexture(0, currentTexture);
            }
            super.begin(builder, textureManager);
        }

        @Override
        public PostEffectPipelines.Pipeline getPipeline() {
            return BloomParticleRenderType.ppl;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private static final Map<Integer, List<ResourceLocation>> TEXTURE_SEQUENCES = new HashMap<>();

        static {
            TEXTURE_SEQUENCES.put(0, Arrays.asList(
                    new ResourceLocation("efn_enhance:textures/particle/thunder1.png"),
                    new ResourceLocation("efn_enhance:textures/particle/thunder2.png"),
                    new ResourceLocation("efn_enhance:textures/particle/thunder3.png"),
                    new ResourceLocation("efn_enhance:textures/particle/thunder4.png"),
                    new ResourceLocation("efn_enhance:textures/particle/thunder5.png"),
                    new ResourceLocation("efn_enhance:textures/particle/thunder6.png"),
                    new ResourceLocation("efn_enhance:textures/particle/thunder7.png")
            ));
        }

        public Provider(SpriteSet spriteSet) {
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType typeIn, ClientLevel level,
                                       double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            int eid = (int) Double.doubleToRawLongBits(x);
            int animid = (int) Double.doubleToRawLongBits(z);
            int jointId = (int) Double.doubleToRawLongBits(xSpeed);
            int idx = (int) Double.doubleToRawLongBits(ySpeed);
            int textureSequenceId = (int) Double.doubleToRawLongBits(zSpeed);

            Entity entity = level.getEntity(eid);
            if (entity == null) return null;

            LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
            if (entitypatch == null) return null;

            AnimationManager.AnimationAccessor<? extends StaticAnimation> animation = AnimationManager.byId(animid);
            if (animation == null) return null;

            Optional<List<TrailInfo>> trailInfo = animation.get().getProperty(ClientAnimationProperties.TRAIL_EFFECT);
            if (trailInfo.isEmpty()) return null;

            TrailInfo result = trailInfo.get().get(idx);

            if (result.hand() != null) {
                ItemStack stack = entitypatch.getOriginal().getItemInHand(result.hand());
                RenderItemBase renderItemBase = ClientEngine.getInstance().renderEngine.getItemRenderer(stack);
                if (renderItemBase != null && renderItemBase.trailInfo() != null) {
                    result = renderItemBase.trailInfo().overwrite(result);
                }
            }

            if (result.playable()) {
                List<ResourceLocation> textures = TEXTURE_SEQUENCES.getOrDefault(textureSequenceId,
                        Collections.singletonList(result.texturePath()));

                return new DYBloomTrailParticle(level, entitypatch,
                        entitypatch.getArmature().searchJointById(jointId),
                        animation, result, textures);
            }
            return null;
        }
    }
}