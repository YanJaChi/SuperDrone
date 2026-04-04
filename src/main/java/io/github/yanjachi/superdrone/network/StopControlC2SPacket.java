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
        ServerPlayer sp = ctx.get().getSender();
        if (sp == null) {
            ctx.get().setPacketHandled(true);
            return;
        }

        ctx.get().enqueueWork(() -> {
            DroneEntity drone = DroneEntity.getControlledDrone(sp);
            if (drone != null) {
                drone.clearController();
            }
            DroneEntity.stopControlling(sp);
        });

        ctx.get().setPacketHandled(true);
    }
}