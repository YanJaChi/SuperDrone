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

        if (mc.level != null) {
            Entity drone = mc.level.getEntity(DroneClientControl.droneId);
            if (drone != null) {
                if (mc.getCameraEntity() != drone) {
                    mc.setCameraEntity(drone);
                }
                // 新增：本地即时旋转同步，避免“只在移动时更新视角”
                drone.setYRot(mc.player.getYRot());
                drone.setXRot(mc.player.getXRot());
            }
        }

        if (mc.options.keyShift.isDown()) {
            DroneClientControl.stopControl();
            return;
        }

        float forward = 0f;
        float strafe = 0f;
        if (mc.options.keyUp.isDown()) forward += 1f;
        if (mc.options.keyDown.isDown()) forward -= 1f;
        if (mc.options.keyLeft.isDown()) strafe += 1f;
        if (mc.options.keyRight.isDown()) strafe -= 1f;

        boolean up = mc.options.keyJump.isDown();
        boolean down = mc.options.keySprint.isDown();

        // 关键：每 tick 都发，哪怕全是0
        DroneClientControl.sendInput(forward, strafe, up, down);
    }
}