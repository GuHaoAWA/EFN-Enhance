package com.guhao.efn_enhance.event;

import com.guhao.efn_enhance.EFN_E;
import com.guhao.efn_enhance.register.EFNEEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(
        modid = EFN_E.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ServerEvent {
    private static final Random RANDOM = new Random();
    private static void twitchHead(LivingEntity entity, float intensityYaw, float intensityPitch) {
        long gameTime = entity.level().getGameTime();
        float time = (float)(gameTime % 100) / 100f * (float)Math.PI * 2;
        float noise1 = (float)Math.sin(time * 5) * (RANDOM.nextFloat() * 0.5f + 0.5f);
        float noise2 = (float)Math.cos(time * 5.3f) * (RANDOM.nextFloat() * 0.5f + 0.5f);
        float noise3 = (float)Math.sin(time * 7.1f + 1) * (RANDOM.nextFloat() * 0.5f + 0.5f);
        float yawJitter = (noise1 * 0.4f + noise2 * 0.3f + noise3 * 0.3f) * intensityYaw * 4.0f;
        float pitchJitter = (noise2 * 0.4f + noise3 * 0.3f + noise1 * 0.3f) * intensityPitch * 4.0f;
        if (RANDOM.nextFloat() < 0.2f) {
            yawJitter += (RANDOM.nextFloat() - 0.5f) * intensityYaw * 6.0f;
            pitchJitter += (RANDOM.nextFloat() - 0.5f) * intensityPitch * 6.0f;
        }
        entity.yHeadRot += yawJitter;
        entity.setXRot(entity.getXRot() + pitchJitter);
        float pitch = entity.getXRot();
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch));
        entity.setXRot(pitch);
        if (RANDOM.nextFloat() < 0.4f) {
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(
                    motion.x + (RANDOM.nextFloat() - 0.5f) * 0.2,
                    motion.y + (RANDOM.nextFloat() - 0.2f) * 0.1,
                    motion.z + (RANDOM.nextFloat() - 0.5f) * 0.2
            );
        }

        entity.yHeadRotO = entity.yHeadRot;
        entity.xRotO = entity.getXRot();

        entity.hurtMarked = true;
    }
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(EFNEEffects.THUNDER.get())) {
            twitchHead(entity, 15f, 15f);
        }


    }


    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getEffect(EFNEEffects.THUNDER.get()) != null) {
            entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.0, entity.getDeltaMovement().z());
        }

    }
    @SubscribeEvent
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (event.isCancelable() && player.hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }
    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent event) {
        LivingEntity living = event.getEntity();
        if (event.isCancelable() && living.hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }
    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (event.isCancelable() && living.hasEffect(EFNEEffects.THUNDER.get())) {
                event.setCanceled(true);
            }
        }

    }
    @SubscribeEvent
    public void onFillBucket(FillBucketEvent event) {
        LivingEntity living = event.getEntity();
        if (living != null && event.isCancelable() && living.hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.isCancelable() && event.getPlayer().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getHealth() <= event.getAmount() && entity.hasEffect(EFNEEffects.THUNDER.get())) {
            entity.removeEffect(EFNEEffects.THUNDER.get());
        }


    }
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.isCancelable() && event.getEntity().hasEffect(EFNEEffects.THUNDER.get())) {
            event.setCanceled(true);
        }
    }
}
