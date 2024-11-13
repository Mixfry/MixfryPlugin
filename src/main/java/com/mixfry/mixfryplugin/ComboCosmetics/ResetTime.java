package com.mixfry.mixfryplugin.ComboCosmetics;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ResetTime {

    private final MixfryPlugin plugin;

    public ResetTime(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    public void openResetTimeSettingInventory(Player player) {
        Inventory resetTimeInventory = Bukkit.createInventory(null, 18, "リセット時間設定");

        // Slot9: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        resetTimeInventory.setItem(9, backArrow);

        player.openInventory(resetTimeInventory);
    }
}