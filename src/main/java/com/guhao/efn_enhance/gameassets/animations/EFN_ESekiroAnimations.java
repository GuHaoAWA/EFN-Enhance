package com.guhao.efn_enhance.gameassets.animations;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.client.effek.MortalBladeEffek;
import com.guhao.efn_enhance.client.effek.RedDragonFlashEffek;
import com.guhao.efn_enhance.client.effek.RedDragonFlashFinish1Effek;
import com.guhao.efn_enhance.client.effek.RedDragonFlashFinish2Effek;
import com.guhao.efn_enhance.client.model.FakeManArmature;
import com.guhao.efn_enhance.client.particles.EFNEParticles;
import com.guhao.efn_enhance.effects.SparkBaEffek;
import com.guhao.efn_enhance.entity.fakeman.FakeManEntity;
import com.guhao.efn_enhance.register.EFNEEffects;
import com.guhao.efn_enhance.register.EFNESounds;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.animations.types.sekiro.SekiroArtsAnimation;
import com.hm.efn.animations.types.sekiro.SekiroAttackAnimation;
import com.hm.efn.client.effek.MistEffek;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.EFNVFXManagers;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.ParticleEffectInvoker;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.hm.efn.gameasset.animations.EFNSekiroAnimations.*;
import static com.merlin204.avalon.util.AvalonAnimationUtils.createSimplePhase;

@SuppressWarnings({"removal", "all"})
@Mod.EventBusSubscriber(modid = EFN_E.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFN_ESekiroAnimations {
    public static final Collider ROOT = new MultiOBBCollider(2, 4, 4, 4, 0.0, 0, 0.0);
    public static final Collider MORTAL_BLADE2 = new MultiOBBCollider(3, 1.5, 7, 1.5, 0.0, 21, 0.0);
    public static final Collider SAKURA_DANCE_COLL = new MultiOBBCollider(3, 1.0, 3.2, 1.0, 0.0, 3.2, 0.0);

    public static AnimationManager.AnimationAccessor<StaticAnimation> KUSABIMARU_IDLE;
    public static AnimationManager.AnimationAccessor<MovementAnimation> KUSABIMARU_WALK;
    public static AnimationManager.AnimationAccessor<MovementAnimation> KUSABIMARU_RUN;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO1;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO2;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO3;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO4;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO5;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> DRAGON_FLASH;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> SHADOW_RUSH;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> ICHIMONJI_1;
    public static AnimationManager.AnimationAccessor<SekiroAttackAnimation> ICHIMONJI_2;
    public static AnimationManager.AnimationAccessor<AttackAnimation> SAKURA_DANCE;
    public static AnimationManager.AnimationAccessor<AttackAnimation> RUSHING_TEMPO;
    public static AnimationManager.AnimationAccessor<SekiroArtsAnimation> OPEN_MORTAL_BLADE_1;
    public static AnimationManager.AnimationAccessor<SekiroArtsAnimation> FAKE_OPEN_MORTAL_BLADE_2;
    public static AnimationManager.AnimationAccessor<SekiroArtsAnimation> WU_SHEN_ULTIMATE_TECHNIQUE_THE_THREE_FORMS_OF_ZERO_INTENT;

    public static Armatures.ArmatureAccessor<FakeManArmature> Sekiro = Armatures.ArmatureAccessor.create(EFN_E.MODID, "weapon/kusabimaru", FakeManArmature::new);
    static Joint kusabimaru = Sekiro.get().searchJointByName("Tool_R");
    static Joint mortalBlade = Sekiro.get().searchJointByName("Mortal_Blade");
    static Joint Slash = Sekiro.get().searchJointByName("Tool_L");
    static Joint Root = Sekiro.get().searchJointByName("Root");
    public EFN_ESekiroAnimations() {
    }

    public static AnimationEvent.InTimeEvent setKusabimaruSheathMesh(float triggerTime, boolean isSheath) {
        return AnimationEvent.InTimeEvent.create(triggerTime, (entityPatch, self, params) -> {
            var mainHandItem = entityPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND);
            var tag = mainHandItem.getOrCreateTag();
            tag.putInt("kusabimaru_sheath", isSheath ? 1 : 0);
        }, AnimationEvent.Side.BOTH);
    }

    public static AnimationEvent.SimpleEvent setKusabimaruSheathMesh(boolean isSheath) {
        return AnimationEvent.SimpleEvent.create((entityPatch, self, params) -> {
            var mainHandItem = entityPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND);
            var tag = mainHandItem.getOrCreateTag();
            tag.putInt("kusabimaru_sheath", isSheath ? 1 : 0);
        }, AnimationEvent.Side.BOTH);
    }

    public static void build(AnimationManager.AnimationBuilder builder) {

        KUSABIMARU_IDLE =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_idle", (accessor) ->
                        new StaticAnimation(true, accessor, Sekiro)
                                .addEvents(setKusabimaruSheathMesh(0, false))
                );
        KUSABIMARU_WALK =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_walk", (accessor) ->
                        new MovementAnimation(true, accessor, Sekiro)
                );
        KUSABIMARU_RUN =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_run", (accessor) ->
                        new MovementAnimation(true, accessor, Sekiro)
                );
        KUSABIMARU_AUTO1 =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_auto1", accessor -> new SekiroAttackAnimation(0.12F, accessor, Sekiro, 1, 1
                        , createSimplePhase(25, 32, 36, InteractionHand.MAIN_HAND, 1, 1, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.WHOOSH_HEAVY_1.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.08F))
                );
        KUSABIMARU_AUTO2 =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_auto2", accessor -> new SekiroAttackAnimation(0.05F, accessor, Sekiro, 1, 1
                        , createSimplePhase(20, 26, 30, InteractionHand.MAIN_HAND, 1.05F, 1.05F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.WHOOSH_HEAVY_4.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.08F))
                );
        KUSABIMARU_AUTO3 =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_auto3", accessor -> new SekiroAttackAnimation(0.05F, accessor, Sekiro, 1, 1
                        , createSimplePhase(26, 33, 38, InteractionHand.MAIN_HAND, 1.1F, 1.1F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.WHOOSH_HEAVY_2.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F))
                );
        KUSABIMARU_AUTO4 =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_auto4", accessor -> new SekiroAttackAnimation(0.05F, accessor, Sekiro, 1, 1
                        , createSimplePhase(15, 24, 33, InteractionHand.MAIN_HAND, 1.15F, 1.15F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.WHOOSH_HEAVY_3.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F))
                );
        KUSABIMARU_AUTO5 =
                builder.nextAccessor("biped/sekiro/kusabimaru/kusabimaru_auto5", accessor -> new SekiroAttackAnimation(0.05F, accessor, Sekiro, 1F, 1
                        , createSimplePhase(18, 29, 34, InteractionHand.MAIN_HAND, 1.3F, 1.3F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.WHOOSH_HEAVY_4.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.04F))
                );
        DRAGON_FLASH =
                builder.nextAccessor("biped/sekiro/kusabimaru/dragon_flash", accessor -> new SekiroAttackAnimation(0.1F, accessor, Sekiro, 1F, 1
                        , createSimplePhase(96, 107, 150, InteractionHand.MAIN_HAND, 3.5F, 1.6F, Root , ROOT))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.MORTAL_BLADE_WHOOSH.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                        .addEvents(setKusabimaruSheathMesh(0.5F, true),
                                AnimationEvent.InTimeEvent.create(0.01F, (entityPatch, self, params) -> {
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false, false));
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(1.6F, (entityPatch, self, params) -> {
                                    if (!EffekUnits.VFXENABLE()) {
                                        return;
                                    }
                                    MortalBladeEffek.playMortalBlade(MortalBladeEffek.Type.LEVEL3,entityPatch.getOriginal().level,entityPatch.getOriginal().getX(),entityPatch.getOriginal().getY(),entityPatch.getOriginal().getZ(),1.2f);
                                }, AnimationEvent.Side.CLIENT),
                                        mortalBladeParticleTrail(96, 107,
                                                new Vec3(-0.6, 6, 0F),
                                                new Vec3(-0.6, 7, 0F),
                                                25, 12,
                                                EFNParticles.MORTAL_BLADE.get(),
                                                0.35F),
                                AnimationEvent.InTimeEvent.create(0.65F, (entityPatch, self, params) -> {
                                    if (!EffekUnits.VFXENABLE()) {
                                        return;
                                    }

                                    if (!EFNClientConfig.SEKIRO_DRAGONFLASH_VFX.get()) {
                                        return;
                                    }

                                    Level level = entityPatch.getOriginal().level();

                                    AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                                    if (player == null) {
                                        return;
                                    }

                                    float prevElapsedTime = player.getPrevElapsedTime();
                                    float elapsedTime = player.getElapsedTime();
                                    float step = (elapsedTime - prevElapsedTime) / 10.0f;

                                    Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.ZERO);
                                    Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(
                                            entityPatch,
                                            kusabimaru,
                                            step,
                                            pointOffset
                                    );
                                    if (!EffekUnits.VFXENABLE()) {
                                        return;
                                    }
                                    Random random = new Random();

                                    for (int i = 0; i < 2; i++) {
                                        float rx = random.nextFloat(-90, 90);
                                        float ry = random.nextFloat(-90, 90);
                                        float rz = random.nextFloat(-90, 90);
                                        RedDragonFlashEffek.playRedDragonFlash(
                                                RedDragonFlashEffek.Type.LEVEL1,
                                                level,
                                                worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                                rx, ry, rz,
                                                2f,
                                                entityPatch.getOriginal()
                                        );
                                    }

                                    float rx2 = random.nextFloat(-90, 90);
                                    float ry2 = random.nextFloat(-90, 90);
                                    float rz2 = random.nextFloat(-90, 90);
                                    RedDragonFlashEffek.playRedDragonFlash(
                                            RedDragonFlashEffek.Type.LEVEL2,
                                            level,
                                            worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                            rx2, ry2, rz2,
                                            2f,
                                            entityPatch.getOriginal()
                                    );

                                    float rx3 = random.nextFloat(-90, 90);
                                    float ry3 = random.nextFloat(-90, 90);
                                    float rz3 = random.nextFloat(-90, 90);
                                    RedDragonFlashEffek.playRedDragonFlash(
                                            RedDragonFlashEffek.Type.LEVEL3,
                                            level,
                                            worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                            rx3, ry3, rz3,
                                            2f,
                                            entityPatch.getOriginal()
                                    );
                                }, AnimationEvent.Side.CLIENT),
                                AnimationEvent.InTimeEvent.create(1.61667F, (entityPatch, self, params) -> {

                                    if (!EffekUnits.VFXENABLE()) {
                                        return;
                                    }

                                    Level level = entityPatch.getOriginal().level();
                                    LivingEntity original = entityPatch.getOriginal();

                                    RedDragonFlashFinish1Effek.playDragonFlashFinish(
                                            RedDragonFlashFinish1Effek.Type.LEVEL1,
                                            level,
                                            entityPatch.getOriginal().position.x(),
                                            entityPatch.getOriginal().position.y(),
                                            entityPatch.getOriginal().position.z(),
                                            0, EffekUnits.getRY(entityPatch), 0,
                                            1.0f
                                    );
                                    RedDragonFlashFinish2Effek.playDragonFlashFinish(
                                            RedDragonFlashFinish2Effek.Type.LEVEL1,
                                            level,
                                            entityPatch.getOriginal().position.x(),
                                            entityPatch.getOriginal().position.y(),
                                            entityPatch.getOriginal().position.z(),
                                            0, EffekUnits.getRY(entityPatch) + 0.1f, 0,
                                            1.0f
                                    );
                                    RedDragonFlashFinish2Effek.playDragonFlashFinish(
                                            RedDragonFlashFinish2Effek.Type.LEVEL1,
                                            level,
                                            entityPatch.getOriginal().position.x(),
                                            entityPatch.getOriginal().position.y(),
                                            entityPatch.getOriginal().position.z(),
                                            0, EffekUnits.getRY(entityPatch) - 0.1f, 0,
                                            1.0f
                                    );
                                    RedDragonFlashFinish2Effek.playDragonFlashFinish(
                                            RedDragonFlashFinish2Effek.Type.LEVEL1,
                                            level,
                                            entityPatch.getOriginal().position.x(),
                                            entityPatch.getOriginal().position.y(),
                                            entityPatch.getOriginal().position.z(),
                                            0, EffekUnits.getRY(entityPatch) + 0.2f, 0,
                                            1.0f
                                    );
                                    RedDragonFlashFinish2Effek.playDragonFlashFinish(
                                            RedDragonFlashFinish2Effek.Type.LEVEL1,
                                            level,
                                            entityPatch.getOriginal().position.x(),
                                            entityPatch.getOriginal().position.y(),
                                            entityPatch.getOriginal().position.z(),
                                            0, EffekUnits.getRY(entityPatch) - 0.2f, 0,
                                            1.0f
                                    );

                                }, AnimationEvent.Side.CLIENT),
                                ParticleEffectInvoker.CustomGroundSplit(110, 2, 0, 0, 0, 2F, true, false, true, false),
                                ParticleEffectInvoker.createDragonFlashBurst(110, 2),
                                ParticleEffectInvoker.CustomGroundSplit(120, 5, 0, 0, 0, 2F, true, true, true, false),
                                ParticleEffectInvoker.createDragonFlashBurst(120, 5),
                                ParticleEffectInvoker.CustomGroundSplit(130, 8, 0, 0, 0, 2F, true, false, true, false),
                                ParticleEffectInvoker.createDragonFlashBurst(130, 8),
                                ParticleEffectInvoker.CustomGroundSplit(140, 11, 0, 0, 0, 2F, true, true, true, false),
                                ParticleEffectInvoker.createDragonFlashBurst(140, 11),
                                ParticleEffectInvoker.CustomGroundSplit(150, 14, 0, 0, 0, 2F, true, false, true, false),
                                ParticleEffectInvoker.createDragonFlashBurst(150, 14),
                                ParticleEffectInvoker.CustomGroundSplit(160, 17, 0, 0, 0, 2F, true, false, true, false),
                                ParticleEffectInvoker.createDragonFlashBurst(160, 17),
                                serverPlaySound(30, EFNSounds.MORTAL_BLADE_SWORD_OUT.get(), 1.2F),
                                serverPlaySound(31, EFNSounds.CIRCULATE_QI.get(), 1.1F),
                                serverPlaySound(100, SoundEvents.ENDER_DRAGON_AMBIENT, 0.5F),
                                setKusabimaruSheathMesh(1.35F, false),
                                EFNVFXManagers.summonVFX(EFNVFXManagers.DRAGON_FLASH_SLASH, 96, 1, 0, 0, 2, Vec3f.ZERO)

                        )

                );
        SHADOW_RUSH =
                builder.nextAccessor("biped/sekiro/kusabimaru/shadow_rush", accessor -> new SekiroAttackAnimation(0.1F, accessor, Sekiro, 1F, 1
                        , createSimplePhase(43, 51, 60, InteractionHand.MAIN_HAND, 2F, 2F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.WHOOSH_HEAVY_4.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, null)
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                        .addProperty(AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))

                );
        ICHIMONJI_1 =
                builder.nextAccessor("biped/sekiro/kusabimaru/ichimonji_1", accessor -> new SekiroAttackAnimation(0.1F, accessor, Sekiro, 1F, 1
                                , createSimplePhase(80, 90, 120, InteractionHand.MAIN_HAND, 1F, 1F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, null)
                                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                                .addProperty(AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                                .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                                .newTimePair(0.0F, 1.8F)
                                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                                .addEvents(AvalonEventUtils.simpleGroundSplit(82, 1, 0, 0, 0, 2, true),
                                        AnimationEvent.InTimeEvent.create(1.4167F, (entityPatch, self, params) -> {
                                            if (!EffekUnits.VFXENABLE()) {
                                                return;
                                            }
                                            Random random = new Random();
//                                    BurstEffek.playBurst(
//                                            BurstEffek.Type.LEVEL1,
//                                            entityPatch.getOriginal().level(),
//                                            entityPatch.getOriginal().position.x(),
//                                            entityPatch.getOriginal().position.y(),
//                                            entityPatch.getOriginal().position.z(),
//                                            (float) random.nextDouble(-Math.PI,Math.PI),EffekUnits.getRY(entityPatch),0,
//                                            1.0f
//                                    );
//
                                        }, AnimationEvent.Side.CLIENT))

                );
        ICHIMONJI_2 =
                builder.nextAccessor("biped/sekiro/kusabimaru/ichimonji_2", accessor -> new SekiroAttackAnimation(0.1F, accessor, Sekiro, 1F, 1
                                , createSimplePhase(50, 60, 70, InteractionHand.MAIN_HAND, 1F, 1F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, null)
                                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                                .addProperty(AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                                .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                                .addEvents(AvalonEventUtils.simpleGroundSplit(55, 1, 0, 0, 0, 2, true),
                                        AnimationEvent.InTimeEvent.create(0.33F, (entityPatch, self, params) -> {
                                            if (!EffekUnits.VFXENABLE()) {
                                                return;
                                            }
                                            Random random = new Random();
//                                    BurstEffek.playBurst(
//                                            BurstEffek.Type.LEVEL1,
//                                            entityPatch.getOriginal().level(),
//                                            entityPatch.getOriginal().position.x(),
//                                            entityPatch.getOriginal().position.y(),
//                                            entityPatch.getOriginal().position.z(),
//                                            (float) random.nextDouble(-Math.PI,Math.PI),EffekUnits.getRY(entityPatch),0,
//                                            1.0f
//                                    );

                                        }, AnimationEvent.Side.CLIENT))

                );
        SAKURA_DANCE = builder.nextAccessor("biped/sekiro/kusabimaru/sakura_dance", accessor ->
                new AttackAnimation(0.1F, accessor, Sekiro,
                        new AttackAnimation.Phase(0, 31 / 90F, 40 / 90F, 50 / 90F, 50 / 90F, Slash, SAKURA_DANCE_COLL)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1))
                                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1))
                                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                        new AttackAnimation.Phase(50 / 90F, 50 / 90F, 65 / 90F, 100 / 90F, 100 / 90F, Slash, SAKURA_DANCE_COLL)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.25F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                        new AttackAnimation.Phase(100 / 90F, 100 / 90F, 113 / 90F, 150 / 90F, 227 / 90F, Slash, SAKURA_DANCE_COLL)
                                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                                .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EFNEParticles.SPARK_HIT)
                                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNESounds.BAZLEY.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get()))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                        .addProperty(AnimationProperty.AttackAnimationProperty.MOVE_VERTICAL, true)
                        .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 5)
                        .addProperty(AnimationProperty.AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0, 150F / 90F))
                        .addEvents(
                                AnimationEvent.InTimeEvent.create(65/90F,(ep,b,c) -> {
                                    SparkBaEffek.playSparkBa(SparkBaEffek.Type.LEVEL1,ep.getOriginal().level,ep.getOriginal().getX(),ep.getOriginal().getY() + 0.25,ep.getOriginal().getZ(),1.0f);
                                }, AnimationEvent.Side.CLIENT),
                                AnimationEvent.InPeriodEvent.create(70 / 90F, 113 / 90F,(ep,b,c) -> {
                                    List<LivingEntity> hitEntities = ep.getCurrentlyActuallyHitEntities();
                                    if (hitEntities != null && !hitEntities.isEmpty()) {
                                        for (LivingEntity hitEntity : hitEntities) {
                                            hitEntity.addEffect(new MobEffectInstance(EFNEEffects.THUNDER.get(),55,1));
                                        }
                                    }
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InPeriodEvent.create(90 / 90F, 113 / 90F,(ep,b,c) -> {
                                    doThunderSparkEffect(ep.getOriginal());
                                    doThunderSliceEffect(ep.getOriginal());
                                }, AnimationEvent.Side.CLIENT),
                                serverPlaySound(66,SoundEvents.LIGHTNING_BOLT_THUNDER, 3.0F),
                                mortalBladeParticleTrail(5, 45,
                                        new Vec3(-0.6, 6, 0F),
                                        new Vec3(-0.6, 7, 0F),
                                        10, 9,
                                        EFNParticles.MORTAL_BLADE.get(),
                                        0.35F)

                        )
                        .newTimePair(0, 150 / 90F)
                        .addStateRemoveOld(EntityState.UPDATE_LIVING_MOTION, false)
                        .newTimePair(0F, Float.MAX_VALUE)
                        .addState(EntityState.ATTACK_RESULT, damageSource -> damageSource.is(DamageTypeTags.IS_FALL) ? AttackResult.ResultType.MISSED : AttackResult.ResultType.SUCCESS)
        );
        RUSHING_TEMPO = builder.nextAccessor("biped/sekiro/kusabimaru/rushing_tempo", (accessor) ->
                new AttackAnimation(0.05F, 0.0F, 0.2F, 0.25F, 0.6F, null, Armatures.BIPED.get().toolR, accessor, Armatures.BIPED)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
                        .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 2)
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, null)
                        .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
                        .addProperty(AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
                        .newTimePair(0.0F, 0.4F)
                        .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                        .addEvents(AvalonEventUtils.simpleGroundSplit(15, 1, 0, 0, 0, 2, true))
        );

        OPEN_MORTAL_BLADE_1 =
                builder.nextAccessor("biped/sekiro/fushigiri/mortalblade_1", accessor -> new SekiroArtsAnimation(0.15F, accessor, Sekiro, 1F, 1
                        , createSimplePhase(114, 152, 200, InteractionHand.MAIN_HAND, 1F, 1.0F, kusabimaru, null))
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.NOSOUND.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(0.025F, 50.0f, 200.0f), EFNExtraDamageInstance.EXTRA_DAMAGE.create(10), ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                        .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                        .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 6)
                        .addEvents(
                                AvalonEventUtils.simpleCameraShake(10, 40, 6, 4, 32),
                                AvalonEventUtils.simpleSound(24, EFNSounds.MORTAL_BLADE_CHARGE1.get(), 1F, 1),
                                AvalonEventUtils.simpleSound(34, EFNSounds.CIRCULATE_QI.get(), 1F, 1),
                                AvalonEventUtils.simpleSound(115, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get(), 1.3F, 1),
                                localPlaySound(24, EFNSounds.MORTAL_BLADE_SWORD_IN.get(), 1.2F),
                                AvalonEventUtils.simpleCameraShake(124, 20, 4, 2, 16),
                                AnimationEvent.InTimeEvent.create(0.0001F, (entityPatch, self, params) -> {
                                    ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(entityPatch.getOriginal(), ServerPlayerPatch.class);
                                    if (serverPlayerPatch != null) FakeManEntity.summon(serverPlayerPatch,EFN_ESekiroAnimations.FAKE_OPEN_MORTAL_BLADE_2,-0.2f);
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false, false));
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 110, 2, false, false, false));
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY.get(), 110, 1, false, false, false));
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.01F, (entityPatch, self, params) -> {
                                    if (EffekUnits.VFXENABLE() && EFNClientConfig.SEKIRO_MORTAL_BLADE_VFX.get()) {
                                        MistEffek.playMist(MistEffek.Type.MIST, entityPatch.getOriginal().level, 0, 0.8, 0, 0.35f, entityPatch.getOriginal());
                                    }
                                }, AnimationEvent.Side.CLIENT),
                                AnimationEvent.InTimeEvent.create(0.567F, (entityPatch, self, params) -> {
                                    if (!EffekUnits.VFXENABLE()) {
                                        return;
                                    }

                                    if (!EFNClientConfig.SEKIRO_DRAGONFLASH_VFX.get()) {
                                        return;
                                    }

                                    Level level = entityPatch.getOriginal().level();

                                    AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                                    if (player == null) {
                                        return;
                                    }

                                    float prevElapsedTime = player.getPrevElapsedTime();
                                    float elapsedTime = player.getElapsedTime();
                                    float step = (elapsedTime - prevElapsedTime) / 10.0f;

                                    Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.ZERO);
                                    Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(
                                            entityPatch,Slash
                                            ,
                                            step,
                                            pointOffset
                                    );

                                    Random random = new Random();

                                    for (int i = 0; i < 2; i++) {
                                        float rx = random.nextFloat(-90, 90);
                                        float ry = random.nextFloat(-90, 90);
                                        float rz = random.nextFloat(-90, 90);
                                        RedDragonFlashEffek.playRedDragonFlash(
                                                RedDragonFlashEffek.Type.LEVEL1,
                                                level,
                                                worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                                rx, ry, rz,
                                                4f,
                                                entityPatch.getOriginal()
                                        );
                                    }

                                    float rx2 = random.nextFloat(-90, 90);
                                    float ry2 = random.nextFloat(-90, 90);
                                    float rz2 = random.nextFloat(-90, 90);
                                    RedDragonFlashEffek.playRedDragonFlash(
                                            RedDragonFlashEffek.Type.LEVEL2,
                                            level,
                                            worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                            rx2, ry2, rz2,
                                            4f,
                                            entityPatch.getOriginal()
                                    );

                                    float rx3 = random.nextFloat(-90, 90);
                                    float ry3 = random.nextFloat(-90, 90);
                                    float rz3 = random.nextFloat(-90, 90);
                                    RedDragonFlashEffek.playRedDragonFlash(
                                            RedDragonFlashEffek.Type.LEVEL3,
                                            level,
                                            worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                            rx3, ry3, rz3,
                                            4f,
                                            entityPatch.getOriginal()
                                    );
                                }, AnimationEvent.Side.CLIENT),
                                mortalBladeChargeParticleTrail(24, 114,
                                        new Vec3(0, 0, 0F),
                                        new Vec3(0, 1.6, 0F),
                                        10, 10,
                                        EFNParticles.MORTAL_BLADE_CHARGE_RED.get(),
                                        0.1F),
                                mortalBladeChargeParticleTrail(24, 114,
                                        new Vec3(0, 0.15, 0F),
                                        new Vec3(0, 0.4, 0F),
                                        1, 2,
                                        ParticleTypes.SMOKE,
                                        0.055F))
                );

        FAKE_OPEN_MORTAL_BLADE_2 =
                builder.nextAccessor("biped/sekiro/fushigiri/fake_mortalblade_2", accessor -> new SekiroArtsAnimation(0.15F, accessor, Sekiro, 1F, 1
                        , createSimplePhase(122, 146, 150, InteractionHand.MAIN_HAND, 1.5F, 1.5F, kusabimaru, MORTAL_BLADE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.NOSOUND.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(0.025F, 50.0f, 200.0f), EFNExtraDamageInstance.EXTRA_DAMAGE.create(30), ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        , createSimplePhase(204, 228, 245, InteractionHand.MAIN_HAND, 1.5F, 1.5F, kusabimaru, MORTAL_BLADE2)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.NOSOUND.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(0.025F, 50.0f, 200.0f), EFNExtraDamageInstance.EXTRA_DAMAGE.create(30), ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                        .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                        .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 6)
                        .addEvents(
                                mortalBladeParticleTrail2(122, 146,
                                        new Vec3(-0.6, 13, 0F),
                                        new Vec3(-0.6, 14, 0F),
                                        50, 30,
                                        EFNParticles.MORTAL_BLADE.get(),
                                        0.35F),
                                mortalBladeParticleTrail2(204, 228,
                                        new Vec3(-0.6, 26, 0F),
                                        new Vec3(-0.6, 27, 0F),
                                        50, 30,
                                        EFNParticles.MORTAL_BLADE.get(),
                                        0.35F),
                                AvalonEventUtils.simpleCameraShake(122, 10, 4, 2, 16),
                                AvalonEventUtils.simpleCameraShake(204, 10, 4, 2, 32),
                                AvalonEventUtils.simpleSound(122, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get(), 1.2F, 1),
                                AvalonEventUtils.simpleSound(204, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get(), 1.3F, 1),
                                AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false, false));
                                }, AnimationEvent.Side.SERVER)
                        )
                );

        WU_SHEN_ULTIMATE_TECHNIQUE_THE_THREE_FORMS_OF_ZERO_INTENT =
                builder.nextAccessor("biped/sekiro/fushigiri/mortalblade_3", accessor -> new SekiroArtsAnimation(0.15F, accessor, Sekiro, 1F, 1
                        , createSimplePhase(114, 152, 200, InteractionHand.MAIN_HAND, 1F, 1.0F, kusabimaru, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.NOSOUND.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(0.025F, 50.0f, 200.0f), EFNExtraDamageInstance.EXTRA_DAMAGE.create(10), ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                        , createSimplePhase(160, 185, 200, InteractionHand.MAIN_HAND, 2.0F, 2.0F, Root, ROOT)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EFNSounds.NOSOUND.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE, Set.of(EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(0.025F, 50.0f, 200.0f), EFNExtraDamageInstance.EXTRA_DAMAGE.create(10), ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F))
                        .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                        .addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, 6)
                        .addEvents(
                                AvalonEventUtils.simpleCameraShake(10, 40, 6, 4, 32),
                                AvalonEventUtils.simpleSound(24, EFNSounds.MORTAL_BLADE_CHARGE1.get(), 1F, 1),
                                AvalonEventUtils.simpleSound(34, EFNSounds.CIRCULATE_QI.get(), 1F, 1),
                                AvalonEventUtils.simpleSound(115, EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get(), 1.3F, 1),
                                localPlaySound(24, EFNSounds.MORTAL_BLADE_SWORD_IN.get(), 1.2F),
                                AvalonEventUtils.simpleCameraShake(124, 20, 4, 2, 16),
                                AnimationEvent.InTimeEvent.create(0.0001F, (entityPatch, self, params) -> {
                                    ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(entityPatch.getOriginal(), ServerPlayerPatch.class);
                                    if (serverPlayerPatch != null) FakeManEntity.summon(serverPlayerPatch,EFN_ESekiroAnimations.FAKE_OPEN_MORTAL_BLADE_2,-0.2f);
                                    if (serverPlayerPatch != null) {
                                        if (entityPatch.getTarget() != null) {
                                            LivingEntity target = entityPatch.getTarget();
                                            float yaw = target.getYRot();
                                            double behindX = target.getX() - Math.sin(Math.toRadians(yaw)) * 2.0;
                                            double behindZ = target.getZ() + Math.cos(Math.toRadians(yaw)) * 2.0;
                                            double y = target.getY() + 0.2;
                                            FakeManEntity.summon_pos(serverPlayerPatch, EFN_ESekiroAnimations.SAKURA_DANCE, 0.0f, behindX, y, behindZ);
                                        } else {
                                            FakeManEntity.summon(serverPlayerPatch, EFN_ESekiroAnimations.SAKURA_DANCE, 0.0f);
                                        }
                                    }
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.00001F, (entityPatch, self, params) -> {
                                    ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(entityPatch.getOriginal(), ServerPlayerPatch.class);
                                    if (serverPlayerPatch != null) FakeManEntity.summon(serverPlayerPatch, EFNSekiroAnimations.MORTAL_BLADE_1,-0.2f);
                                    if (serverPlayerPatch != null) FakeManEntity.summon(serverPlayerPatch, EFNSekiroAnimations.MORTAL_BLADE_2,0.0f);
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.1F, (entityPatch, self, params) -> {
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 32, 10, false, false, false));
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 110, 2, false, false, false));
                                    entityPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY.get(), 110, 10, false, false, false));
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(186/60, (entityPatch, self, params) -> {
                                    LivingEntity livingEntity = entityPatch.getOriginal();
                                    LivingEntity target = entityPatch.getTarget();
                                    entityPatch.playAnimationSynchronized(DRAGON_FLASH,-1.55F);
                                }, AnimationEvent.Side.SERVER),
                                AnimationEvent.InTimeEvent.create(0.567F, (entityPatch, self, params) -> {


                                    Level level = entityPatch.getOriginal().level();

                                    AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                                    if (player == null) {
                                        return;
                                    }

                                    float prevElapsedTime = player.getPrevElapsedTime();
                                    float elapsedTime = player.getElapsedTime();
                                    float step = (elapsedTime - prevElapsedTime) / 10.0f;

                                    Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.ZERO);
                                    Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(
                                            entityPatch,Slash
                                            ,
                                            step,
                                            pointOffset
                                    );

                                    Random random = new Random();

                                    for (int i = 0; i < 2; i++) {
                                        float rx = random.nextFloat(-90, 90);
                                        float ry = random.nextFloat(-90, 90);
                                        float rz = random.nextFloat(-90, 90);
                                        RedDragonFlashEffek.playRedDragonFlash(
                                                RedDragonFlashEffek.Type.LEVEL1,
                                                level,
                                                worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                                rx, ry, rz,
                                                16f,
                                                entityPatch.getOriginal()
                                        );
                                    }

                                    float rx2 = random.nextFloat(-90, 90);
                                    float ry2 = random.nextFloat(-90, 90);
                                    float rz2 = random.nextFloat(-90, 90);
                                    RedDragonFlashEffek.playRedDragonFlash(
                                            RedDragonFlashEffek.Type.LEVEL2,
                                            level,
                                            worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                            rx2, ry2, rz2,
                                            16f,
                                            entityPatch.getOriginal()
                                    );

                                    float rx3 = random.nextFloat(-90, 90);
                                    float ry3 = random.nextFloat(-90, 90);
                                    float rz3 = random.nextFloat(-90, 90);
                                    RedDragonFlashEffek.playRedDragonFlash(
                                            RedDragonFlashEffek.Type.LEVEL3,
                                            level,
                                            worldPos.x() - entityPatch.getOriginal().getX(), worldPos.y() - entityPatch.getOriginal().getY(), worldPos.z() - entityPatch.getOriginal().getZ(),
                                            rx3, ry3, rz3,
                                            16f,
                                            entityPatch.getOriginal()
                                    );
                                }, AnimationEvent.Side.CLIENT),
                                AnimationEvent.InTimeEvent.create(160/60, (entityPatch, self, params) -> {
                                    if (EffekUnits.VFXENABLE()) {
                                        MortalBladeEffek.playMortalBlade(MortalBladeEffek.Type.LEVEL3, entityPatch.getOriginal().level, entityPatch.getOriginal().getX(), entityPatch.getOriginal().getY(), entityPatch.getOriginal().getZ(), 1.2f);
                                    }
                                }, AnimationEvent.Side.CLIENT),
                                mortalBladeChargeParticleTrail(24, 114,
                                        new Vec3(0, 0, 0F),
                                        new Vec3(0, 1.6, 0F),
                                        10, 10,
                                        EFNParticles.MORTAL_BLADE_CHARGE_RED.get(),
                                        0.1F),
                                mortalBladeChargeParticleTrail(24, 114,
                                        new Vec3(0, 0.15, 0F),
                                        new Vec3(0, 0.4, 0F),
                                        1, 2,
                                        ParticleTypes.SMOKE,
                                        0.055F))
                );
    }

    public static AnimationEvent.InPeriodEvent mortalBladeChargeParticleTrail(int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random) {
        float start = (float) startFrame / 60.0F;
        float end = (float) endFrame / 60.0F;

        Joint finalJoint = mortalBlade;
        return AnimationEvent.InPeriodEvent.create(start, end, (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0;
            if (player != null) {
                prevElapsedTime = player.getPrevElapsedTime();
            }
            float elapsedTime = 0;
            if (player != null) {
                elapsedTime = player.getElapsedTime();
            }
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f((float) (endOffset.x - startOffset.x), (float) (endOffset.y - startOffset.y), (float) (endOffset.z - startOffset.z));

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
                for (int i = 0; i <= particleCount; ++i) {
                    float ratio = (float) i / (float) particleCount;
                    Vec3f pointOffset = new Vec3f((float) (startOffset.x + (double) (trailDirection.x * ratio)), (float) (startOffset.y + (double) (trailDirection.y * ratio)), (float) (startOffset.z + (double) (trailDirection.z * ratio)));
                    double randX = (Math.random() - 0.5) * (double) random;
                    double randY = (Math.random() - 0.5) * (double) random;
                    double randZ = (Math.random() - 0.5) * (double) random;
                    Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                    if (entityPatch.getOriginal().level().isClientSide) {
                        entityPatch.getOriginal().level().addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                    }
                }
            }
        }, AnimationEvent.Side.CLIENT);
    }

    public static AnimationEvent.InPeriodEvent mortalBladeParticleTrail2(int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random) {
        float start = (float) startFrame / 60.0F;
        float end = (float) endFrame / 60.0F;

        Joint finalJoint = kusabimaru;
        return AnimationEvent.InPeriodEvent.create(start, end, (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0;
            if (player != null) {
                prevElapsedTime = player.getPrevElapsedTime();
            }
            float elapsedTime = 0;
            if (player != null) {
                elapsedTime = player.getElapsedTime();
            }
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f((float) (endOffset.x - startOffset.x), (float) (endOffset.y - startOffset.y), (float) (endOffset.z - startOffset.z));

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
                for (int i = 0; i <= particleCount; ++i) {
                    float ratio = (float) i / (float) particleCount;
                    Vec3f pointOffset = new Vec3f((float) (startOffset.x + (double) (trailDirection.x * ratio)), (float) (startOffset.y + (double) (trailDirection.y * ratio)), (float) (startOffset.z + (double) (trailDirection.z * ratio)));
                    double randX = (Math.random() - 0.5) * (double) random;
                    double randY = (Math.random() - 0.5) * (double) random;
                    double randZ = (Math.random() - 0.5) * (double) random;
                    Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                    if (entityPatch.getOriginal().level().isClientSide) {
                        entityPatch.getOriginal().level().addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                    }
                }
            }

        }, AnimationEvent.Side.CLIENT);
    }
    public static AnimationEvent.InPeriodEvent mortalBladeParticleTrail(int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random) {
        float start = (float) startFrame / 60.0F;
        float end = (float) endFrame / 60.0F;

        Joint finalJoint = Slash;
        return AnimationEvent.InPeriodEvent.create(start, end, (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0;
            if (player != null) {
                prevElapsedTime = player.getPrevElapsedTime();
            }
            float elapsedTime = 0;
            if (player != null) {
                elapsedTime = player.getElapsedTime();
            }
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f((float) (endOffset.x - startOffset.x), (float) (endOffset.y - startOffset.y), (float) (endOffset.z - startOffset.z));

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
                for (int i = 0; i <= particleCount; ++i) {
                    float ratio = (float) i / (float) particleCount;
                    Vec3f pointOffset = new Vec3f((float) (startOffset.x + (double) (trailDirection.x * ratio)), (float) (startOffset.y + (double) (trailDirection.y * ratio)), (float) (startOffset.z + (double) (trailDirection.z * ratio)));
                    double randX = (Math.random() - 0.5) * (double) random;
                    double randY = (Math.random() - 0.5) * (double) random;
                    double randZ = (Math.random() - 0.5) * (double) random;
                    Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                    if (entityPatch.getOriginal().level().isClientSide) {
                        entityPatch.getOriginal().level().addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                    }
                }
            }

        }, AnimationEvent.Side.CLIENT);
    }
    public static AnimationEvent.InPeriodEvent sakuraDanceParticleTrail(int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random) {
        float start = (float) startFrame / 90.0F;
        float end = (float) endFrame / 90.0F;

        Joint finalJoint = Slash;
        return AnimationEvent.InPeriodEvent.create(start, end, (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0;
            if (player != null) {
                prevElapsedTime = player.getPrevElapsedTime();
            }
            float elapsedTime = 0;
            if (player != null) {
                elapsedTime = player.getElapsedTime();
            }
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f((float) (endOffset.x - startOffset.x), (float) (endOffset.y - startOffset.y), (float) (endOffset.z - startOffset.z));

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
                for (int i = 0; i <= particleCount; ++i) {
                    float ratio = (float) i / (float) particleCount;
                    Vec3f pointOffset = new Vec3f((float) (startOffset.x + (double) (trailDirection.x * ratio)), (float) (startOffset.y + (double) (trailDirection.y * ratio)), (float) (startOffset.z + (double) (trailDirection.z * ratio)));
                    double randX = (Math.random() - 0.5) * (double) random;
                    double randY = (Math.random() - 0.5) * (double) random;
                    double randZ = (Math.random() - 0.5) * (double) random;
                    Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                    if (entityPatch.getOriginal().level().isClientSide) {
                        entityPatch.getOriginal().level().addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                    }
                }
            }

        }, AnimationEvent.Side.CLIENT);
    }
    private static void doThunderSparkEffect(LivingEntity entity) {
        LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        if (entityPatch == null) return;

        if (entityPatch.getValidItemInHand(InteractionHand.MAIN_HAND) == null) return;

        Joint toolR = entityPatch.getArmature().searchJointByName("Tool_R");
        if (toolR == null) return;

        SimpleParticleType particle = EFNEParticles.THUNDER_SPARK.get();
        float amount = 1.0F;
        Vec3 particleSpeed = Vec3.ZERO;

        Level level = entity.level();
        RandomSource random = level.random;

        LinkedList<Vec3> offsets = new LinkedList<>();
        int fixedLength = 4;

        for (int i = 1; i <= fixedLength; i++) {
            offsets.add(new Vec3(0.0, -0.02 * i, -0.3 * i));
        }

        for (float step = 1.0F; step <= 9.0F; step += 2.0F) {
            float time = (step + random.nextInt(3) - 1.0F) / 10.0F;
            Pose middlePose = entityPatch.getAnimator().getPose(time);
            Vec3 posMid = entity.getPosition(time);

            OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float) posMid.x, (float) posMid.y, (float) posMid.z)
                    .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                            .mulBack(entityPatch.getModelMatrix(time)));

            OpenMatrix4f middleJointTf = entityPatch.getArmature()
                    .getBindedTransformFor(middlePose, toolR)
                    .mulFront(middleModelTf);

            for (Vec3 modifier : offsets) {
                if (random.nextInt(100) < 40) {
                    Vec3 jittered = modifier.add(
                            random.nextFloat() * 0.2 - 0.1,
                            random.nextFloat() * 0.2 - 0.1,
                            random.nextFloat() * 0.2 - 0.1
                    );
                    Vec3 particlePos = OpenMatrix4f.transform(middleJointTf, jittered);
                    level.addParticle(particle,
                            particlePos.x, particlePos.y, particlePos.z,
                            particleSpeed.x, particleSpeed.y, particleSpeed.z);
                }
            }
        }
    }
    private static void doThunderSliceEffect(LivingEntity entity) {
        LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        if (entityPatch == null) return;

        if (entityPatch.getValidItemInHand(InteractionHand.MAIN_HAND) == null) return;

        Joint toolR = entityPatch.getArmature().searchJointByName("Tool_R");
        if (toolR == null) return;

        SimpleParticleType particle = EFNEParticles.THUNDER_SLICE.get();
        float amount = 1.0F;
        Vec3 particleSpeed = Vec3.ZERO;

        Level level = entity.level();
        RandomSource random = level.random;

        LinkedList<Vec3> offsets = new LinkedList<>();
        for (int i = 4; i <= 16; i++) {
            offsets.add(new Vec3(0.0, -0.02 * i, -0.3 * i));
        }

        for (float step = 1.0F; step <= 9.0F; step += 2.0F) {
            float time = (step + random.nextInt(3) - 1.0F) / 10.0F;
            Pose middlePose = entityPatch.getAnimator().getPose(time);
            Vec3 posMid = entity.getPosition(time);

            OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float) posMid.x, (float) posMid.y, (float) posMid.z)
                    .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                            .mulBack(entityPatch.getModelMatrix(time)));

            OpenMatrix4f middleJointTf = entityPatch.getArmature()
                    .getBindedTransformFor(middlePose, toolR)
                    .mulFront(middleModelTf);

            for (Vec3 modifier : offsets) {
                if (random.nextInt(100) < 40) {
                    Vec3 jittered = modifier.add(
                            random.nextFloat() * 0.2 - 0.1,
                            random.nextFloat() * 0.2 - 0.1,
                            random.nextFloat() * 0.2 - 0.1
                    );
                    Vec3 particlePos = OpenMatrix4f.transform(middleJointTf, jittered);
                    level.addParticle(particle,
                            particlePos.x, particlePos.y, particlePos.z,
                            particleSpeed.x, particleSpeed.y, particleSpeed.z);
                }
            }
        }
    }
}
