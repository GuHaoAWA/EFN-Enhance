package com.guhao.efn_enhance.register;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.client.model.EFNEArmatures;
import com.guhao.efn_enhance.entity.fakeman.FakeManEntity;
import com.guhao.efn_enhance.entity.fakeman.FakeManPatch;
import com.guhao.efn_enhance.entity.fakeman.FakeManRender;
import com.guhao.efn_enhance.entity.fakeman.FakeManRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

@Mod.EventBusSubscriber(modid = EFN_E.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFNEEntityHandler {
    @SubscribeEvent
    public static void handleEntityPatchRegistry(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(EFNEEntity.FAKE_MAN.get(), (entity -> FakeManPatch::new));
    }
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EFNEEntity.FAKE_MAN.get(), FakeManEntity.getDefaultAttribute());
    }
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EFNEEntity.FAKE_MAN.get(), FakeManRender::new);
    }
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handlePatchedRenderers(PatchedRenderersEvent.Add event) {
        EntityRendererProvider.Context context = event.getContext();
        event.addPatchedEntityRenderer(EFNEEntity.FAKE_MAN.get(), entityType ->
                new FakeManRenderer(Meshes.BIPED, context, entityType).initLayerLast(context, entityType));

    }
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(EFNEArmatures::registerArmatures);
    }
}
