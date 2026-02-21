package com.guhao.efn_enhance.entity.fakeman;

import com.guhao.efn_enhance.register.EFNEEntity;
import com.hm.efn.registries.EFNItem;
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
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class FakeManEntity extends TamableAnimal {


    public FakeManEntity(ServerPlayer owner) {
        super(EFNEEntity.FAKE_MAN.get(), owner.level());
        tame(owner);
        this.setPersistenceRequired();
    }

    public FakeManEntity(EntityType<FakeManEntity> FakeManEntityType, Level level) {
        super(FakeManEntityType, level);
    }


    public static AttributeSupplier getDefaultAttribute() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 999999999)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(EpicFightAttributes.MAX_STRIKES.get(), 6.0)
                .build();
    }



    public static void summon(ServerPlayerPatch serverPlayerPatch) {
        FakeManEntity fakeMan = new FakeManEntity(serverPlayerPatch.getOriginal());
        Vec3 vec3 = serverPlayerPatch.getOriginal().position();
        fakeMan.moveTo(new Vec3(vec3.x, vec3.y, vec3.z));
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


        this.setNoAi(true);




    }


}