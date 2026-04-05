package io.github.yanjachi.superdrone.item;

import io.github.yanjachi.superdrone.entity.DroneEntity;
import io.github.yanjachi.superdrone.network.ModNetwork;
import io.github.yanjachi.superdrone.network.StartControlS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class RemoteControllerItem extends Item {
    private static final String TAG_BOUND_DRONE = "BoundDrone";

    public RemoteControllerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void bindDrone(ItemStack stack, UUID droneUuid) {
        stack.getOrCreateTag().putUUID(TAG_BOUND_DRONE, droneUuid);
    }

    public static UUID getBoundDrone(ItemStack stack) {
        if (!stack.hasTag()) return null;
        if (!stack.getTag().hasUUID(TAG_BOUND_DRONE)) return null;
        return stack.getTag().getUUID(TAG_BOUND_DRONE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        UUID bound = getBoundDrone(stack);
        if (bound == null) {
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(Component.literal("遥控器尚未绑定无人机"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // 在附近找已绑定 UUID 的无人机（可改成全维度管理）
        AABB box = player.getBoundingBox().inflate(1024);
        List<DroneEntity> drones = level.getEntitiesOfClass(DroneEntity.class, box);
        DroneEntity target = null;
        for (DroneEntity d : drones) {
            if (bound.equals(d.getUUID())) {
                target = d;
                break;
            }
        }

        if (target == null) {
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(Component.literal("未找到绑定的无人机（距离过远或已不存在）"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof ServerPlayer sp) {
            // 建立控制关系（复用你现有映射）
            target.setController(sp); // 你需要在 DroneEntity 增加此方法
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new StartControlS2CPacket(target.getId())
            );
        }

        return InteractionResultHolder.success(stack);
    }
}