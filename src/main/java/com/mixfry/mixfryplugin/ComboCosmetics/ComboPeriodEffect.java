package com.mixfry.mixfryplugin.ComboCosmetics;

import com.mixfry.mixfryplugin.Commands.Menu;
import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;

public class ComboPeriodEffect {

    private final MixfryPlugin plugin;

    public ComboPeriodEffect(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    public void openComboPeriodEffectSettingInventory(Player player) {
        Inventory comboPeriodEffectInventory = Bukkit.createInventory(null, 18, "コンボ演出設定");

        // Slot0: なしのバリアブロック
        ItemStack noEffect = getComboPeriodEffectItem(player);
        comboPeriodEffectInventory.setItem(0, noEffect);

        // Slot9: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        comboPeriodEffectInventory.setItem(9, backArrow);

        player.openInventory(comboPeriodEffectInventory);
    }

    public ItemStack getComboPeriodEffectItem(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String effectName = config.getString("comboPeriodEffect", "null");

        Material material;
        String displayName;

        if ("null".equals(effectName)) {
            material = Material.BARRIER;
            displayName = ChatColor.WHITE + "なし";
        } else {
            material = Material.BARRIER; // 他のエフェクトがある場合はここで設定
            displayName = ChatColor.WHITE + "なし";
        }

        ItemStack comboPeriodEffectItem = new ItemStack(material);
        ItemMeta comboPeriodEffectMeta = comboPeriodEffectItem.getItemMeta();
        comboPeriodEffectMeta.setDisplayName(displayName);
        comboPeriodEffectMeta.setLore(Collections.singletonList(ChatColor.GRAY + "コンボ演出設定"));
        comboPeriodEffectItem.setItemMeta(comboPeriodEffectMeta);

        return comboPeriodEffectItem;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("コンボ演出設定")) {
            return;
        }

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (clickedItem.getType() == Material.BARRIER && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "なし")) {
            saveComboPeriodEffectSetting(player, null);
            player.sendMessage(ChatColor.GREEN + "コンボ演出が無効になりました。");
            openComboPeriodEffectSettingInventory(player);
        } else if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
            new Menu(plugin).openMenu(player);
        }
    }

    public void saveComboPeriodEffectSetting(Player player, String effect) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("comboPeriodEffect", effect == null ? "null" : effect);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "コンボ演出設定の保存中にエラーが発生しました。", e);
        }
    }
}