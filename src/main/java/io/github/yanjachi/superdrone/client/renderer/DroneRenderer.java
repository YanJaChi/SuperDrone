package io.github.yanjachi.superdrone.client.renderer;

import io.github.yanjachi.superdrone.entity.DroneEntity;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DroneRenderer extends MobRenderer<DroneEntity, PigModel<DroneEntity>> {
    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/pig/pig.png");
    }
}