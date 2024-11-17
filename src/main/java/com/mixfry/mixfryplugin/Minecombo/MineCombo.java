package com.mixfry.mixfryplugin.Minecombo;

import com.mixfry.mixfryplugin.Minecombo.Cosmetics.TotalPointBonus;
import com.mixfry.mixfryplugin.MixfryPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class MineCombo implements Listener {

    private final JavaPlugin plugin;
    private final TotalPointBonus totalPointBonus;
    private final Map<UUID, Integer> playerCombos = new HashMap<>();
    private final Map<UUID, BukkitRunnable> comboResetTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> warning30SecTasks = new HashMap<>();
    private final Map<UUID, BukkitRunnable> warning10SecTasks = new HashMap<>();
    private final Map<UUID, Integer> playerNotePitches = new HashMap<>();
    private final Map<UUID, Integer> playerComboPoints = new HashMap<>();
    private final Map<UUID, Integer> playerTotalComboPoints = new HashMap<>();
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    private static MineCombo instance;
    private boolean mineComboEnabled = false;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
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
        updatePlayerLevelDisplay(player);
        updateBossBar(player);
    }

    public MineCombo(JavaPlugin plugin) {
        this.plugin = plugin;
        this.totalPointBonus = new TotalPointBonus((MixfryPlugin) plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static MineCombo getInstance() {
        if (instance == null) {
            instance = new MineCombo(MixfryPlugin.getInstance());
        }
        return instance;
    }

    public void updatePlayerLevelDisplay(Player player) {
        int level = totalPointBonus.getPlayerLevel(player);
        ChatColor levelColor = ComboColor.getLevelColor(level);
        ChatColor borderColor = ComboColor.getBorderColor(level);
        String displayName;
        if (level >= 2560) {
            displayName = ComboColor.getRainbowText("[" + level + "] " + player.getName());
        } else {
            displayName = borderColor + "[" + levelColor + level + borderColor + "] " + ChatColor.RESET + player.getName();
        }
        player.setPlayerListName(displayName);
        player.setDisplayName(displayName);
        player.setCustomName(displayName);
        player.setCustomNameVisible(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        int level = totalPointBonus.getPlayerLevel(player);

        if (!mineComboEnabled) {
            return;
        }

        Location blockLocation = event.getBlock().getLocation();
        int combo = playerCombos.getOrDefault(playerId, 0) + 1;
        playerCombos.put(playerId, combo);

        if (combo > 100) {
            Particle comboParticle = getComboParticle(player);
            if (comboParticle != null) {
                player.getWorld().spawnParticle(comboParticle, blockLocation.add(0, 0.5, 0), 3, 0.25, 0.25, 0.25, 0.1);
            }

            if (combo % 5 == 0){
                ArmorStand armorStand = player.getWorld().spawn(blockLocation.add(0.5, 0, 0.5), ArmorStand.class);
                armorStand.setVisible(false);
                armorStand.setCustomName(ComboColor.getComboName(combo));
                armorStand.setCustomNameVisible(true);
                armorStand.setGravity(false);
                armorStand.setMarker(true);
                armorStand.setInvisible(true);
                armorStand.hasMetadata("mineCombo");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        armorStand.remove();
                    }
                }.runTaskLater(plugin, 20);
            }

            int notePitch = playerNotePitches.getOrDefault(playerId, 0);
            Sound comboSound = getComboSound(player);
            if (comboSound != null) {
                player.playSound(player.getLocation(), comboSound, 1.0f, (float) Math.pow(2.0, (notePitch - 12) / 12.0));
            }
            notePitch = (notePitch + 1) % 25;
            playerNotePitches.put(playerId, notePitch);
        }

        // 500コンボごとに花火を打ち上げる
        if (combo % 500 == 0) {
            Firework firework = player.getWorld().spawn(blockLocation, Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .withColor(Color.RED)
                    .withColor(Color.YELLOW)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withFlicker()
                    .build());
            fireworkMeta.setPower(1);
            firework.setFireworkMeta(fireworkMeta);
            firework.setInvulnerable(true);
        }

        // 100コンボ毎にコンボポイント追加処理
        if (combo % 100 == 0) {
            int comboPoints = playerComboPoints.getOrDefault(playerId, 0) + 1;
            playerComboPoints.put(playerId, comboPoints);

            int totalComboPoints = playerTotalComboPoints.getOrDefault(playerId, 0) + 1;
            playerTotalComboPoints.put(playerId, totalComboPoints);

            File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("comboPoints", comboPoints);
            config.set("totalComboPoints", totalComboPoints);
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "コンボポイントの保存中にエラーが発生しました。", e);
            }
            updatePlayerLevelDisplay(player);
            updateBossBar(player);
        }

        // 1000コンボごとにボーナスポイント追加処理
        if (combo % 1000 == 0) {
            int bonusPoints = 4 + (combo / 1000);
            int comboPoints = playerComboPoints.getOrDefault(playerId, 0) + bonusPoints;
            playerComboPoints.put(playerId, comboPoints);

            int totalComboPoints = playerTotalComboPoints.getOrDefault(playerId, 0) + bonusPoints;
            playerTotalComboPoints.put(playerId, totalComboPoints);

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "コンボボーナス！ " + ChatColor.GOLD + "+" + bonusPoints + "ポイント"));

            File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("comboPoints", comboPoints);
            config.set("totalComboPoints", totalComboPoints);
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "コンボポイントの保存中にエラーが発生しました。", e);
            }
            updatePlayerLevelDisplay(player);
            updateBossBar(player);
        }

        // 1000分の1の確率で1~10ポイントをランダムに追加
        int chance = Math.max(1000 - (level / 10) * 5, 500); // 10レベルごとに0.5%up、最大50%

        Random random = new Random();
        if (random.nextInt(chance) == 0) {
            int randomPoints = random.nextInt(10) + 1;
            int comboPoints = playerComboPoints.getOrDefault(playerId, 0) + randomPoints;
            playerComboPoints.put(playerId, comboPoints);

            int totalComboPoints = playerTotalComboPoints.getOrDefault(playerId, 0) + randomPoints;
            playerTotalComboPoints.put(playerId, totalComboPoints);

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Lucky!! " + ChatColor.GOLD + "+" + randomPoints + ChatColor.GREEN + " Points!"));

            File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("comboPoints", comboPoints);
            config.set("totalComboPoints", totalComboPoints);
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "コンボポイントの保存中にエラーが発生しました。", e);
            }
            updatePlayerLevelDisplay(player);
            updateBossBar(player);
        }

        if (comboResetTasks.containsKey(playerId)) {
            comboResetTasks.get(playerId).cancel();
        }

        if (comboResetTasks.containsKey(playerId)) {
            comboResetTasks.get(playerId).cancel();
        }
        if (warning30SecTasks.containsKey(playerId)) {
            warning30SecTasks.get(playerId).cancel();
        }
        if (warning10SecTasks.containsKey(playerId)) {
            warning10SecTasks.get(playerId).cancel();
        }

        BukkitRunnable resetTask = new BukkitRunnable() {
            @Override
            public void run() {
                int combo = playerCombos.get(playerId);
                playerCombos.remove(playerId);
                playerNotePitches.remove(playerId);
                if (combo > 100) {
                    player.sendMessage(ChatColor.RED + "Combo reset! " + ChatColor.GRAY + "[" + ChatColor.GOLD + combo + ChatColor.GRAY + "]");
                }
                removeBossBar(player);
            }
        };

        int resetTime = 3 * 1200 + (level / 10) * 3 * 1200; // 基本3分 + レベル10ごとに3分追加

        // 30秒前の警告
        BukkitRunnable warning30SecTask = new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.YELLOW + "あと30秒でコンボがリセットされます");
            }
        };
        warning30SecTask.runTaskLater(plugin, resetTime - 600); // 30秒前に実行

        // 10秒前の警告
        BukkitRunnable warning10SecTask = new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.RED + "あと10秒でコンボがリセットされます");
            }
        };
        warning10SecTask.runTaskLater(plugin, resetTime - 200);

        resetTask.runTaskLater(plugin, resetTime);
        comboResetTasks.put(playerId, resetTask);
        warning30SecTasks.put(playerId, warning30SecTask);
        warning10SecTasks.put(playerId, warning10SecTask);
    }

    public void setMineComboEnabled(boolean enabled) {
        this.mineComboEnabled = enabled;
    }

    private Sound getComboSound(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String soundName = config.getString("comboSound", "null");
        if (soundName.equals("null")) {
            return null;
        }
        return Sound.valueOf(soundName);
    }

    private Particle getComboParticle(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String particleName = config.getString("comboParticle", "null");
        if (particleName.equals("null")) {
            return null;
        }
        return Particle.valueOf(particleName);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true);
        }
    }

    public void updateBossBar(Player player) {
        UUID playerId = player.getUniqueId();
        int combo = playerCombos.getOrDefault(playerId, 0);
        int totalComboPoints = playerTotalComboPoints.getOrDefault(playerId, 0);
        int level = totalComboPoints / 10;
        int progress = totalComboPoints % 10;

        if (combo >= 100) {
            BossBar bossBar = playerBossBars.get(playerId);
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_10);
                bossBar.addPlayer(player);
                playerBossBars.put(playerId, bossBar);
            }

            bossBar.setTitle(ChatColor.GOLD + "[" + level + "レベル] " + progress + "/10");
            bossBar.setProgress(progress / 10.0);
        }
    }

    public void removeBossBar(Player player) {
        UUID playerId = player.getUniqueId();
        BossBar bossBar = playerBossBars.remove(playerId);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void loadSettings() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
            if (configFile.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                boolean mineComboEnabled = config.getBoolean("mineCombo", true);
                setMineComboEnabled(mineComboEnabled);
            }
        }
    }

    public void savePlayerComboData(Player player) {
        UUID playerId = player.getUniqueId();
        int comboPoints = playerComboPoints.getOrDefault(playerId, 0);
        int totalComboPoints = playerTotalComboPoints.getOrDefault(playerId, 0);

        File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("comboPoints", comboPoints);
        config.set("totalComboPoints", totalComboPoints);
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "コンボデータの保存中にエラーが発生しました。", e);
        }
    }

    public void loadPlayerComboData(Player player) {
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
    }
}