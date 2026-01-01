package com.sujal.skyblockmenu.gui;

import com.sujal.skyblockcore.api.SkyblockAPI;
import com.sujal.skyblockmenu.SkyblockMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MenuHandler extends GenericContainerScreenHandler {

    public MenuHandler(int syncId, PlayerInventory playerInventory) {
        // Client side empty constructor
        this(syncId, playerInventory, new SimpleInventory(27));
    }

    public MenuHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SkyblockMenu.MENU_HANDLER_TYPE, syncId, playerInventory, inventory, 3);
        
        // --- Setup Menu Icons ---
        // Slot 11: HUB (Nether Star)
        inventory.setStack(11, new ItemStack(Items.NETHER_STAR).setCustomName(Text.literal("§b§lWarp to Hub")));
        
        // Slot 13: ISLAND (Grass Block)
        inventory.setStack(13, new ItemStack(Items.GRASS_BLOCK).setCustomName(Text.literal("§a§lWarp to Island")));
        
        // Slot 15: CLOSE (Barrier)
        inventory.setStack(15, new ItemStack(Items.BARRIER).setCustomName(Text.literal("§cClose Menu")));
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // Prevent taking items out of the menu
        if (slotIndex >= 0 && slotIndex < 27) { // Top inventory (Menu)
            if (player instanceof ServerPlayerEntity serverPlayer) {
                handleMenuClick(serverPlayer, slotIndex);
            }
            // Cancel the event visually
            this.setCursorStack(ItemStack.EMPTY);
            return;
        }
        
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private void handleMenuClick(ServerPlayerEntity player, int slotIndex) {
        boolean success = false;
        
        if (slotIndex == 11) { // Hub
            player.closeHandledScreen();
            success = SkyblockAPI.teleportTo(player, "hub");
            if(success) player.sendMessage(Text.literal("§eWarping to Hub..."), true);
        } 
        else if (slotIndex == 13) { // Island
            player.closeHandledScreen();
            success = SkyblockAPI.teleportTo(player, "island");
            if(success) player.sendMessage(Text.literal("§eWarping to Island..."), true);
        }
        else if (slotIndex == 15) { // Close
            player.closeHandledScreen();
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
