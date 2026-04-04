package io.github.yanjachi.superdrone.network;

import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SuperDrone.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(StartControlS2CPacket.class, id++)
                .decoder(StartControlS2CPacket::new)
                .encoder(StartControlS2CPacket::toBytes)
                .consumerMainThread(StartControlS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(ControlInputC2SPacket.class, id++)
                .decoder(ControlInputC2SPacket::new)
                .encoder(ControlInputC2SPacket::toBytes)
                .consumerMainThread(ControlInputC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(StopControlC2SPacket.class, id++)
                .decoder(StopControlC2SPacket::new)
                .encoder(StopControlC2SPacket::toBytes)
                .consumerMainThread(StopControlC2SPacket::handle)
                .add();
    }
}