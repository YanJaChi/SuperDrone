package io.github.yanjachi.superdrone.network;

import io.github.yanjachi.superdrone.entity.DroneEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StopControlC2SPacket {

    public StopControlC2SPacket() {}

    public StopControlC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ServerPlayer sp = context.getSender();
        if (sp == null) {
            context.setPacketHandled(true);
            return;
        }

        context.enqueueWork(() -> {
            DroneEntity drone = DroneEntity.getControlledDrone(sp);
            if (drone != null) {
                // 退出控制时：立即刹停并进入悬停（不下落）
                drone.setDeltaMovement(0.0D, 0.0D, 0.0D);
                drone.setHovering(true);
                drone.clearController();
                drone.hasImpulse = true;
                drone.hurtMarked = true;
            }
            DroneEntity.stopControlling(sp);
        });

        context.setPacketHandled(true);
    }
}