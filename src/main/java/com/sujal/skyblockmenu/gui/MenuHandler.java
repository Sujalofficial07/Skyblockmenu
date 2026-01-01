package com.sujal.skyblockmenu.gui;

import com.sujal.skyblockcore.api.SkyblockAPI;
import com.sujal.skyblockmenu.SkyblockMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

public class MenuHandler extends GenericContainerScreenHandler {

    private static final int ROWS = 6;
    private final Inventory menuInventory; // <-- Fix: Store reference here

    public MenuHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(ROWS * 9));
    }

    public MenuHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SkyblockMenu.MENU_HANDLER_TYPE, syncId, playerInventory, inventory, ROWS);
        this.menuInventory = inventory; // <-- Fix: Save inventory reference
        
        // --- 1. GUI SETUP (Visuals) ---
        
        // Background Filler
        ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        filler.setCustomName(Text.empty());
        for (int i = 0; i < ROWS * 9; i++) {
            inventory.setStack(i, filler);
        }

        // Icons
        ItemStack profile = new ItemStack(Items.PLAYER_HEAD);
        profile.setCustomName(Text.literal("§aYour SkyBlock Profile"));
        setLore(profile, "§7View your character stats,", "§7progress, and more!");
        inventory.setStack(13, profile);

        ItemStack skills = new ItemStack(Items.DIAMOND_SWORD);
        skills.setCustomName(Text.literal("§aYour Skills"));
        setLore(skills, "§7View your skill levels", "§7and rewards.", "", "§eClick to view!");
        inventory.setStack(19, skills);

        ItemStack collections = new ItemStack(Items.PAINTING);
        collections.setCustomName(Text.literal("§aCollections"));
        setLore(collections, "§7View your progress in", "§7unlocking items and recipes.", "", "§eClick to view!");
        inventory.setStack(21, collections);

        ItemStack recipes = new ItemStack(Items.BOOK);
        recipes.setCustomName(Text.literal("§aRecipe Book"));
        setLore(recipes, "§7Review all the recipes", "§7you have unlocked.", "", "§eClick to view!");
        inventory.setStack(23, recipes);
        
        ItemStack trades = new ItemStack(Items.EMERALD);
        trades.setCustomName(Text.literal("§aTrades"));
        setLore(trades, "§7Unlock trades by leveling", "§7up your collections.", "", "§eClick to view!");
        inventory.setStack(25, trades);

        ItemStack hubIcon = new ItemStack(Items.GOLDEN_APPLE);
        hubIcon.setCustomName(Text.literal("§b§lWarp to Hub"));
        setLore(hubIcon, "§7Teleport to the", "§7Hub island.", "", "§eClick to warp!");
        inventory.setStack(48, hubIcon);

        ItemStack islandIcon = new ItemStack(Items.GRASS_BLOCK);
        islandIcon.setCustomName(Text.literal("§b§lWarp to Private Island"));
        setLore(islandIcon, "§7Teleport to your", "§7private island.", "", "§eClick to warp!");
        inventory.setStack(50, islandIcon);
        
        ItemStack close = new ItemStack(Items.BARRIER);
        close.setCustomName(Text.literal("§cClose Menu"));
        inventory.setStack(49, close);
    }

    private void setLore(ItemStack stack, String... lines) {
        NbtList loreList = new NbtList();
        Arrays.stream(lines).forEach(line -> 
            loreList.add(NbtString.of(Text.Serializer.toJson(Text.literal(line))))
        );
        stack.getOrCreateSubNbt("display").put("Lore", loreList);
    }

    // --- 2. SECURITY LAYERS (Prevention) ---

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // SECURITY 1: Agar click MENU (Top Inventory 0-53) ke andar hai
        if (slotIndex >= 0 && slotIndex < ROWS * 9) {
            
            if (player instanceof ServerPlayerEntity serverPlayer && actionType == SlotActionType.PICKUP) {
                handleMenuClick(serverPlayer, slotIndex);
            }
            
            this.setCursorStack(ItemStack.EMPTY); 
            player.getInventory().markDirty(); 
            return; 
        }
        
        if (actionType == SlotActionType.QUICK_MOVE) {
            return;
        }

        if (actionType == SlotActionType.THROW && slotIndex < ROWS * 9 && slotIndex >= 0) {
            return;
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY; 
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        // Fix: Use 'this.menuInventory' instead of 'this.inventory'
        return slot.inventory != this.menuInventory; 
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
