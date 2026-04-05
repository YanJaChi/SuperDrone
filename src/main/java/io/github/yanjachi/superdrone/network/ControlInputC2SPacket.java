package io.github.yanjachi.superdrone.network;

import io.github.yanjachi.superdrone.entity.DroneEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ControlInputC2SPacket {
    private final float forward;
    private final float strafe;
    private final boolean up;
    private final boolean down;
    private final float yaw;
    private final float pitch;

    public ControlInputC2SPacket(float forward, float strafe, boolean up, boolean down, float yaw, float pitch) {
        this.forward = forward;
        this.strafe = strafe;
        this.up = up;
        this.down = down;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public ControlInputC2SPacket(FriendlyByteBuf buf) {
        this.forward = buf.readFloat();
        this.strafe = buf.readFloat();
        this.up = buf.readBoolean();
        this.down = buf.readBoolean();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(forward);
        buf.writeFloat(strafe);
        buf.writeBoolean(up);
        buf.writeBoolean(down);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ServerPlayer sp = context.getSender();
        if (sp == null) {
            context.setPacketHandled(true);
            return;
        }

        context.enqueueWork(() -> {
            DroneEntity drone = DroneEntity.getControlledDrone(sp);

            // 找不到或实体失效：跳过本 tick，不强制断控
            if (drone == null || !drone.isAlive()) return;

            // 输入即续控（心跳）
            drone.setController(sp);

            // 收到输入时取消悬停，恢复重力/运动
            if (drone.isHovering()) {
                drone.setHovering(false);
            }

            // 视角同步
            drone.setYRot(yaw);
            drone.setXRot(pitch);
            drone.setYHeadRot(yaw);
            drone.setYBodyRot(yaw);
            drone.yRotO = yaw;
            drone.xRotO = pitch;
            drone.yHeadRotO = yaw;
            drone.yBodyRotO = yaw;

            // 输入归一化
            float f = forward;
            float s = strafe;
            float len = (float) Math.sqrt(f * f + s * s);
            if (len > 1.0F) {
                f /= len;
                s /= len;
            }

            // 相对视角移动
            float yawRad = (float) Math.toRadians(yaw);
            Vec3 forwardVec = new Vec3(-Math.sin(yawRad), 0.0D, Math.cos(yawRad));
            Vec3 rightVec = new Vec3(Math.cos(yawRad), 0.0D, Math.sin(yawRad));

            Vec3 horizontal = forwardVec.scale(f * 1.0D).add(rightVec.scale(s * 0.5D));
            double yMove = (up ? 0.25D : 0.0D) + (down ? -0.25D : 0.0D);

            drone.setDeltaMovement(horizontal.x, yMove, horizontal.z);
            drone.hasImpulse = true;
            drone.hurtMarked = true;
        });

        context.setPacketHandled(true);
    }
}