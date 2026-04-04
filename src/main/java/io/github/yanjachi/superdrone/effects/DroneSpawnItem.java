package io.github.yanjachi.superdrone.effects;

import io.github.yanjachi.superdrone.entity.DroneEntity;
import io.github.yanjachi.superdrone.entity.ModEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DroneSpawnItem extends Item {
    public DroneSpawnItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos spawnPos = context.getClickedPos().above();

        DroneEntity drone = ModEntity.DRONE.get().create((ServerLevel) level);
        if (drone == null) return InteractionResult.FAIL;

        drone.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                context.getRotation(),
                0.0F
        );

        level.addFreshEntity(drone);

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.CONSUME;
    }
}