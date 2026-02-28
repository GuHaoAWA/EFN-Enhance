package com.guhao.efn_enhance.command;

import com.guhao.efn_enhance.EFNEConfig;
import com.guhao.efn_enhance.client.EffectController;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class EFNECommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("efne")
                        .requires(source -> source.hasPermission(2))

                        // 简写：同时控制两个
                        .then(Commands.literal("on")
                                .executes(context -> setAllEnabled(context, true)))
                        .then(Commands.literal("off")
                                .executes(context -> setAllEnabled(context, false)))
        );
    }

    private static int setAllEnabled(CommandContext<CommandSourceStack> context, boolean enabled) {
        // 同时控制着色器和动画
        EFNEConfig.CLIENT.effectEnabled.set(enabled);
        EffectController.setAnimationEffectEnabled(enabled);

        String message = enabled ?
                "§a[EFNE] 所有效果已开启" :
                "§c[EFNE] 所有效果已关闭";

        context.getSource().sendSuccess(() -> Component.literal(message), true);
        return Command.SINGLE_SUCCESS;
    }
}