package com.guhao.efn_enhance.client;

import com.guhao.efn_enhance.EFNEConfig;

public class EffectController {
    private static boolean animationEffectEnabled = true;

    public static void setAnimationEffectEnabled(boolean enabled) {
        animationEffectEnabled = enabled;

    }

    public static boolean isAnimationEffectEnabled() {
        return animationEffectEnabled && EFNEConfig.isEffectEnabled();
    }

    // 切换开关
    public static void toggleAnimationEffect() {
        setAnimationEffectEnabled(!animationEffectEnabled);
    }
}