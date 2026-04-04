package io.github.yanjachi.superdrone.client;

import io.github.yanjachi.superdrone.network.ControlInputC2SPacket;
import io.github.yanjachi.superdrone.network.ModNetwork;
import io.github.yanjachi.superdrone.network.StopControlC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class DroneClientControl {
    public static boolean controlling = false;
    public static int droneId = -1;

    public static void startControl(int id) {
        Minecraft mc = Minecraft.getInstance();
        Entity e = mc.level != null ? mc.level.getEntity(id) : null;
        if (e != null) {
            controlling = true;
            droneId = id;
            mc.setCameraEntity(e);
        }
    }

    public static void stopControl() {
        Minecraft mc = Minecraft.getInstance();
        if (controlling) {
            ModNetwork.CHANNEL.sendToServer(new StopControlC2SPacket());
        }
        controlling = false;
        droneId = -1;
        if (mc.player != null) mc.setCameraEntity(mc.player);
    }

    public static void tickSafetyCheck() {
        Minecraft mc = Minecraft.getInstance();
        if (!controlling || mc.level == null) return;
        Entity e = mc.level.getEntity(droneId);
        if (e == null || !e.isAlive()) stopControl();
    }

    public static void sendInput(float forward, float strafe, boolean up, boolean down) {
        if (!controlling) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 只用玩家角度（稳定）
        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();

        ModNetwork.CHANNEL.sendToServer(
                new ControlInputC2SPacket(forward, strafe, up, down, yaw, pitch)
        );
    }
}