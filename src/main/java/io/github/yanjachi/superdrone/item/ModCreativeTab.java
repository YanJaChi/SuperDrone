package io.github.yanjachi.superdrone.item;

import io.github.yanjachi.superdrone.screen.SuperDrone;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SuperDrone.MODID);

    //这里是添加物品栏的地方
    public static final RegistryObject<CreativeModeTab> SUPERDRONE_TAB = CREATIVE_MODE_TABS.register("superdrone_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.superdrone_tab"))
                    .icon(() -> new ItemStack(ModItem.DRONE.get())) // 这个物品的图标会变成物品栏的图标
                    .displayItems((parameters, output) -> {
                        output.accept(ModItem.DRONE_CORE.get());
                        output.accept(ModItem.DRONE.get());
                        output.accept(ModItem.GEM_CHIP.get());
                        output.accept(ModItem.BRASHLESS_MOTOR.get());
                        // 后续新增物品继续往这里加：
                        // output.accept(ModItems.XXX.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}