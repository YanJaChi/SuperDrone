package io.github.yanjachi.superdrone.entity;

import io.github.yanjachi.superdrone.item.ModItem;
import io.github.yanjachi.superdrone.item.RemoteControllerItem;
import io.github.yanjachi.superdrone.network.ModNetwork;
import io.github.yanjachi.superdrone.network.StartControlS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DroneEntity extends PathfinderMob {

    // playerUUID -> droneUUID
    private static final Map<UUID, UUID> CONTROLLING = new ConcurrentHashMap<>();
    private UUID controller;

    // 悬停状态：true 时保持空中静止
    private boolean hovering = false;

    public DroneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 3.0D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        // 1) 手持遥控器右键无人机：绑定
        if (!this.level().isClientSide && held.is(ModItem.REMOTE_CONTROLLER.get())) {
            RemoteControllerItem.bindDrone(held, this.getUUID());
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(Component.literal("已绑定无人机: " + this.getUUID()), true);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // 2) 普通右键：接管控制
        if (!this.level().isClientSide && player instanceof ServerPlayer sp) {
            setController(sp);
            setHovering(false); // 接管时取消悬停，恢复飞行控制

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new StartControlS2CPacket(this.getId())
            );
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    public void setController(ServerPlayer sp) {
        this.controller = sp.getUUID();
        CONTROLLING.put(sp.getUUID(), this.getUUID());
    }

    public boolean isControlledBy(ServerPlayer player) {
        return controller != null && controller.equals(player.getUUID());
    }

    public static DroneEntity getControlledDrone(ServerPlayer player) {
        UUID droneUuid = CONTROLLING.get(player.getUUID());
        if (droneUuid == null) return null;

        // 在玩家当前维度按 UUID 查找
        for (Entity e : player.serverLevel().getAllEntities()) {
            if (e instanceof DroneEntity drone && droneUuid.equals(drone.getUUID())) {
                return drone;
            }
        }

        // 当前维度暂时找不到，不立即清映射
        return null;
    }

    public static void stopControlling(ServerPlayer player) {
        CONTROLLING.remove(player.getUUID());
    }

    public void clearController() {
        this.controller = null;
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
        this.setNoGravity(hovering);

        if (hovering) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.hasImpulse = true;
            this.hurtMarked = true;
        }
    }

    public boolean isHovering() {
        return hovering;
    }

    @Override
    public void tick() {
        super.tick();

        // 服务端维持悬停
        if (!this.level().isClientSide && hovering) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.hasImpulse = true;
            this.hurtMarked = true;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (controller != null) {
            CONTROLLING.remove(controller);
        }
        super.remove(reason);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
}