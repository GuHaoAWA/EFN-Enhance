package com.guhao.efn_enhance.entity.fakeman;

import com.guhao.efn_enhance.register.EFNEEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class FakeManEntity extends TamableAnimal {

    AssetAccessor<? extends StaticAnimation> animation;
    float transitionTimeModifier;
    public FakeManEntity(ServerPlayer owner, AssetAccessor<? extends StaticAnimation> animation, float transitionTimeModifier) {
        super(EFNEEntity.FAKE_MAN.get(), owner.level());
        this.transitionTimeModifier = transitionTimeModifier;
        this.animation = animation;
        tame(owner);
//        this.setPersistenceRequired();
        this.noCulling = true;
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public FakeManEntity(EntityType<FakeManEntity> FakeManEntityType, Level level) {
        super(FakeManEntityType, level);
    }


    public static AttributeSupplier getDefaultAttribute() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 999999)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ATTACK_SPEED, 1.6)
                .add(Attributes.ATTACK_KNOCKBACK, 0)
                .add(EpicFightAttributes.MAX_STRIKES.get(), 99999.0)
                .add(EpicFightAttributes.IMPACT.get(), 10)
                .add(EpicFightAttributes.MAX_STAMINA.get(), 999999)
                .add(EpicFightAttributes.STUN_ARMOR.get(), 100)
                .add(EpicFightAttributes.WEIGHT.get(), 0)
                .add(EpicFightAttributes.STAMINA_REGEN.get(), 1)
                .add(EpicFightAttributes.OFFHAND_IMPACT.get(), 10)
                .add(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get(), 1)
                .add(EpicFightAttributes.EXECUTION_RESISTANCE.get(), 100)
                .add(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get(), 100)
                .add(EpicFightAttributes.OFFHAND_MAX_STRIKES.get(), 99999.0)
                .build();
    }



    public static void summon(ServerPlayerPatch serverPlayerPatch,AssetAccessor<? extends StaticAnimation> animation,float transitionTimeModifier) {
        FakeManEntity fakeMan = new FakeManEntity(serverPlayerPatch.getOriginal(),animation,transitionTimeModifier);
        Vec3 vec3 = serverPlayerPatch.getOriginal().position();
        fakeMan.moveTo(new Vec3(vec3.x, vec3.y, vec3.z));
        serverPlayerPatch.getOriginal().serverLevel().addFreshEntity(fakeMan);
    }
    public static void summon_pos(ServerPlayerPatch serverPlayerPatch,AssetAccessor<? extends StaticAnimation> animation,float transitionTimeModifier,double x,double y,double z) {
        FakeManEntity fakeMan = new FakeManEntity(serverPlayerPatch.getOriginal(),animation,transitionTimeModifier);
        fakeMan.moveTo(new Vec3(x,y,z));
        serverPlayerPatch.getOriginal().serverLevel().addFreshEntity(fakeMan);
    }

    @Override
    public void push(@NotNull Entity pEntity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    protected void doPush(@NotNull Entity p_20971_) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public void tame(@NotNull Player player) {
        super.tame(player);
        setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND).copy());
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float p_27568_) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void tick() {

        super.tick();

        if (this.getOwner() == null || !this.getOwner().isAlive() || this.getOwner().getHealth() <= 0 || tickCount >= 100) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }
        LivingEntity owner = getOwner();
        this.setNoAi(true);
        FakeManPatch fakeManPatch = EpicFightCapabilities.getEntityPatch(this, FakeManPatch.class);
        assert fakeManPatch != null;
        if (tickCount == 1) {
            fakeManPatch.playAnimationSynchronized(animation,transitionTimeModifier);
        }

        if (!fakeManPatch.getEntityState().turningLocked()) {
            this.xRotO = owner.xRotO;
            this.yRotO = owner.yRotO;
            this.setYRot(owner.getYRot());
            this.setXRot(owner.getXRot());
            this.setYHeadRot(owner.getYHeadRot());
            this.yBodyRot = owner.yBodyRot;
        } else {
            if (fakeManPatch.getTarget() != null)
                fakeManPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(fakeManPatch.getTarget().getX(), fakeManPatch.getTarget().getEyeY() + 0.1, fakeManPatch.getTarget().getZ()));
        }

    }


}