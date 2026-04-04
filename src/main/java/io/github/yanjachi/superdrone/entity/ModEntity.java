package io.github.yanjachi.superdrone.entity;

import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntity {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SuperDrone.MODID);

    public static final RegistryObject<EntityType<DroneEntity>> DRONE =
            ENTITIES.register("drone", () ->
                    EntityType.Builder.<DroneEntity>of(DroneEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 0.9F) // 碰撞箱宽高，可后续调整
                            .build("drone")
            );

    public static void register(IEventBus eventBus) {ENTITIES.register(eventBus);
    }
}