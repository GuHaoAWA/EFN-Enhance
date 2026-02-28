package com.guhao.efn_enhance;

import com.guhao.efn_enhance.client.particles.EFNEParticles;
import com.guhao.efn_enhance.register.EFNEEffects;
import com.guhao.efn_enhance.register.EFNEEntity;
import com.guhao.efn_enhance.register.EFNEPostPasses;
import com.guhao.efn_enhance.register.EFNESounds;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.nio.file.Path;


@Mod(EFN_E.MODID)
public class EFN_E
{
    public static final String MODID = "efn_enhance";

    public static final Logger LOGGER = LogUtils.getLogger();
    public EFN_E(FMLJavaModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.CLIENT, EFNEConfig.CLIENT_SPEC, "efn_enhance-client.toml");
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackFindersEvent);
        EFNESounds.EFNESound.register(modEventBus);
        EFNEEntity.ENTITIES.register(modEventBus);
        EFNEParticles.PARTICLES.register(modEventBus);
        EFNEEffects.EFFECTS.register(modEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(EFNEPostPasses::register);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }
    public void addPackFindersEvent(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Path resourcePath = ModList.get().getModFileById(EFN_E.MODID).getFile().findResource("packs/efn_enhance_must");
            PathPackResources pack = new PathPackResources(ModList.get().getModFileById(EFN_E.MODID).getFile().getFileName() + ":" + resourcePath, resourcePath, false);
            Pack.ResourcesSupplier resourcesSupplier = (string) -> pack;
            Pack.Info info = Pack.readPackInfo("efn_enhance_must", resourcesSupplier);

            if (info != null) {
                event.addRepositorySource((source) ->
                        source.accept(Pack.create("efn_enhance_must", Component.translatable("pack.efn_enhance_must.title"), true, resourcesSupplier, info, PackType.CLIENT_RESOURCES, Pack.Position.TOP, false, PackSource.BUILT_IN)));
            }
        }
    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
