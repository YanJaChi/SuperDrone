package io.github.yanjachi.superdrone.client;

import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SuperDrone.MODID, value = Dist.CLIENT)
public class DroneRenderEvents {

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (DroneClientControl.controlling) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        if (!DroneClientControl.controlling) return;
        event.setCanceled(true); // 控制中禁用攻击/交互
    }
}