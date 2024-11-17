package com.mixfry.mixfryplugin.Function;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreBoard {

    private final JavaPlugin plugin;

    public ScoreBoard(JavaPlugin plugin) {
        this.plugin = plugin;
        startUpdatingScoreboards();
    }

    private void startUpdatingScoreboards() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("MixfryBoard", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "受逃会");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score emptyLine1 = objective.getScore(" ");
        emptyLine1.setScore(5);

        String biome = player.getLocation().getBlock().getBiome().name();
        Score biomeLine = objective.getScore(ChatColor.WHITE + "バイオーム: " + biome);
        biomeLine.setScore(4);

        Location location = player.getLocation();
        String coords = ChatColor.GRAY + "[" +
                ChatColor.WHITE + location.getBlockX() + ChatColor.GRAY + ", " +
                ChatColor.WHITE + location.getBlockY() + ChatColor.GRAY + ", " +
                ChatColor.WHITE + location.getBlockZ() + ChatColor.GRAY +
                "]";
        Score coordsLine = objective.getScore(coords);
        coordsLine.setScore(3);

        Score emptyLine2 = objective.getScore("  ");
        emptyLine2.setScore(2);

        SimpleDateFormat sdf = new SimpleDateFormat("M/d HH:mm");
        String dateTime = sdf.format(new Date());
        Score dateTimeLine = objective.getScore(ChatColor.YELLOW + dateTime);
        dateTimeLine.setScore(1);

        player.setScoreboard(board);
    }
}