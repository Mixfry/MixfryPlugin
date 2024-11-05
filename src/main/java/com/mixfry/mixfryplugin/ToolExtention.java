package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToolExtention implements Listener {

    private static ToolExtention instance;
    private final Map<UUID, Map<UUID, Boolean>> alertedItems = new HashMap<>();
    private boolean durabilityWarningEnabled = true;

    private ToolExtention() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
    }

    public static ToolExtention getInstance() {
        if (instance == null) {
            instance = new ToolExtention();
        }
        return instance;
    }

    public void setDurabilityWarningEnabled(boolean enabled) {
        this.durabilityWarningEnabled = enabled;
    }

    public void loadSettings(Player player) {
        FileConfiguration config = MixfryPlugin.getInstance().getPlayerConfig(player);
        durabilityWarningEnabled = config.getBoolean("durabilityWarning", true);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (!durabilityWarningEnabled) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Material itemType = item.getType();
        int maxDurability = item.getType().getMaxDurability();
        int currentDurability = maxDurability - item.getDurability() - event.getDamage();

        NamespacedKey key = new NamespacedKey(MixfryPlugin.getInstance(), "itemUUID");
        String itemUUIDString = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        UUID itemUUID;
        if (itemUUIDString == null) {
            itemUUID = UUID.randomUUID();
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, itemUUID.toString());
            item.setItemMeta(meta);
        } else {
            itemUUID = UUID.fromString(itemUUIDString);
        }

        UUID playerUUID = player.getUniqueId();
        Map<UUID, Boolean> playerAlertedItems = alertedItems.computeIfAbsent(playerUUID, k -> new HashMap<>());

        if (currentDurability <= maxDurability * 0.1 && !playerAlertedItems.getOrDefault(itemUUID, false)) {
            ItemMeta meta = item.getItemMeta();
            String itemName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name();
            player.sendMessage(ChatColor.RED + itemName + "の耐久度が10%を下回りました！");
            playerAlertedItems.put(itemUUID, true);
        } else if (currentDurability > maxDurability * 0.1) {
            playerAlertedItems.put(itemUUID, false);
        }
    }
}