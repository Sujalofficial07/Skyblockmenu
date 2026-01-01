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
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MenuHandler extends GenericContainerScreenHandler {

    private static final int ROWS = 6;

    public MenuHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(ROWS * 9));
    }

    public MenuHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SkyblockMenu.MENU_HANDLER_TYPE, syncId, playerInventory, inventory, ROWS);
        
        // 1. Fill Background with Stained Glass Panes
        ItemStack filler = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        filler.setCustomName(Text.literal(" ")); // Empty name
        
        for (int i = 0; i < ROWS * 9; i++) {
            inventory.setStack(i, filler);
        }

        // 2. Setup Feature Icons
        
        // Slot 13: Your Profile
        ItemStack profile = new ItemStack(Items.PLAYER_HEAD);
        profile.setCustomName(Text.literal("§aYour SkyBlock Profile"));
        inventory.setStack(13, profile);

        // Slot 19: Skills (Diamond Sword)
        ItemStack skills = new ItemStack(Items.DIAMOND_SWORD);
        skills.setCustomName(Text.literal("§aYour Skills"));
        inventory.setStack(19, skills);

        // Slot 21: Collections (Painting)
        ItemStack collections = new ItemStack(Items.PAINTING);
        collections.setCustomName(Text.literal("§aCollections"));
        inventory.setStack(21, collections);

        // Slot 23: Recipe Book (Book)
        ItemStack recipes = new ItemStack(Items.BOOK);
        recipes.setCustomName(Text.literal("§aRecipe Book"));
        inventory.setStack(23, recipes);
        
        // Slot 25: Trades (Emerald)
        ItemStack trades = new ItemStack(Items.EMERALD);
        trades.setCustomName(Text.literal("§aTrades"));
        inventory.setStack(25, trades);

        // --- FAST TRAVEL SECTION (Bottom) ---

        // Slot 48: Warp to Hub (Golden Apple)
        ItemStack hubIcon = new ItemStack(Items.GOLDEN_APPLE);
        hubIcon.setCustomName(Text.literal("§b§lWarp to Hub"));
        inventory.setStack(48, hubIcon);

        // Slot 50: Warp to Island (Grass Block)
        ItemStack islandIcon = new ItemStack(Items.GRASS_BLOCK);
        islandIcon.setCustomName(Text.literal("§b§lWarp to Private Island"));
        inventory.setStack(50, islandIcon);
        
        // Slot 49: Close (Barrier) - Center Bottom
        ItemStack close = new ItemStack(Items.BARRIER);
        close.setCustomName(Text.literal("§cClose"));
        inventory.setStack(49, close);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // Prevent moving items in the menu (Top Inventory)
        if (slotIndex >= 0 && slotIndex < ROWS * 9) {
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                handleMenuClick(serverPlayer, slotIndex);
            }
            
            // STRICTLY CANCEL EVENT: Item cursor par chipakna nahi chahiye
            this.setCursorStack(ItemStack.EMPTY);
            return;
        }
        
        // Inventory move block (Shift click se menu mein item na jaye)
        if (actionType == SlotActionType.QUICK_MOVE) {
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private void handleMenuClick(ServerPlayerEntity player, int slotIndex) {
        boolean success = false;
        
        if (slotIndex == 48) { // Hub
            player.closeHandledScreen();
            success = SkyblockAPI.teleportTo(player, "hub");
            if (success) player.sendMessage(Text.literal("§aWarped to Hub!"), true);
        } 
        else if (slotIndex == 50) { // Island
            player.closeHandledScreen();
            success = SkyblockAPI.teleportTo(player, "island");
            if (success) player.sendMessage(Text.literal("§aWarped to Island!"), true);
        }
        else if (slotIndex == 49) { // Close
            player.closeHandledScreen();
        }
        else if (slotIndex == 19 || slotIndex == 21 || slotIndex == 23 || slotIndex == 25) {
             player.sendMessage(Text.literal("§cThis feature is coming in the next mod!"), true);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
                                          }
