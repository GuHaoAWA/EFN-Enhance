package com.guhao.efn_enhance.register;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.client.shaderpasses.RedBlackContrast;
import com.guhao.efn_enhance.client.shaderpasses.SphereMaskComposite;
import com.guhao.efn_enhance.client.shaderpasses.SphereMaskGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class EFNEPostPasses {
    public static RedBlackContrast red_black_contrast;
    public static SphereMaskGenerator sphere_mask_generator;
    public static SphereMaskComposite sphere_mask_composite;
    public static void register(RegisterShadersEvent event) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        try {
            red_black_contrast = new RedBlackContrast("efn_enhance:red_black_contrast", rm);
            sphere_mask_generator = new SphereMaskGenerator("efn_enhance:sphere_mask_gen", rm);
            sphere_mask_composite = new SphereMaskComposite("efn_enhance:sphere_mask_composite", rm);
        } catch (IOException e) {
            EFN_E.LOGGER.error("Failed to load EFNE shaders", e);

        }
    }
}
