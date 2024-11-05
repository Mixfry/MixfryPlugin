package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;

public class Setting implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final ToolExtention toolExtention;
    private final RareDropAlert rareDropAlert;

    public Setting(JavaPlugin plugin) {
        this.plugin = plugin;
        this.toolExtention = ToolExtention.getInstance();
        this.rareDropAlert = RareDropAlert.getInstance();
    }

    public void openSettingInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 18, "設定");

        // Slot0: 耐久値警告機能のアイコン
        ItemStack durabilityWarning = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta durabilityMeta = durabilityWarning.getItemMeta();
        durabilityMeta.setDisplayName(ChatColor.GOLD + "残り耐久値の警告機能");
        durabilityMeta.setLore(Collections.emptyList());
        durabilityWarning.setItemMeta(durabilityMeta);
        durabilityWarning.setDurability((short) (durabilityWarning.getType().getMaxDurability() * 0.9));
        inventory.setItem(0, durabilityWarning);

        // Slot1: レアドロップ通知のアイコン
        ItemStack rareDropNotification = new ItemStack(Material.DIAMOND);
        ItemMeta rareDropMeta = rareDropNotification.getItemMeta();
        rareDropMeta.setDisplayName(ChatColor.GOLD + "レアドロップ通知機能");
        rareDropMeta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        rareDropMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        rareDropNotification.setItemMeta(rareDropMeta);
        inventory.setItem(1, rareDropNotification);

        // 設定を読み込む
        FileConfiguration config = loadPlayerConfig(player);
        boolean durabilityWarningEnabled = config.getBoolean("durabilityWarning", true);
        boolean rareDropNotificationEnabled = config.getBoolean("rareDropNotification", true);

        // Slot9: 残り耐久値の警告機能の羊毛
        ItemStack durabilitySetting = new ItemStack(durabilityWarningEnabled ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta durabilitySettingMeta = durabilitySetting.getItemMeta();
        durabilitySettingMeta.setDisplayName(durabilityWarningEnabled ? ChatColor.GREEN + "オン" : ChatColor.RED + "オフ");
        durabilitySettingMeta.setLore(java.util.Arrays.asList(ChatColor.DARK_GRAY + "残り耐久値の警告機能"));
        durabilitySetting.setItemMeta(durabilitySettingMeta);
        inventory.setItem(9, durabilitySetting);

        // Slot10: レアドロップ通知機能の羊毛
        ItemStack rareDropSetting = new ItemStack(rareDropNotificationEnabled ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta rareDropSettingMeta = rareDropSetting.getItemMeta();
        rareDropSettingMeta.setDisplayName(rareDropNotificationEnabled ? ChatColor.GREEN + "オン" : ChatColor.RED + "オフ");
        rareDropSettingMeta.setLore(java.util.Arrays.asList(ChatColor.DARK_GRAY + "レアドロップ通知機能"));
        rareDropSetting.setItemMeta(rareDropSettingMeta);
        inventory.setItem(10, rareDropSetting);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("設定")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Player player = (Player) event.getWhoClicked();
            if (clickedItem.getType() == Material.GREEN_WOOL) {
                ItemMeta meta = clickedItem.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "オフ");
                clickedItem.setType(Material.RED_WOOL);
                clickedItem.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
            } else if (clickedItem.getType() == Material.RED_WOOL) {
                ItemMeta meta = clickedItem.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "オン");
                clickedItem.setType(Material.GREEN_WOOL);
                clickedItem.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("設定")) {
            Inventory inventory = event.getInventory();
            boolean durabilityWarning = inventory.getItem(9).getType() == Material.GREEN_WOOL;
            boolean rareDropNotification = inventory.getItem(10).getType() == Material.GREEN_WOOL;
            saveSettings((Player) event.getPlayer(), durabilityWarning, rareDropNotification);
        }
    }

    private void saveSettings(Player player, boolean durabilityWarning, boolean rareDropNotification) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("durabilityWarning", durabilityWarning);
        config.set("rareDropNotification", rareDropNotification);
        try {
            config.save(configFile);
            toolExtention.setDurabilityWarningEnabled(durabilityWarning);
            rareDropAlert.setRareDropNotificationEnabled(rareDropNotification);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "設定の保存中にエラーが発生しました。", e);
        }
    }

    private FileConfiguration loadPlayerConfig(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "設定ファイルの作成中にエラーが発生しました。", e);
            }
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーからのみ実行可能です。");
            return true;
        }

        Player player = (Player) sender;
        openSettingInventory(player);
        return true;
    }
}