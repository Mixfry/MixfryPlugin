package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathPoint implements Listener {

    public DeathPoint() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getName();
        Vector location = event.getEntity().getLocation().toVector();
        String worldName = event.getEntity().getWorld().getName();
        String worldType = "";

        switch (worldName) {
            case "world":
                worldType = "オーバーワールド";
                break;
            case "world_nether":
                worldType = "ネザー";
                break;
            case "world_the_end":
                worldType = "エンド";
                break;
            default:
                worldType = "不明なディメンション";
                break;
        }

        String message =
                ChatColor.RED + playerName + "の死亡地点: " + worldType + "の" +
                        ChatColor.GRAY + "[" +
                        ChatColor.AQUA + location.getBlockX() + ChatColor.GRAY + ", " +
                        ChatColor.RED + location.getBlockY() + ChatColor.GRAY + ", " +
                        ChatColor.GREEN + location.getBlockZ() + ChatColor.GRAY + "]";

        Bukkit.broadcastMessage(message);

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getEntity().sendMessage(ChatColor.YELLOW + "死亡から5分経過しました。");
            }
        }.runTaskLater(MixfryPlugin.getInstance(), 5 * 60 * 20);
    }
}
