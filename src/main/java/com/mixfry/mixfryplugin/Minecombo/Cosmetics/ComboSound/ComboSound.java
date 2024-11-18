package com.mixfry.mixfryplugin.Minecombo.Cosmetics.ComboSound;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ComboSound {

    private final MixfryPlugin plugin;
    private final SaveSound saveSound;

    public ComboSound(MixfryPlugin plugin) {
        this.plugin = plugin;
        this.saveSound = new SaveSound(plugin);
    }

    public void openComboSoundSettingInventory(Player player) {
        Inventory comboSoundInventory = Bukkit.createInventory(player, 18, "コンボ音設定");

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

        ItemStack selectedItem = GetSound.getComboSoundItem(plugin, player);
        if (selectedItem != null) {
            ItemMeta selectedMeta = selectedItem.getItemMeta();
            selectedMeta.addEnchant(Enchantment.LUCK, 1, true);
            selectedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            selectedItem.setItemMeta(selectedMeta);

            for (int i = 0; i < comboSoundInventory.getSize(); i++) {
                ItemStack item = comboSoundInventory.getItem(i);
                if (item != null && item.getType() == selectedItem.getType()) {
                    comboSoundInventory.setItem(i, selectedItem);
                    break;
                }
            }
        }

        player.openInventory(comboSoundInventory);
    }

    public void saveComboSoundSetting(Player player, Sound sound) {
        saveSound.saveComboSoundSetting(player, sound);
    }
}