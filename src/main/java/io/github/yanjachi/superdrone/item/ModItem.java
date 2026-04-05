package io.github.yanjachi.superdrone.item;

import io.github.yanjachi.superdrone.effects.DroneSpawnItem;
import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SuperDrone.MODID);

    // 这里是注册物品的地方
    public static final RegistryObject<Item> DRONE_CORE = ITEMS.register("drone_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRONE = ITEMS.register("drone",
            () -> new DroneSpawnItem(new Item.Properties().stacksTo(1).durability(300)));
    public static final RegistryObject<Item> GEM_CHIP = ITEMS.register("gem_chip",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRASHLESS_MOTOR = ITEMS.register("brashless_motor",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REMOTE_CONTROL = ITEMS.register("remote_control",
            () -> new Item(new Item.Properties()));

    // 你后续可以继续这样加：
    // public static final RegistryObject<Item> BATTERY = ITEMS.register("battery",
    //         () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}