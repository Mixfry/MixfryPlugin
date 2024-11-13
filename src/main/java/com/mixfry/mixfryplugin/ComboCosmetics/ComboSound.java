package com.mixfry.mixfryplugin.ComboCosmetics;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;

public class ComboSound {

    private final MixfryPlugin plugin;

    public ComboSound(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    public Sound ComboSoundIcon(Material material) {
        switch (material) {
            case BARRIER:
                return null;
            case ICE:
                return Sound.BLOCK_NOTE_BLOCK_CHIME;
            case SOUL_SAND:
                return Sound.BLOCK_NOTE_BLOCK_COW_BELL;
            case BONE_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
            case CLAY:
                return Sound.BLOCK_NOTE_BLOCK_FLUTE;
            case EMERALD_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_BIT;
            case IRON_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
            case GOLD_BLOCK:
                return Sound.BLOCK_NOTE_BLOCK_BELL;
            case STONE:
                return Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
            default:
                return null;
        }
    }

    public ItemStack getComboSoundItem(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String soundName = config.getString("comboSound", "null");

        Material material;
        String displayName;

        switch (soundName) {
            case "BLOCK_NOTE_BLOCK_CHIME":
                material = Material.ICE;
                displayName = ChatColor.AQUA + "チャイム";
                break;
            case "BLOCK_NOTE_BLOCK_COW_BELL":
                material = Material.SOUL_SAND;
                displayName = ChatColor.GOLD + "カウベル";
                break;
            case "BLOCK_NOTE_BLOCK_XYLOPHONE":
                material = Material.BONE_BLOCK;
                displayName = ChatColor.YELLOW + "木琴";
                break;
            case "BLOCK_NOTE_BLOCK_FLUTE":
                material = Material.CLAY;
                displayName = ChatColor.BLUE + "フルート";
                break;
            case "BLOCK_NOTE_BLOCK_BIT":
                material = Material.EMERALD_BLOCK;
                displayName = ChatColor.GREEN + "電子音";
                break;
            case "BLOCK_NOTE_BLOCK_IRON_XYLOPHONE":
                material = Material.IRON_BLOCK;
                displayName = ChatColor.GRAY + "鉄琴";
                break;
            case "BLOCK_NOTE_BLOCK_BELL":
                material = Material.GOLD_BLOCK;
                displayName = ChatColor.GOLD + "ベル";
                break;
            case "BLOCK_NOTE_BLOCK_BASEDRUM":
                material = Material.STONE;
                displayName = ChatColor.DARK_GRAY + "バスドラム";
                break;
            default:
                material = Material.BARRIER;
                displayName = ChatColor.WHITE + "無音";
                break;
        }

        ItemStack comboSoundItem = new ItemStack(material);
        ItemMeta comboSoundMeta = comboSoundItem.getItemMeta();
        comboSoundMeta.setDisplayName(displayName);
        comboSoundMeta.setLore(Collections.singletonList(ChatColor.GRAY + "コンボ音設定"));
        comboSoundItem.setItemMeta(comboSoundMeta);

        return comboSoundItem;
    }

    public void openComboSoundSettingInventory(Player player) {
        Inventory comboSoundInventory = Bukkit.createInventory(null, 18, "コンボ音設定");

        // Slot0: 無音のバリアブロック
        ItemStack noSound = new ItemStack(Material.BARRIER);
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

        // Slot2: バスドラムの石
        ItemStack bassDrum = new ItemStack(Material.STONE);
        ItemMeta bassDrumMeta = bassDrum.getItemMeta();
        bassDrumMeta.setDisplayName(ChatColor.DARK_GRAY + "バスドラム");
        bassDrum.setItemMeta(bassDrumMeta);
        comboSoundInventory.setItem(2, bassDrum);

        // Slot3: カウベルのソウルサンド
        ItemStack cowbell = new ItemStack(Material.SOUL_SAND);
        ItemMeta cowbellMeta = cowbell.getItemMeta();
        cowbellMeta.setDisplayName(ChatColor.GOLD + "カウベル");
        cowbell.setItemMeta(cowbellMeta);
        comboSoundInventory.setItem(3, cowbell);

        // Slot4: 木琴の骨ブロック
        ItemStack xylophone = new ItemStack(Material.BONE_BLOCK);
        ItemMeta xylophoneMeta = xylophone.getItemMeta();
        xylophoneMeta.setDisplayName(ChatColor.YELLOW + "木琴");
        xylophone.setItemMeta(xylophoneMeta);
        comboSoundInventory.setItem(4, xylophone);

        // Slot5: 鉄琴の鉄ブロック
        ItemStack ironXylophone = new ItemStack(Material.IRON_BLOCK);
        ItemMeta ironXylophoneMeta = ironXylophone.getItemMeta();
        ironXylophoneMeta.setDisplayName(ChatColor.GRAY + "鉄琴");
        ironXylophone.setItemMeta(ironXylophoneMeta);
        comboSoundInventory.setItem(5, ironXylophone);

        // Slot6: ベルの金ブロック
        ItemStack bell = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta bellMeta = bell.getItemMeta();
        bellMeta.setDisplayName(ChatColor.GOLD + "ベル");
        bell.setItemMeta(bellMeta);
        comboSoundInventory.setItem(6, bell);

        // Slot7: フルートの粘土
        ItemStack flute = new ItemStack(Material.CLAY);
        ItemMeta fluteMeta = flute.getItemMeta();
        fluteMeta.setDisplayName(ChatColor.BLUE + "フルート");
        flute.setItemMeta(fluteMeta);
        comboSoundInventory.setItem(7, flute);

        // Slot8: 電子音のエメラルドブロック
        ItemStack bit = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta bitMeta = bit.getItemMeta();
        bitMeta.setDisplayName(ChatColor.GREEN + "電子音");
        bit.setItemMeta(bitMeta);
        comboSoundInventory.setItem(8, bit);

        // Slot9: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        comboSoundInventory.setItem(9, backArrow);

        player.openInventory(comboSoundInventory);
    }

    public void saveComboSoundSetting(Player player, Sound sound) {
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