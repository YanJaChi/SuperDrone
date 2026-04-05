package io.github.yanjachi.superdrone.effects;

import io.github.yanjachi.superdrone.entity.DroneEntity;
import io.github.yanjachi.superdrone.entity.ModEntity;
import net.minecraft.core.BlockPos;
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

        // 在被点击方块的“点击面外侧”生成，而不是固定 above()
        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());

        DroneEntity drone = ModEntity.DRONE.get().create(level);
        if (drone == null) return InteractionResult.FAIL;

        drone.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                context.getRotation(),
                0.0F
        );

        // 可选：避免与方块重叠，检查碰撞
        if (!level.noCollision(drone, drone.getBoundingBox())) {
            return InteractionResult.FAIL;
        }

        level.addFreshEntity(drone);

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.CONSUME;
    }
}