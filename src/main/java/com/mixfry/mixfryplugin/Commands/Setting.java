package com.mixfry.mixfryplugin.Commands;

import com.mixfry.mixfryplugin.DeathPoint;
import com.mixfry.mixfryplugin.MineCombo;
import com.mixfry.mixfryplugin.RareDropAlert;
import com.mixfry.mixfryplugin.ToolExtention;
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

        // Slot4: コンボ音変更のアイコン
        ItemStack comboSoundChange = new ItemStack(Material.BEACON);
        ItemMeta comboSoundChangeMeta = comboSoundChange.getItemMeta();
        comboSoundChangeMeta.setDisplayName(ChatColor.GOLD + "採掘コンボ音変更");
        comboSoundChange.setItemMeta(comboSoundChangeMeta);
        inventory.setItem(4, comboSoundChange);

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

        // Slot13: コンボ音変更の音符ブロック
        ItemStack noteBlock = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta noteBlockMeta = noteBlock.getItemMeta();
        noteBlockMeta.setDisplayName(ChatColor.AQUA + "コンボ音変更");
        noteBlock.setItemMeta(noteBlockMeta);
        inventory.setItem(13, noteBlock);

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
                openComboSoundSettingInventory(player);
            }
        } else if (event.getView().getTitle().equals("コンボ音設定")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            Player player = (Player) event.getWhoClicked();
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openSettingInventory(player);
            } else {
                Sound sound = null;
                switch (clickedItem.getType()) {
                    case MINECART:
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
        }
    }

    public void openComboSoundSettingInventory(Player player) {
        Inventory comboSoundInventory = Bukkit.createInventory(null, 18, "コンボ音設定");

        // Slot0: 無音のトロッコ
        ItemStack noSound = new ItemStack(Material.MINECART);
        ItemMeta noSoundMeta = noSound.getItemMeta();
        noSoundMeta.setDisplayName(ChatColor.WHITE + "無音");
        noSound.setItemMeta(noSoundMeta);
        comboSoundInventory.setItem(0, noSound);

        // Slot1: チャイムの氷
        ItemStack chime = new ItemStack(Material.ICE);
        ItemMeta chimeMeta = chime.getItemMeta();
        chimeMeta.setDisplayName(ChatColor.AQUA + "チャイム");
        chimeMeta.setLore(Collections.singletonList(ChatColor.GRAY + "デフォルト音"));
        chime.setItemMeta(chimeMeta);
        comboSoundInventory.setItem(1, chime);

        // Slot2: カウベルのソウルサンド
        ItemStack cowbell = new ItemStack(Material.SOUL_SAND);
        ItemMeta cowbellMeta = cowbell.getItemMeta();
        cowbellMeta.setDisplayName(ChatColor.GOLD + "カウベル");
        cowbell.setItemMeta(cowbellMeta);
        comboSoundInventory.setItem(2, cowbell);

        // Slot3: 木琴の骨ブロック
        ItemStack xylophone = new ItemStack(Material.BONE_BLOCK);
        ItemMeta xylophoneMeta = xylophone.getItemMeta();
        xylophoneMeta.setDisplayName(ChatColor.YELLOW + "木琴");
        xylophone.setItemMeta(xylophoneMeta);
        comboSoundInventory.setItem(3, xylophone);

        // Slot4: フルートの粘土
        ItemStack flute = new ItemStack(Material.CLAY);
        ItemMeta fluteMeta = flute.getItemMeta();
        fluteMeta.setDisplayName(ChatColor.BLUE + "フルート");
        flute.setItemMeta(fluteMeta);
        comboSoundInventory.setItem(4, flute);

        // Slot5: 電子音のエメラルドブロック
        ItemStack bit = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta bitMeta = bit.getItemMeta();
        bitMeta.setDisplayName(ChatColor.GREEN + "電子音");
        bit.setItemMeta(bitMeta);
        comboSoundInventory.setItem(5, bit);

        // Slot6: 鉄琴の鉄ブロック
        ItemStack ironXylophone = new ItemStack(Material.IRON_BLOCK);
        ItemMeta ironXylophoneMeta = ironXylophone.getItemMeta();
        ironXylophoneMeta.setDisplayName(ChatColor.GRAY + "鉄琴");
        ironXylophone.setItemMeta(ironXylophoneMeta);
        comboSoundInventory.setItem(6, ironXylophone);

        // Slot7: ベルの金ブロック
        ItemStack bell = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta bellMeta = bell.getItemMeta();
        bellMeta.setDisplayName(ChatColor.GOLD + "ベル");
        bell.setItemMeta(bellMeta);
        comboSoundInventory.setItem(7, bell);

        // Slot8: バスドラムの石
        ItemStack bassDrum = new ItemStack(Material.STONE);
        ItemMeta bassDrumMeta = bassDrum.getItemMeta();
        bassDrumMeta.setDisplayName(ChatColor.DARK_GRAY + "バスドラム");
        bassDrum.setItemMeta(bassDrumMeta);
        comboSoundInventory.setItem(8, bassDrum);

        // Slot13: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        comboSoundInventory.setItem(13, backArrow);

        player.openInventory(comboSoundInventory);
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
}