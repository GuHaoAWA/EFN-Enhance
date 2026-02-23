package com.guhao.efn_enhance.gameassets.type;


import com.guhao.efn_enhance.gameassets.animations.EFN_EBroadBladeAnimations;
import com.hm.efn.mixin.ArmaturesAccessor;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExecuteAttackAnimation extends AttackAnimation {
    private final float fixDistance;

    public ExecuteAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float fixDistance) {
        super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
        this.fixDistance = fixDistance;
    }

    public ExecuteAttackAnimation(float transitionTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, AnimationManager.AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float fixDistance) {
        super(transitionTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
        this.fixDistance = fixDistance;
    }

    public ExecuteAttackAnimation(float transitionTime, AnimationManager.AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, float fixDistance, AttackAnimation.Phase... phases) {
        super(transitionTime, accessor, armature, phases);
        this.fixDistance = fixDistance;
    }

    public ExecuteAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, AssetAccessor<? extends Armature> armature, float fixDistance) {
        super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
        this.fixDistance = fixDistance;
    }

    public ExecuteAttackAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature, float fixDistance, AttackAnimation.Phase... phases) {
        super(convertTime, path, armature, phases);
        this.fixDistance = fixDistance;
    }

    protected void hurtCollidingEntities(LivingEntityPatch<?> entityPatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, AttackAnimation.Phase phase) {
        LivingEntity entity = entityPatch.getOriginal();
        float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
        float poseTime = state.attacking() ? elapsedTime : phase.contact;
        List<Entity> list = this.getPhaseByTime(elapsedTime).getCollidingEntities(entityPatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entityPatch, this));
        if (!list.isEmpty()) {
            HitEntityList hitEntities = new HitEntityList(entityPatch, list, phase.getProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY).orElse(HitEntityList.Priority.DISTANCE));
            int maxStrikes = this.getMaxStrikes(entityPatch, phase);

            while(true) {
                Entity target;
                LivingEntity trueEntity;
                do {
                    do {
                        do {
                            do {
                                do {
                                    if (entityPatch.getCurrentlyActuallyHitEntities().size() >= maxStrikes || !hitEntities.next()) {
                                        return;
                                    }

                                    target = hitEntities.getEntity();
                                    trueEntity = this.getTrueEntity(target);
                                } while(trueEntity == null);
                            } while(!trueEntity.isAlive());
                        } while(entityPatch.getCurrentlyAttackTriedEntities().contains(trueEntity));
                    } while(entityPatch.isTargetInvulnerable(target));
                } while(!(target instanceof LivingEntity) && !(target instanceof PartEntity));

                if (entity.hasLineOfSight(target)) {
                    EpicFightDamageSource damageSource = this.getEpicFightDamageSource(entityPatch, target, phase);
                    int invulnerableTime = target.invulnerableTime;
                    target.invulnerableTime = 0;
                    AttackResult attackResult = entityPatch.attack(damageSource, target, phase.hand);
                    target.invulnerableTime = invulnerableTime;
                    if (attackResult.resultType.dealtDamage()) {
                        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), this.getHitSound(entityPatch, phase), target.getSoundSource(), 1.0F, 1.0F);
                        Vec3 attackerPos = entityPatch.getOriginal().position();
                        if (!(trueEntity instanceof Player)) {
                            trueEntity.lookAt(EntityAnchorArgument.Anchor.FEET, attackerPos);
                        }

                        this.spawnHitParticle((ServerLevel)target.level(), entityPatch, target, phase);
                        this.onHit(entityPatch, target);
                    }

                    entityPatch.getCurrentlyAttackTriedEntities().add(trueEntity);
                    if (attackResult.resultType.shouldCount()) {
                        entityPatch.getCurrentlyActuallyHitEntities().add(trueEntity);
                    }
                }
            }
        }
    }

    private void onHit(LivingEntityPatch<?> entityPatch, Entity target) {
        LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>) EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
        int phase = this.getPhaseOrderByTime(Objects.requireNonNull(entityPatch.getAnimator().getPlayerFor(this.getAccessor())).getElapsedTime());
        if (phase == 0) {
            if (targetPatch != null) {
                if (targetPatch.isStunned() && target.isAlive()) {
                    this.fixTargetMotion(entityPatch, targetPatch);
                    targetPatch.playAnimationSynchronized(EFN_EBroadBladeAnimations.BROADBLADE_EXECUTED, -0.1f);
                }
            }
        }
    }

    private void fixTargetMotion(LivingEntityPatch<?> entityPatch, LivingEntityPatch<?> targetPatch) {
        Vec3 pos = calculateFrontPosition(entityPatch.getOriginal(), this.fixDistance);
        targetPatch.getOriginal().teleportTo(pos.x, pos.y, pos.z);
    }

    private static Vec3 calculateFrontPosition(LivingEntity attacker, double distance) {
        float yaw = attacker.getViewYRot(0.0F);
        double rad = Math.toRadians(yaw);
        double offsetX = -Math.sin(rad) * distance;
        double offsetZ = Math.cos(rad) * distance;
        return attacker.position().add(offsetX, 0.0, offsetZ);
    }
}