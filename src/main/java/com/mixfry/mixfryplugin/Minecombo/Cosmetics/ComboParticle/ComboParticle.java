package com.mixfry.mixfryplugin.Minecombo.Cosmetics.ComboParticle;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;

public class ComboParticle {

    private final MixfryPlugin plugin;

    public ComboParticle(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    public Particle ComboParticleIcon(Material material) {
        switch (material) {
            case EMERALD:
                return Particle.VILLAGER_HAPPY;
            case IRON_SWORD:
                return Particle.CRIT;
            case BLAZE_POWDER:
                return Particle.FLAME;
            default:
                return null;
        }
    }

    public ItemStack getComboParticleItem(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String particleName = config.getString("comboParticle", "null");

        Material material;
        String displayName;

        switch (particleName) {
            case "VILLAGER_HAPPY":
                material = Material.EMERALD;
                displayName = ChatColor.GREEN + "ハッピービレジャー";
                break;
            case "CRIT":
                material = Material.IRON_SWORD;
                displayName = ChatColor.GRAY + "クリティカル";
                break;
            case "FLAME":
                material = Material.BLAZE_POWDER;
                displayName = ChatColor.RED + "炎";
                break;
            default:
                material = Material.BARRIER;
                displayName = ChatColor.WHITE + "なし";
                break;
        }

        ItemStack comboParticleItem = new ItemStack(material);
        ItemMeta comboParticleMeta = comboParticleItem.getItemMeta();
        comboParticleMeta.setDisplayName(displayName);
        comboParticleMeta.setLore(Collections.singletonList(ChatColor.GRAY + "コンボパーティクル設定"));
        comboParticleItem.setItemMeta(comboParticleMeta);

        return comboParticleItem;
    }

    public void openComboParticleSettingInventory(Player player) {
        Inventory comboParticleInventory = Bukkit.createInventory(player, 18, "コンボパーティクル設定");

        // Slot0: なしのバリアブロック
        ItemStack noParticle = new ItemStack(Material.BARRIER);
        ItemMeta noParticleMeta = noParticle.getItemMeta();
        noParticleMeta.setDisplayName(ChatColor.WHITE + "なし");
        noParticle.setItemMeta(noParticleMeta);
        comboParticleInventory.setItem(0, noParticle);

        // Slot1: ハッピービレジャーのエメラルド
        ItemStack villagerHappy = new ItemStack(Material.EMERALD);
        ItemMeta villagerHappyMeta = villagerHappy.getItemMeta();
        villagerHappyMeta.setDisplayName(ChatColor.GREEN + "ハッピービレジャー");
        villagerHappy.setItemMeta(villagerHappyMeta);
        comboParticleInventory.setItem(1, villagerHappy);

        // Slot2: クリティカルの鉄の剣
        ItemStack crit = new ItemStack(Material.IRON_SWORD);
        ItemMeta critMeta = crit.getItemMeta();
        critMeta.setDisplayName(ChatColor.GRAY + "クリティカル");
        crit.setItemMeta(critMeta);
        comboParticleInventory.setItem(2, crit);

        // Slot3: 炎のブレイズパウダー
        ItemStack flame = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta flameMeta = flame.getItemMeta();
        flameMeta.setDisplayName(ChatColor.RED + "炎");
        flame.setItemMeta(flameMeta);
        comboParticleInventory.setItem(3, flame);

        // Slot9: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        comboParticleInventory.setItem(9, backArrow);

        ItemStack selectedItem = getComboParticleItem(player);
        if (selectedItem != null) {
            ItemMeta selectedMeta = selectedItem.getItemMeta();
            selectedMeta.addEnchant(Enchantment.LUCK, 1, true);
            selectedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            selectedItem.setItemMeta(selectedMeta);

            for (int i = 0; i < comboParticleInventory.getSize(); i++) {
                ItemStack item = comboParticleInventory.getItem(i);
                if (item != null && item.getType() == selectedItem.getType()) {
                    comboParticleInventory.setItem(i, selectedItem);
                    break;
                }
            }
        }

        player.openInventory(comboParticleInventory);
    }

    public void saveComboParticleSetting(Player player, Particle particle) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("comboParticle", particle == null ? "null" : particle.name());
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "コンボパーティクル設定の保存中にエラーが発生しました。", e);
        }
    }
}