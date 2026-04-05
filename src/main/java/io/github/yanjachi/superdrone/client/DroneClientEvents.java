package io.github.yanjachi.superdrone.client;

import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SuperDrone.MODID, value = Dist.CLIENT)
public class DroneClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options == null) return;

        DroneClientControl.tickSafetyCheck();
        if (!DroneClientControl.controlling) return;

        // 确保相机实体仍是无人机
        if (mc.level != null) {
            Entity drone = mc.level.getEntity(DroneClientControl.droneId);
            if (drone != null && mc.getCameraEntity() != drone) {
                mc.setCameraEntity(drone);
            }
        }

        // Shift 退出
        if (mc.options.keyShift.isDown()) {
            DroneClientControl.stopControl();
            return;
        }

        float forward = 0f;
        float strafe = 0f;

        if (mc.options.keyUp.isDown()) forward += 1f;      // W
        if (mc.options.keyDown.isDown()) forward -= 1f;    // S
        if (mc.options.keyLeft.isDown()) strafe += 1f;     // A
        if (mc.options.keyRight.isDown()) strafe -= 1f;    // D

        boolean up = mc.options.keyJump.isDown();          // Space
        boolean down = mc.options.keySprint.isDown();      // Ctrl

        DroneClientControl.sendInput(forward, strafe, up, down);
    }
}