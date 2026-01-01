package com.sujal.skyblockmenu;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SkyblockMenuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register Generic Container Screen (Auto handles 3-6 rows)
        HandledScreens.register(SkyblockMenu.MENU_HANDLER_TYPE, GenericContainerScreen::new);
    }
}
