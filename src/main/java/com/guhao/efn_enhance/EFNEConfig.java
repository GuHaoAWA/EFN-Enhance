package com.guhao.efn_enhance;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = "efn_enhance", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EFNEConfig {
    public static class Client {
        public final ForgeConfigSpec.BooleanValue effectEnabled;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            effectEnabled = builder
                    .comment("Enable/disable the sphere mask effect")
                    .define("effectEnabled", true);

            builder.pop();
        }
    }

    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    private static boolean effectEnabled = true;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading configEvent) {
        if (configEvent.getConfig().getSpec() == CLIENT_SPEC) {
            bakeConfig();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading configEvent) {
        if (configEvent.getConfig().getSpec() == CLIENT_SPEC) {
            bakeConfig();
        }
    }

    private static void bakeConfig() {
        effectEnabled = CLIENT.effectEnabled.get();
    }

    public static boolean isEffectEnabled() {
        return effectEnabled;
    }
}