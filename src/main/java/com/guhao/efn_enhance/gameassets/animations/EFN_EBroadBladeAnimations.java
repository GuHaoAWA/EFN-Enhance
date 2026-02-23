package com.guhao.efn_enhance.gameassets.animations;

import com.guhao.efn_enhance.gameassets.type.ExecuteAttackAnimation;
import com.guhao.efn_enhance.register.EFNEEffects;
import com.hm.efn.EFN;
import com.hm.efn.animations.types.stun.EFNStunAnimation;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.yamato.DMC_V_JC_Client;
import com.hm.efn.util.yamato.DMC_V_JC_Server;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;
import java.util.Set;
import java.util.function.Function;


@Mod.EventBusSubscriber(modid = EFN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFN_EBroadBladeAnimations {
    public static final Function<DamageSource, AttackResult.ResultType> INVINCIBLE_SOURCE_VALIDATOR = (damagesource) -> damagesource.getEntity() != null && !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? AttackResult.ResultType.MISSED : AttackResult.ResultType.SUCCESS;
    public static final Collider FIST = new MultiOBBCollider(3, 0.8, 0.8, 0.8, 0.0, 0.0, 0.4);
    public static AnimationManager.AnimationAccessor<ExecuteAttackAnimation> BROADBLADE_EXECUTE;
    public static AnimationManager.AnimationAccessor<EFNStunAnimation> BROADBLADE_EXECUTED;
    public static void build(AnimationManager.AnimationBuilder builder) {
        Armatures.ArmatureAccessor<Armature> Broadblade = Armatures.ArmatureAccessor.create(EFN.MODID, "weapon/broadblade", Armature::new);
        Joint weapon = Broadblade.get().searchJointByName("Hand_R");
        BROADBLADE_EXECUTE =
                builder.nextAccessor("biped/broadblade/execute", (accessor) -> (new ExecuteAttackAnimation(0.1F, accessor, Armatures.BIPED, -0.25F,
                        (new AttackAnimation.Phase(0.0F, (float) 90 / 60, (float) 100 /60, (float) 215 /60, (float) 120 /60, InteractionHand.MAIN_HAND, weapon,FIST)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.01F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.UNBLOCKALBE))),
                        (new AttackAnimation.Phase((float) 120 /60, (float) 182 / 60, (float) 190 /60, (float) 215 /60, Float.MAX_VALUE, InteractionHand.MAIN_HAND, weapon,FIST)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(10000F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.multiplier(10000F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.NONE)
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.UNBLOCKALBE)))
                )
                                .addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(
                                        AnimationEvent.SimpleEvent.create((ep, anim, objs) -> {
                                            if (ep.getTarget() == null) {
                                                return;
                                            }
                                            LivingEntity targetEntity = ep.getTarget();
                                            LivingEntity player = ep.getOriginal();
                                            targetEntity.addEffect(new MobEffectInstance(
                                                    EFNEEffects.THUNDER.get(),
                                                    100,
                                                    0
                                            ));
                                            if (player != null) {
                                                double distance = 0.8;
                                                float yaw = player.getYRot();
                                                float pitch = player.getXRot();

                                                double dx = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                                                double dz = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

                                                double targetX = player.getX() + dx * distance;
                                                double targetY = player.getY();
                                                double targetZ = player.getZ() + dz * distance;

                                                targetEntity.teleportTo(targetX, targetY, targetZ);
                                            }
                                        }, AnimationEvent.Side.SERVER))
                                )
                .addProperty(AnimationProperty.AttackAnimationProperty.REACH, 0.0F).newTimePair(0.0F, 1.5F).addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .newTimePair(0.0F, Float.MAX_VALUE).addStateRemoveOld(EntityState.LOCKON_ROTATE, false).addStateRemoveOld(EntityState.TURNING_LOCKED, true)
                .newTimePair(0.0F, 1.5F).addStateRemoveOld(EntityState.ATTACK_RESULT, INVINCIBLE_SOURCE_VALIDATOR)
                .addProperty(AnimationProperty.AttackAnimationProperty.PLAY_SPEED_MODIFIER, Animations.ReusableSources.CONSTANT_ONE))
                                .addEvents(

                                        AnimationEvent.InTimeEvent.create(0.001F, (entityPatch, self, params) -> {

                                        }, AnimationEvent.Side.SERVER),
                                        AnimationEvent.InTimeEvent.create(0.001F, (entityPatch, self, params) -> {
                                            entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 110, 5, false, false, false));
                                        }, AnimationEvent.Side.SERVER),
                                        AnimationEvent.InTimeEvent.create(0.001F, (entityPatch, self, params) -> {
                                            entityPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY.get(), 110, 5, false, false, false));
                                        }, AnimationEvent.Side.SERVER))
                );
        BROADBLADE_EXECUTED =
                builder.nextAccessor("biped/broadblade/executed", (accessor) -> new EFNStunAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
                        .addEvents(AnimationEvent.InTimeEvent.create(0.0F, (entitypatch, self, params) -> {
                            entitypatch.getOriginal()
                                    .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT.get(), 10, 1, false, false, false));
                        }, AnimationEvent.Side.SERVER))
                );
    }
}
