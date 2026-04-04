package io.github.yanjachi.superdrone.network;

import io.github.yanjachi.superdrone.client.DroneClientControl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartControlS2CPacket {
    private final int droneId;

    public StartControlS2CPacket(int droneId) {
        this.droneId = droneId;
    }

    public StartControlS2CPacket(FriendlyByteBuf buf) {
        this.droneId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(droneId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DroneClientControl.startControl(droneId));
        ctx.get().setPacketHandled(true);
    }
}