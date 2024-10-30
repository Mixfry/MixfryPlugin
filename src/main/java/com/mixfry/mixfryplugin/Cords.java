package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Cords implements Listener {
    public Cords() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
    }

    public void ShareCords(Player player) {
        Location location = player.getLocation();
        String playerName = player.getName();
        String message = ChatColor.GRAY + playerName + "の現在地: " +
        ChatColor.GRAY + "[" +
        ChatColor.AQUA + location.getBlockX() + ChatColor.GRAY + ", " +
        ChatColor.RED + location.getBlockY() + ChatColor.GRAY + ", " +
        ChatColor.GREEN + location.getBlockZ() + ChatColor.GRAY + "]";
        Bukkit.broadcastMessage(message);
    }
}