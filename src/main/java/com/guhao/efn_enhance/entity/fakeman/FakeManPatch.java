package com.guhao.efn_enhance.entity.fakeman;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

import java.util.Set;


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
        return this.getOwnerPatch() != null && this.shouldUseOwnerAttack() ? this.getOwnerPatch().attack(damageSource, target, hand) : super.attack(damageSource, target, hand);
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
    public @Nullable EpicFightDamageSource getEpicFightDamageSource() {
        return this.getOwnerPatch() != null ? this.getOwnerPatch().getEpicFightDamageSource() : super.getEpicFightDamageSource();
    }
    @Override
    public EpicFightDamageSource getDamageSource(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
        return this.getOwnerPatch() != null ? this.getOwnerPatch().getDamageSource(animation, hand) : super.getDamageSource(animation, hand);
    }
    @OnlyIn(Dist.CLIENT)
    public boolean flashTargetIndicator(LocalPlayerPatch playerPatch) {
        return false;
    }
    public boolean isTargetInvulnerable(Entity entity) {
        if (entity.equals((this.getOriginal()).getOwner())) {
            return true;
        } else {
            if (entity instanceof FakeManEntity artifactSpiritEntity) {
                if (this.getOwnerPatch() != null) {
                    return this.getOwnerPatch().getOriginal().equals(artifactSpiritEntity.getOwner());
                }
            }

            return false;
        }
    }
}
