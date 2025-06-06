package com.mixfry.mixfryplugin.Menu;

import com.mixfry.mixfryplugin.*;
import com.mixfry.mixfryplugin.Function.DeathPoint;
import com.mixfry.mixfryplugin.Function.RareDropAlert;
import com.mixfry.mixfryplugin.Function.ToolExtention;
import com.mixfry.mixfryplugin.Minecombo.Cosmetics.ComboSound.ComboSound;
import com.mixfry.mixfryplugin.Minecombo.MineCombo;
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
import org.bukkit.event.player.PlayerJoinEvent;
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
    private final DeathPoint deathPoint;
    private final MineCombo mineCombo;

    public Setting(JavaPlugin plugin) {
        this.plugin = plugin;
        this.toolExtention = ToolExtention.getInstance();
        this.rareDropAlert = RareDropAlert.getInstance();
        this.deathPoint = DeathPoint.getInstance();
        this.mineCombo = MineCombo.getInstance();
    }

    public void openSettingInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, "設定");

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

        // Slot2: 死亡地点通知のアイコン
        ItemStack deathPointNotification = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta deathPointMeta = deathPointNotification.getItemMeta();
        deathPointMeta.setDisplayName(ChatColor.GOLD + "死亡地点通知機能");
        deathPointNotification.setItemMeta(deathPointMeta);
        inventory.setItem(2, deathPointNotification);

        // Slot3: 採掘コンボのアイコン
        ItemStack mineComboNotification = new ItemStack(Material.BEACON);
        ItemMeta mineComboMeta = mineComboNotification.getItemMeta();
        mineComboMeta.setDisplayName(ChatColor.GOLD + "採掘コンボ");
        mineComboNotification.setItemMeta(mineComboMeta);
        inventory.setItem(3, mineComboNotification);

        // 設定を読み込む
        FileConfiguration config = loadPlayerConfig(player);
        boolean durabilityWarningEnabled = config.getBoolean("durabilityWarning", true);
        boolean rareDropNotificationEnabled = config.getBoolean("rareDropNotification", true);
        boolean deathPointNotificationEnabled = config.getBoolean("deathPointNotification", true);
        boolean mineComboEnabled = config.getBoolean("mineCombo", true);

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

        // Slot11: 死亡地点通知機能の羊毛
        ItemStack deathPointSetting = new ItemStack(deathPointNotificationEnabled ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta deathPointSettingMeta = deathPointSetting.getItemMeta();
        deathPointSettingMeta.setDisplayName(deathPointNotificationEnabled ? ChatColor.GREEN + "オン" : ChatColor.RED + "オフ");
        deathPointSettingMeta.setLore(java.util.Arrays.asList(ChatColor.DARK_GRAY + "死亡地点通知機能"));
        deathPointSetting.setItemMeta(deathPointSettingMeta);
        inventory.setItem(11, deathPointSetting);

        // Slot12: 採掘コンボの羊毛
        ItemStack mineComboSetting = new ItemStack(mineComboEnabled ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta mineComboSettingMeta = mineComboSetting.getItemMeta();
        mineComboSettingMeta.setDisplayName(mineComboEnabled ? ChatColor.GREEN + "オン" : ChatColor.RED + "オフ");
        mineComboSettingMeta.setLore(java.util.Arrays.asList(ChatColor.DARK_GRAY + "採掘コンボ"));
        mineComboSetting.setItemMeta(mineComboSettingMeta);
        inventory.setItem(12, mineComboSetting);

        // Slot18: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        inventory.setItem(18, backArrow);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("設定")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Player player = (Player) event.getWhoClicked();
            if (clickedItem.getType() == Material.GREEN_WOOL || clickedItem.getType() == Material.RED_WOOL) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (clickedItem.getType() == Material.GREEN_WOOL) {
                    meta.setDisplayName(ChatColor.RED + "オフ");
                    clickedItem.setType(Material.RED_WOOL);
                } else {
                    meta.setDisplayName(ChatColor.GREEN + "オン");
                    clickedItem.setType(Material.GREEN_WOOL);
                }
                clickedItem.setItemMeta(meta);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
            } else if (clickedItem.getType() == Material.NOTE_BLOCK && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "コンボ音変更")) {
                ComboSound comboSound = new ComboSound((MixfryPlugin) plugin);
                comboSound.openComboSoundSettingInventory(player);
            } else if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN , 1.0f, 1.0f);
                new Menu((MixfryPlugin) plugin).openMenu(player);
            }
        } else if (event.getView().getTitle().equals("コンボ音設定")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Player player = (Player) event.getWhoClicked();
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN , 1.0f, 1.0f);
                openSettingInventory(player);
            } else {
                Sound sound = null;
                switch (clickedItem.getType()) {
                    case BARRIER:
                        sound = null;
                        break;
                    case ICE:
                        sound = Sound.BLOCK_NOTE_BLOCK_CHIME;
                        break;
                    case SOUL_SAND:
                        sound = Sound.BLOCK_NOTE_BLOCK_COW_BELL;
                        break;
                    case BONE_BLOCK:
                        sound = Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
                        break;
                    case CLAY:
                        sound = Sound.BLOCK_NOTE_BLOCK_FLUTE;
                        break;
                    case EMERALD_BLOCK:
                        sound = Sound.BLOCK_NOTE_BLOCK_BIT;
                        break;
                    case IRON_BLOCK:
                        sound = Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
                        break;
                    case GOLD_BLOCK:
                        sound = Sound.BLOCK_NOTE_BLOCK_BELL;
                        break;
                    case STONE:
                        sound = Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
                        break;
                    default:
                        break;
                }
                if (sound != null) {
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                }
                saveComboSoundSetting(player, sound);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("設定")) {
            Inventory inventory = event.getInventory();
            boolean durabilityWarning = inventory.getItem(9).getType() == Material.GREEN_WOOL;
            boolean rareDropNotification = inventory.getItem(10).getType() == Material.GREEN_WOOL;
            boolean deathPointNotification = inventory.getItem(11).getType() == Material.GREEN_WOOL;
            boolean mineCombo = inventory.getItem(12).getType() == Material.GREEN_WOOL;
            saveSettings((Player) event.getPlayer(), durabilityWarning, rareDropNotification, deathPointNotification, mineCombo);
        }
    }

    private void saveComboSoundSetting(Player player, Sound sound) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("comboSound", sound == null ? "null" : sound.name());
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "コンボ音設定の保存中にエラーが発生しました。", e);
        }
    }

    private void saveSettings(Player player, boolean durabilityWarning, boolean rareDropNotification, boolean deathPointNotification, boolean mineComboEnabled) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("durabilityWarning", durabilityWarning);
        config.set("rareDropNotification", rareDropNotification);
        config.set("deathPointNotification", deathPointNotification);
        config.set("mineCombo", mineComboEnabled);
        try {
            config.save(configFile);
            toolExtention.setDurabilityWarningEnabled(durabilityWarning);
            rareDropAlert.setRareDropNotificationEnabled(rareDropNotification);
            deathPoint.setDeathPointNotificationEnabled(deathPointNotification);
            mineCombo.setMineComboEnabled(mineComboEnabled);
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

    public void loadSettings() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
            if (configFile.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                boolean durabilityWarning = config.getBoolean("durabilityWarning", true);
                boolean rareDropNotification = config.getBoolean("rareDropNotification", true);
                boolean deathPointNotification = config.getBoolean("deathPointNotification", true);
                boolean mineComboEnabled = config.getBoolean("mineCombo", true);

                toolExtention.setDurabilityWarningEnabled(durabilityWarning);
                rareDropAlert.setRareDropNotificationEnabled(rareDropNotification);
                deathPoint.setDeathPointNotificationEnabled(deathPointNotification);
                mineCombo.setMineComboEnabled(mineComboEnabled);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                config.set("durabilityWarning", true);
                config.set("rareDropNotification", true);
                config.set("deathPointNotification", true);
                config.set("mineCombo", true);
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "設定ファイルの作成中にエラーが発生しました。", e);
            }
        } else {
            // ymlの読み込み 多分これでいける
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            boolean durabilityWarning = config.getBoolean("durabilityWarning", true);
            boolean rareDropNotification = config.getBoolean("rareDropNotification", true);
            boolean deathPointNotification = config.getBoolean("deathPointNotification", true);
            boolean mineComboEnabled = config.getBoolean("mineCombo", true);

            // 設定を反映 これでうまくいってくれ頼む
            toolExtention.setDurabilityWarningEnabled(durabilityWarning);
            rareDropAlert.setRareDropNotificationEnabled(rareDropNotification);
            deathPoint.setDeathPointNotificationEnabled(deathPointNotification);
            mineCombo.setMineComboEnabled(mineComboEnabled);
        }
    }
}