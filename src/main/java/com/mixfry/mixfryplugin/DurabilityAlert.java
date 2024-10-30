package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DurabilityAlert implements Listener {
    private final Map<UUID, Map<Material, Boolean>> alertedItems = new HashMap<>();

    public DurabilityAlert() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Material itemType = item.getType();
        int maxDurability = item.getType().getMaxDurability();
        int currentDurability = maxDurability - item.getDurability();

        if (currentDurability <= maxDurability * 0.1) {
            UUID playerUUID = player.getUniqueId();
            Map<Material, Boolean> playerAlertedItems = alertedItems.computeIfAbsent(playerUUID, k -> new HashMap<>());

            if (!playerAlertedItems.getOrDefault(itemType, false)) {
                player.sendMessage(ChatColor.RED + item.getType().name() + "の耐久度が10%を下回りました！");
                playerAlertedItems.put(itemType, true);
            }
        }
    }
}