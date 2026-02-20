package com.guhao.efn_enhance.register;


import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.entity.fakeman.FakeManEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EFNEEntity {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EFN_E.MODID);
    public static final RegistryObject<EntityType<FakeManEntity>> FAKE_MAN = ENTITIES.register("fake_man", () ->
            EntityType.Builder.<FakeManEntity>of(FakeManEntity::new, MobCategory.MONSTER)
                    .fireImmune().sized(0.6F, 1.8F).clientTrackingRange(10).build("fake_man")
    );

}
