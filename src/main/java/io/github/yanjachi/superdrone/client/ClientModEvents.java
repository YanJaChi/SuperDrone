package io.github.yanjachi.superdrone.client;

import io.github.yanjachi.superdrone.client.model.DroneModel;
import io.github.yanjachi.superdrone.client.renderer.DroneRenderer;
import io.github.yanjachi.superdrone.entity.ModEntity;
import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SuperDrone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DroneModel.LAYER_LOCATION, DroneModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntity.DRONE.get(), DroneRenderer::new);
    }
}