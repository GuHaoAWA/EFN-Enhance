package com.guhao.efn_enhance.client.model;

import com.guhao.efn_enhance.register.EFNEEntity;
import yesman.epicfight.gameasset.Armatures;

public class EFNEArmatures {
    public static void registerArmatures() {
        Armatures.registerEntityTypeArmature(EFNEEntity.FAKE_MAN.get(), Armatures.BIPED);
    }
}
