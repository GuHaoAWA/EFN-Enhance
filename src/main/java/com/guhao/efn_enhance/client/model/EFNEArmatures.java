package com.guhao.efn_enhance.client.model;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.register.EFNEEntity;
import yesman.epicfight.gameasset.Armatures;

public class EFNEArmatures {
    public static final Armatures.ArmatureAccessor<FakeManArmature> SEKRIO = Armatures.ArmatureAccessor.create(EFN_E.MODID, "weapon/kusabimaru", FakeManArmature::new);
    public static void registerArmatures() {
        Armatures.registerEntityTypeArmature(EFNEEntity.FAKE_MAN.get(), SEKRIO);
    }
}
