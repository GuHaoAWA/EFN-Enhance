package com.guhao.efn_enhance.client.model;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.model.armature.HumanoidArmature;

import java.util.Map;

public class FakeManArmature extends HumanoidArmature {
    public final Joint mortalBlade;
    public final Joint Sheath1;
    public final Joint Sheath2;
    public final Joint Slash;
    public FakeManArmature(String name, int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(name, jointNumber, rootJoint, jointMap);
        this.mortalBlade =  this.getOrLogException(jointMap, "Mortal_Blade");
        this.Sheath2 =  this.getOrLogException(jointMap, "Sheath2");
        this.Sheath1 =  this.getOrLogException(jointMap, "Sheath1");
        this.Slash =  this.getOrLogException(jointMap, "Slash");
    }
}
