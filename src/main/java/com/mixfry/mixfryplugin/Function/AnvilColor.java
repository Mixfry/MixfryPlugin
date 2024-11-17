package com.mixfry.mixfryplugin.Function;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilColor implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            if (event.getRawSlot() == 2) {
                ItemStack resultItem = event.getCurrentItem();
                if (resultItem != null) {
                    ItemMeta meta = resultItem.getItemMeta();
                    if (meta != null && meta.hasDisplayName()) {
                        String displayName = meta.getDisplayName();
                        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                        meta.setDisplayName(displayName);
                        resultItem.setItemMeta(meta);
                    }
                }
            }
        }
    }
}