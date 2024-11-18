package com.mixfry.mixfryplugin.Minecombo.Cosmetics.ComboSound;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;

public class GetSound {

    public static ItemStack getComboSoundItem(MixfryPlugin plugin, Player player) {
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
}