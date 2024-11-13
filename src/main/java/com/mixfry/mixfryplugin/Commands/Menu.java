package com.mixfry.mixfryplugin.Commands;

import com.mixfry.mixfryplugin.MineComboShop;
import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Menu implements CommandExecutor, Listener {

    private final MixfryPlugin plugin;

    public Menu(MixfryPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("menu").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーからのみ実行可能です。");
            return true;
        }

        Player player = (Player) sender;
        openMenu(player);
        return true;
    }

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "メニュー");

        // slot0: クッキークリッカー
        ItemStack cookieItem = new ItemStack(Material.COOKIE);
        ItemMeta cookieMeta = cookieItem.getItemMeta();
        cookieMeta.setDisplayName(ChatColor.GOLD + "クッキークリッカー");
        cookieItem.setItemMeta(cookieMeta);

        // slot1: コンボショップ
        ItemStack comboShopItem = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta comboShopMeta = comboShopItem.getItemMeta();
        comboShopMeta.setDisplayName(ChatColor.GOLD + "コンボショップ");
        comboShopMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        comboShopMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        comboShopItem.setItemMeta(comboShopMeta);

        // slot8: 設定
        ItemStack settingsItem = new ItemStack(Material.ANVIL);
        ItemMeta settingsMeta = settingsItem.getItemMeta();
        settingsMeta.setDisplayName(ChatColor.GOLD + "設定");
        settingsItem.setItemMeta(settingsMeta);

        menu.setItem(0, cookieItem);
        menu.setItem(1, comboShopItem);
        menu.setItem(8, settingsItem);
        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("メニュー")) {
            return;
        }

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (clickedItem.getType() == Material.COOKIE && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "クッキークリッカー")) {
            CookieClicker cookieClicker = new CookieClicker();
            cookieClicker.openCookieInventory(player);
        } else if (clickedItem.getType() == Material.GOLDEN_PICKAXE && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "コンボショップ")) {
            MineComboShop mineComboShop = new MineComboShop(plugin);
            mineComboShop.openComboShop(player);
        } else if (clickedItem.getType() == Material.ANVIL && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "設定")) {
            Setting setting = new Setting(plugin);
            setting.openSettingInventory(player);
        }
    }
}