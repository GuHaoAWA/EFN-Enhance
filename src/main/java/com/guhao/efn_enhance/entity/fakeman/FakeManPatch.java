package com.guhao.efn_enhance.entity.fakeman;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.guhao.efn_enhance.gameassets.animations.EFN_ESekiroAnimations;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@SuppressWarnings("removal")
public class FakeManPatch extends HumanoidMobPatch<FakeManEntity> {
    private @Nullable PlayerPatch<?> ownerPatch;

    public FakeManPatch() {
        super(Factions.UNDEAD);
    }

    @Override
    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        super.commonAggresiveMobAnimatorInit(animator);
    }

    @Override
    protected void setWeaponMotions() {
        super.setWeaponMotions();
//        this.weaponAttackMotions.put(CapabilityItem.WeaponCategories.UCHIGATANA, ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, MobCombatBehaviors.HUMANOID_KATANA));
        this.weaponLivingMotions = Maps.newHashMap();
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.UCHIGATANA,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, Set.of(
                        Pair.of(LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK),
                        Pair.of(LivingMotions.CHASE, EFNSwordAnimations.NF_SWORD_RUN),
                        Pair.of(LivingMotions.IDLE, EFNSekiroAnimations.KUSABIMARU_IDLE),
                        Pair.of(LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN))));
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        super.commonAggressiveRangedMobUpdateMotion(considerInaction);
    }

    @Override
    public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
        if (target == this.getOriginal().getOwner()) {
            return AttackResult.missed(0);
        }

        AttackResult attackResult;

        if (this.getOwnerPatch() != null && this.shouldUseOwnerAttack()) {
            EpicFightDamageSource modifiedSource = damageSource.addRuntimeTag(createUniqueAttackIdentifier(true));
            attackResult = this.getOwnerPatch().attack(modifiedSource, target, hand);
        } else {
            damageSource.addRuntimeTag(createUniqueAttackIdentifier(false));
            attackResult = super.attack(damageSource, target, hand);
        }

        setFakeManAttackResult(attackResult);

        return attackResult;
    }

    private void setFakeManAttackResult(AttackResult attackResult) {
        try {
            java.lang.reflect.Field resultTypeField = LivingEntityPatch.class.getDeclaredField("lastAttackResultType");
            java.lang.reflect.Field damageField = LivingEntityPatch.class.getDeclaredField("lastDealDamage");

            resultTypeField.setAccessible(true);
            damageField.setAccessible(true);

            resultTypeField.set(this, attackResult.resultType);
            damageField.set(this, attackResult.damage);

        } catch (Exception ignored) {

        }
    }


    @Override
    public EpicFightDamageSource getDamageSource(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
        if (this.getOwnerPatch() != null) {
            EpicFightDamageSource ownerSource = this.getOwnerPatch().getDamageSource(animation, hand);
            return ownerSource.addRuntimeTag(createUniqueAttackIdentifier(true));
        }
        return super.getDamageSource(animation, hand).addRuntimeTag(createUniqueAttackIdentifier(false));
    }

    private TagKey<DamageType> createUniqueAttackIdentifier(boolean isOwnerAttack) {
        UUID attackerUUID = isOwnerAttack ?
                this.getOwnerPatch().getOriginal().getUUID() :
                this.getOriginal().getUUID();

        String identifier = String.format("attack_%s_%d_%d",
                attackerUUID,
                System.nanoTime(),
                ThreadLocalRandom.current().nextInt(1000));

        return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("epicfight", identifier));
    }

    @Override
    public void tick(LivingEvent.LivingTickEvent event) {
        super.tick(event);
        if (getOwnerPatch() == null) return;
        if (getOwnerPatch().getTarget() != null) {
            this.setAttakTargetSync(getOwnerPatch().getTarget());
        }
    }

    public boolean shouldUseOwnerAttack() {
        return true;
    }

    public @Nullable PlayerPatch<?> getOwnerPatch() {
        if (this.ownerPatch != null) {
            return this.ownerPatch;
        } else if ((this.getOriginal()).getOwner() != null) {
            this.ownerPatch = EpicFightCapabilities.getEntityPatch((this.getOriginal()).getOwner(), PlayerPatch.class);
            return this.ownerPatch;
        } else {
            return null;
        }
    }

    @Override
    public void onJoinWorld(FakeManEntity entity, EntityJoinLevelEvent event) {
        super.onJoinWorld(entity, event);
        this.playAnimationSynchronized(EFN_ESekiroAnimations.KUSABIMARU_AUTO1,0.0f);
    }
}
