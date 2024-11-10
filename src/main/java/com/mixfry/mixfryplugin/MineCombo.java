package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineCombo implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> playerCombos = new HashMap<>();
    private final Map<UUID, BukkitRunnable> comboResetTasks = new HashMap<>();
    private final Map<UUID, Integer> playerNotePitches = new HashMap<>();

    private static MineCombo instance;
    private boolean mineComboEnabled = false;

    public MineCombo(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static MineCombo getInstance() {
        if (instance == null) {
            instance = new MineCombo(MixfryPlugin.getInstance());
        }
        return instance;
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
        ArmorStand armorStand = player.getWorld().spawn(blockLocation.add(0.5, 0, 0.5), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setCustomName(getComboName(combo));
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                armorStand.remove();
            }
        }.runTaskLater(plugin, 20);

        int notePitch = playerNotePitches.getOrDefault(playerId, 0);
        Sound comboSound = getComboSound(player);
        if (comboSound != null) {
            player.playSound(player.getLocation(), comboSound, 1.0f, (float) Math.pow(2.0, (notePitch - 12) / 12.0));
        }
        notePitch = (notePitch + 1) % 25;
        playerNotePitches.put(playerId, notePitch);
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
            player.sendMessage(ChatColor.RED + "Combo reset! " + ChatColor.GRAY + "[" + ChatColor.GOLD + combo + ChatColor.GRAY + "]");
        }
    };
    resetTask.runTaskLater(plugin, 1200);
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

    private String getComboName(int combo) {
        if (combo >= 10000) {
            return getRainbowText(combo + " Combo!", false);
        } else if (combo >= 5000) {
            return getRainbowText(combo + " Combo!", true);
        } else if (combo >= 1500) {
            return ChatColor.WHITE.toString() + combo + " Combo!";
        } else {
            ChatColor color;
            switch ((combo / 100) % 15) {
                case 1: color = ChatColor.RED; break;
                case 2: color = ChatColor.GOLD; break;
                case 3: color = ChatColor.YELLOW; break;
                case 4: color = ChatColor.GREEN; break;
                case 5: color = ChatColor.AQUA; break;
                case 6: color = ChatColor.BLUE; break;
                case 7: color = ChatColor.DARK_PURPLE; break;
                case 8: color = ChatColor.LIGHT_PURPLE; break;
                case 9: color = ChatColor.DARK_RED; break;
                case 10: color = ChatColor.DARK_GREEN; break;
                case 11: color = ChatColor.DARK_AQUA; break;
                case 12: color = ChatColor.DARK_BLUE; break;
                case 13: color = ChatColor.DARK_GRAY; break;
                case 14: color = ChatColor.GRAY; break;
                default: color = ChatColor.WHITE; break;
            }
            return color.toString() + combo + " Combo!";
        }
    }

    private String getRainbowText(String text, boolean light) {
        ChatColor[] colors;
        if (light) {
            colors = new ChatColor[]{
                    ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.GREEN,
                    ChatColor.AQUA, ChatColor.BLUE, ChatColor.WHITE
            };
        } else {
            colors = new ChatColor[]{
                    ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
                    ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_PURPLE
            };
        }
        StringBuilder rainbowText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            rainbowText.append(colors[i % colors.length]).append(text.charAt(i));
        }
        return rainbowText.toString();
    }
}