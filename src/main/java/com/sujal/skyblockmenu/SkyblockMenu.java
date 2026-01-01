package com.sujal.skyblockmenu;

import com.sujal.skyblockmenu.gui.MenuHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

public class SkyblockMenu implements ModInitializer {

    public static final String MOD_ID = "skyblockmenu";
    
    // Register Screen Handler Type
    public static final ScreenHandlerType<MenuHandler> MENU_HANDLER_TYPE = new ScreenHandlerType<>(MenuHandler::new, FeatureSet.empty());

    @Override
    public void onInitialize() {
        
        // Register the Menu Type
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "menu"), MENU_HANDLER_TYPE);

        // Command: /sbmenu
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("sbmenu")
                .executes(context -> {
                    openMenu(context.getSource().getPlayer());
                    return 1;
                }));
        });

        // Event: Right Click with Nether Star
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && hand == Hand.MAIN_HAND) {
                if (player.getStackInHand(hand).getItem() == Items.NETHER_STAR) {
                    openMenu((ServerPlayerEntity) player);
                    return TypedActionResult.success(player.getStackInHand(hand));
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }

    private void openMenu(ServerPlayerEntity player) {
        player.openHandledScreen(new net.minecraft.screen.SimpleNamedScreenHandlerFactory(
            (syncId, inventory, p) -> new MenuHandler(syncId, inventory, new SimpleInventory(27)),
            Text.literal("SkyBlock Menu")
        ));
    }
}
