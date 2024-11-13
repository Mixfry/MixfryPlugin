package com.mixfry.mixfryplugin;

import com.mixfry.mixfryplugin.ComboCosmetics.TotalPointBonus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
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
    private final Map<UUID, Integer> playerNotePitches = new HashMap<>();
    private final Map<UUID, Integer> playerComboPoints = new HashMap<>();
    private final Map<UUID, Integer> playerTotalComboPoints = new HashMap<>();

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

    private void updatePlayerLevelDisplay(Player player) {
        int level = totalPointBonus.getPlayerLevel(player);
        String displayName = ChatColor.GOLD + "[" + level + "] " + ChatColor.RESET + player.getName();
        player.setPlayerListName(displayName);
        player.setDisplayName(displayName);
        player.setCustomName(displayName);
        player.setCustomNameVisible(true);
    }

    public void setMineComboEnabled(boolean enabled) {
        this.mineComboEnabled = enabled;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

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
                armorStand.setCustomName(getComboName(combo));
                armorStand.setCustomNameVisible(true);
                armorStand.setGravity(false);
                armorStand.setMarker(true);
                armorStand.setInvisible(true);

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
        }

        // 1000分の1の確率で1~10ポイントをランダムに追加
        Random random = new Random();
        if (random.nextInt(1000) == 0) {
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
        }

        if (comboResetTasks.containsKey(playerId)) {
            comboResetTasks.get(playerId).cancel();
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
            }
        };
        resetTask.runTaskLater(plugin, 1200); //1分でリセットするやつ
        comboResetTasks.put(playerId, resetTask);
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

    private String getGradientText(String text, int combo) {
        ChatColor[] colors;
        if (combo >= 15000) {
            colors = new ChatColor[]{
                    ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
                    ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_PURPLE
            };
        } else if (combo >= 10000) {
            colors = new ChatColor[]{
                    ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.GREEN,
                    ChatColor.AQUA, ChatColor.BLUE, ChatColor.WHITE
            };
        } else if (combo < 2000) {
            colors = new ChatColor[]{ChatColor.WHITE, ChatColor.GRAY, ChatColor.DARK_GRAY};
        } else if (combo < 2500) {
            colors = new ChatColor[]{ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE};
        } else if (combo < 3000) {
            colors = new ChatColor[]{ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW};
        } else if (combo < 3500) {
            colors = new ChatColor[]{ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD};
        } else if (combo < 4000) {
            colors = new ChatColor[]{ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.BLUE};
        } else if (combo < 4500) {
            colors = new ChatColor[]{ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};
        } else if (combo < 5000) {
            colors = new ChatColor[]{ChatColor.DARK_BLUE, ChatColor.BLUE, ChatColor.AQUA};
        } else if (combo < 5500) {
            colors = new ChatColor[]{ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.YELLOW};
        } else if (combo < 6000) {
            colors = new ChatColor[]{ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD};
        } else if (combo < 6500) {
            colors = new ChatColor[]{ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.BLUE};
        } else if (combo < 7000) {
            colors = new ChatColor[]{ChatColor.GOLD, ChatColor.YELLOW, ChatColor.RED};
        } else if (combo < 7500) {
            colors = new ChatColor[]{ChatColor.BLUE, ChatColor.AQUA, ChatColor.DARK_BLUE};
        } else if (combo < 8000) {
            colors = new ChatColor[]{ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.YELLOW};
        } else if (combo < 8500) {
            colors = new ChatColor[]{ChatColor.RED, ChatColor.DARK_RED, ChatColor.GOLD};
        } else if (combo < 9000) {
            colors = new ChatColor[]{ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.BLUE};
        } else {
            colors = new ChatColor[]{ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED};
        }
        StringBuilder gradientText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            gradientText.append(colors[i % colors.length]).append(text.charAt(i));
        }
        return gradientText.toString();
    }

    private String getComboName(int combo) {
        if (combo >= 1500) {
            return getGradientText(combo + " Combo!", combo);
        } else {
            ChatColor color;
            switch ((combo / 100) % 15) {
                case 1: color = ChatColor.DARK_GRAY; break;
                case 2: color = ChatColor.GRAY; break;
                case 3: color = ChatColor.WHITE; break;
                case 4: color = ChatColor.AQUA; break;
                case 5: color = ChatColor.DARK_AQUA; break;
                case 6: color = ChatColor.BLUE; break;
                case 7: color = ChatColor.DARK_BLUE; break;
                case 8: color = ChatColor.DARK_GREEN; break;
                case 9: color = ChatColor.GREEN; break;
                case 10: color = ChatColor.YELLOW; break;
                case 11: color = ChatColor.GOLD; break;
                case 12: color = ChatColor.RED; break;
                case 13: color = ChatColor.DARK_RED; break;
                case 14: color = ChatColor.LIGHT_PURPLE; break;
                case 15: color = ChatColor.DARK_PURPLE; break;
                default: color = ChatColor.WHITE; break;
            }
            return color.toString() + combo + " Combo!";
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
}