package com.guhao.efn_enhance.register;

import com.guhao.efn_enhance.command.EFNECommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "efn_enhance")
public class EFNECommandRegister {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        EFNECommand.register(event.getDispatcher());
    }
}