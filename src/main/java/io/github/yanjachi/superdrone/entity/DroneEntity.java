package io.github.yanjachi.superdrone.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class DroneEntity extends PathfinderMob {
    public DroneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
}