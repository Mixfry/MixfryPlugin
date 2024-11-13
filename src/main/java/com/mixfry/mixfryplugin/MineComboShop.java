package com.mixfry.mixfryplugin;

import com.mixfry.mixfryplugin.Commands.Menu;
import com.mixfry.mixfryplugin.ComboCosmetics.ComboDisplay;
import com.mixfry.mixfryplugin.ComboCosmetics.ComboParticle;
import com.mixfry.mixfryplugin.ComboCosmetics.ComboPeriodEffect;
import com.mixfry.mixfryplugin.ComboCosmetics.ComboSound;
import com.mixfry.mixfryplugin.ComboCosmetics.ResetTime;
import com.mixfry.mixfryplugin.ComboCosmetics.TotalPointBonus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineComboShop implements Listener {

    private final MixfryPlugin plugin;
    private final ComboSound comboSound;
    private final ComboParticle comboParticle;
    private final ComboDisplay comboDisplay;
    private final ComboPeriodEffect comboPeriodEffect;
    private final ResetTime resetTime;
    private final Map<UUID, Integer> playerComboPoints = new HashMap<>();
    private final Map<UUID, Integer> playerTotalComboPoints = new HashMap<>();

    public MineComboShop(MixfryPlugin plugin) {
        this.plugin = plugin;
        this.comboSound = new ComboSound(plugin);
        this.comboParticle = new ComboParticle(plugin);
        this.comboDisplay = new ComboDisplay(plugin);
        this.comboPeriodEffect = new ComboPeriodEffect(plugin);
        this.resetTime = new ResetTime(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openComboShop(Player player) {
        UUID playerId = player.getUniqueId();
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            int comboPoints = config.getInt("comboPoints", 0);
            int totalComboPoints = config.getInt("totalComboPoints", 0);
            playerComboPoints.put(playerId, comboPoints);
            playerTotalComboPoints.put(playerId, totalComboPoints);
        } else {
            playerComboPoints.put(playerId, 0);
            playerTotalComboPoints.put(playerId, 0);
        }

        Inventory comboShop = Bukkit.createInventory(null, 36, "コンボショップ");

        // slot4: レベル頭
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        headMeta.setOwningPlayer(player);
        int totalPoints = playerTotalComboPoints.getOrDefault(playerId, 0);
        int level = totalPoints / 10;
        int progress = totalPoints % 10;
        headMeta.setDisplayName(ChatColor.GOLD + String.valueOf(level) + "レベル");
        String progressBar = ChatColor.GREEN + "[" + generateProgressBar(progress) + "] " + progress + "/10";
        headMeta.setLore(Arrays.asList(progressBar, ChatColor.DARK_GRAY + "10ポイント毎に1レベルUP"));
        playerHead.setItemMeta(headMeta);
        comboShop.setItem(4, playerHead);

        // slot9: コンボの見た目
        ItemStack appearanceItem = new ItemStack(Material.NAME_TAG);
        ItemMeta appearanceMeta = appearanceItem.getItemMeta();
        appearanceMeta.setDisplayName(ChatColor.GOLD + "コンボの見た目");
        appearanceItem.setItemMeta(appearanceMeta);
        comboShop.setItem(9, appearanceItem);

        // slot10: コンボ音
        ItemStack soundItem = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta soundMeta = soundItem.getItemMeta();
        soundMeta.setDisplayName(ChatColor.GOLD + "コンボ音");
        soundItem.setItemMeta(soundMeta);
        comboShop.setItem(10, soundItem);

        // slot11: コンボパーティクル
        ItemStack particleItem = new ItemStack(Material.FIREWORK_STAR);
        ItemMeta particleMeta = particleItem.getItemMeta();
        particleMeta.setDisplayName(ChatColor.GOLD + "コンボパーティクル");
        particleItem.setItemMeta(particleMeta);
        comboShop.setItem(11, particleItem);

        // slot12: 100コンボごとの演出
        ItemStack fireworkItem = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta fireworkMeta = fireworkItem.getItemMeta();
        fireworkMeta.setDisplayName(ChatColor.GOLD + "100コンボごとの演出");
        fireworkItem.setItemMeta(fireworkMeta);
        comboShop.setItem(12, fireworkItem);

        // slot13: リセットまでの時間
        ItemStack resetTimeItem = new ItemStack(Material.CLOCK);
        ItemMeta resetTimeMeta = resetTimeItem.getItemMeta();
        resetTimeMeta.setDisplayName(ChatColor.GOLD + "リセットまでの時間");
        resetTimeItem.setItemMeta(resetTimeMeta);
        comboShop.setItem(13, resetTimeItem);

        // slot18: デフォルト
        ItemStack defaultItem1 = new ItemStack(Material.STONE);
        ItemMeta defaultMeta1 = defaultItem1.getItemMeta();
        defaultMeta1.setDisplayName(ChatColor.GOLD + "デフォルト");
        defaultMeta1.setLore(Collections.singletonList(ChatColor.GRAY + "コンボの見た目設定"));
        defaultItem1.setItemMeta(defaultMeta1);
        comboShop.setItem(18, defaultItem1);

        // slot19: コンボ音設定
        ItemStack comboSoundItem = comboSound.getComboSoundItem(player);
        comboShop.setItem(19, comboSoundItem);

        // slot20: コンボパーティクル設定
        ItemStack comboParticleItem = comboParticle.getComboParticleItem(player);
        comboShop.setItem(20, comboParticleItem);

        // slot21: コンボ演出設定
        ItemStack periodEffectItem = new ItemStack(Material.BARRIER);
        ItemMeta periodEffectMeta = periodEffectItem.getItemMeta();
        periodEffectMeta.setDisplayName(ChatColor.GOLD + "なし");
        periodEffectMeta.setLore(Collections.singletonList(ChatColor.GRAY + "コンボ演出設定"));
        periodEffectItem.setItemMeta(periodEffectMeta);
        comboShop.setItem(21, periodEffectItem);

        // slot22: 1分
        ItemStack oneMinuteItem = new ItemStack(Material.PAPER);
        ItemMeta oneMinuteMeta = oneMinuteItem.getItemMeta();
        oneMinuteMeta.setDisplayName(ChatColor.GOLD + "1分");
        oneMinuteMeta.setLore(Collections.singletonList(ChatColor.GRAY + "コンボリセット設定"));
        oneMinuteItem.setItemMeta(oneMinuteMeta);
        comboShop.setItem(22, oneMinuteItem);

        // slot27: 戻るの矢
        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setDisplayName(ChatColor.RED + "戻る");
        backArrow.setItemMeta(backArrowMeta);
        comboShop.setItem(27, backArrow);

        // slot35: コンボポイント
        int comboPoints = playerComboPoints.getOrDefault(player.getUniqueId(), 0);
        int totalComboPoints = playerTotalComboPoints.getOrDefault(player.getUniqueId(), 0);
        ItemStack comboPointItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta comboPointMeta = comboPointItem.getItemMeta();
        comboPointMeta.setDisplayName(ChatColor.GOLD + "コンボポイント: " + comboPoints);
        comboPointMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "100コンボ毎に手に入ります",
                ChatColor.GRAY + "ブロックから低確率で入手できます",
                ChatColor.GRAY + "総獲得ポイント: " + totalComboPoints
        ));
        comboPointItem.setItemMeta(comboPointMeta);
        comboShop.setItem(35, comboPointItem);

        player.openInventory(comboShop);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("コンボショップ") && !event.getView().getTitle().equals("コンボ音設定") && !event.getView().getTitle().equals("コンボパーティクル設定") && !event.getView().getTitle().equals("コンボの見た目設定") && !event.getView().getTitle().equals("コンボ演出設定") && !event.getView().getTitle().equals("リセット時間設定") && !event.getView().getTitle().equals("累計ポイントボーナス")) {
            return;
        }

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("コンボショップ")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                new Menu(plugin).openMenu(player);
            } else if (clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().contains(ChatColor.GRAY + "コンボの見た目設定")) {
                comboDisplay.openComboDisplaySettingInventory(player);
            } else if (clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().contains(ChatColor.GRAY + "コンボ音設定")) {
                comboSound.openComboSoundSettingInventory(player);
            } else if (clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().contains(ChatColor.GRAY + "コンボパーティクル設定")) {
                comboParticle.openComboParticleSettingInventory(player);
            } else if (clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().contains(ChatColor.GRAY + "コンボ演出設定")) {
                comboPeriodEffect.openComboPeriodEffectSettingInventory(player);
            } else if (clickedItem.getItemMeta().getLore() != null && clickedItem.getItemMeta().getLore().contains(ChatColor.GRAY + "コンボリセット設定")) {
                resetTime.openResetTimeSettingInventory(player);
            }
        } else if (event.getView().getTitle().equals("累計ポイントボーナス")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openComboShop(player);
            }
        } else if (event.getView().getTitle().equals("コンボ音設定")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openComboShop(player);
            } else {
                Sound sound = comboSound.ComboSoundIcon(clickedItem.getType());
                if (sound != null) {
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                }
                comboSound.saveComboSoundSetting(player, sound);
            }
        } else if (event.getView().getTitle().equals("コンボパーティクル設定")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openComboShop(player);
            } else {
                Particle particle = comboParticle.ComboParticleIcon(clickedItem.getType());
                comboParticle.saveComboParticleSetting(player, particle);
            }
        } else if (event.getView().getTitle().equals("コンボの見た目設定")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openComboShop(player);
            }
        } else if (event.getView().getTitle().equals("コンボ演出設定")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openComboShop(player);
            }
        } else if (event.getView().getTitle().equals("リセット時間設定")) {
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "戻る")) {
                openComboShop(player);
            }
        }
    }

    private String generateProgressBar(int progress) {
        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < progress; i++) {
            progressBar.append("|");
        }
        for (int i = progress; i < 10; i++) {
            progressBar.append(ChatColor.DARK_GRAY).append("|");
        }
        return progressBar.toString();
    }
}