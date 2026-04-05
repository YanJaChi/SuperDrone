package io.github.yanjachi.superdrone.client;

import io.github.yanjachi.superdrone.network.ControlInputC2SPacket;
import io.github.yanjachi.superdrone.network.ModNetwork;
import io.github.yanjachi.superdrone.network.StopControlC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class DroneClientControl {
    public static boolean controlling = false;
    public static int droneId = -1;

    // 新增：允许短暂丢失实体（区块/同步抖动），避免立刻断控
    private static int missingTicks = 0;
    private static final int MAX_MISSING_TICKS = 60; // 约 3 秒（20tps）

    public static void startControl(int id) {
        Minecraft mc = Minecraft.getInstance();
        Entity e = mc.level != null ? mc.level.getEntity(id) : null;
        controlling = true;
        droneId = id;
        missingTicks = 0;

        if (e != null) {
            mc.setCameraEntity(e);
        } else if (mc.player != null) {
            // 先保持玩家视角，等实体同步到客户端后再切
            mc.setCameraEntity(mc.player);
        }
    }

    public static void stopControl() {
        Minecraft mc = Minecraft.getInstance();
        if (controlling) {
            ModNetwork.CHANNEL.sendToServer(new StopControlC2SPacket());
        }
        controlling = false;
        droneId = -1;
        missingTicks = 0;

        if (mc.options != null) {
            mc.options.keyUse.setDown(false);
            mc.options.keyAttack.setDown(false);
        }

        if (mc.player != null) mc.setCameraEntity(mc.player);
    }

    public static void tickSafetyCheck() {
        Minecraft mc = Minecraft.getInstance();
        if (!controlling || mc.level == null) return;

        Entity e = mc.level.getEntity(droneId);

        if (e == null || !e.isAlive()) {
            missingTicks++;
            if (missingTicks > MAX_MISSING_TICKS) {
                stopControl();
            }
            return;
        }

        // 找回实体后重置计数，并确保镜头回到无人机
        missingTicks = 0;
        if (mc.getCameraEntity() != e) {
            mc.setCameraEntity(e);
        }
    }

    public static void sendInput(float forward, float strafe, boolean up, boolean down) {
        if (!controlling) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();

        ModNetwork.CHANNEL.sendToServer(
                new ControlInputC2SPacket(forward, strafe, up, down, yaw, pitch)
        );
    }
}