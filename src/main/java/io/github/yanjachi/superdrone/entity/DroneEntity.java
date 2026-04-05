package io.github.yanjachi.superdrone.entity;

import io.github.yanjachi.superdrone.network.ModNetwork;
import io.github.yanjachi.superdrone.network.StartControlS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DroneEntity extends PathfinderMob {

    private static final Map<UUID, Integer> CONTROLLING = new ConcurrentHashMap<>();
    private UUID controller;

    public DroneEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 3.0D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer sp) {
            // 接管控制：进入新无人机时覆盖旧绑定
            controller = sp.getUUID();
            CONTROLLING.put(sp.getUUID(), this.getId());

            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new StartControlS2CPacket(this.getId())
            );
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false; // 免疫摔落伤害
    }

    public boolean isControlledBy(ServerPlayer player) {
        return controller != null && controller.equals(player.getUUID());
    }

    public static DroneEntity getControlledDrone(ServerPlayer player) {
        Integer id = CONTROLLING.get(player.getUUID());
        if (id == null) return null;
        if (player.serverLevel().getEntity(id) instanceof DroneEntity drone) {
            return drone;
        }
        CONTROLLING.remove(player.getUUID());
        return null;
    }

    public static void stopControlling(ServerPlayer player) {
        CONTROLLING.remove(player.getUUID());
    }

    public void clearController() {
        this.controller = null;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (controller != null) {
            CONTROLLING.remove(controller);
        }
        super.remove(reason);
    }
}