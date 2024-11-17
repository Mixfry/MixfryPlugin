//主に補填用に使う

package com.mixfry.mixfryplugin.Commands;

import com.mixfry.mixfryplugin.Minecombo.MineCombo;
import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MineComboDebug implements CommandExecutor, TabCompleter {

    private final MixfryPlugin plugin;
    private final Map<UUID, Integer> comboPointsCache = new HashMap<>();
    private final Map<UUID, Integer> totalComboPointsCache = new HashMap<>();

    public MineComboDebug(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (!player.getName().equals("Mixfry")) {
            return true;
        }

        if (args.length != 3 || (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add"))) {
            sender.sendMessage(ChatColor.RED + "使用方法: /minecombo [対象セレクタ] [set|add] [数]");
            return true;
        }

        Collection<Player> players = Bukkit.selectEntities(sender, args[0]).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());

        if (players.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "指定された対象が見つかりません。");
            return true;
        }

        int points;
        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "数値を入力してください。");
            return true;
        }

        for (Player targetPlayer : players) {
            UUID playerId = targetPlayer.getUniqueId();
            int currentPoints = comboPointsCache.getOrDefault(playerId, plugin.getPlayerComboPoints().getOrDefault(playerId, 0));
            int newPoints = 0;
            int totalPoints = totalComboPointsCache.getOrDefault(playerId, plugin.getPlayerTotalComboPoints().getOrDefault(playerId, 0));
            String message = "";

            if (args[1].equalsIgnoreCase("set")) {
                newPoints = points;
                totalPoints = points;
                message = ChatColor.GREEN + String.valueOf(newPoints) + ChatColor.WHITE + "ポイントに設定されました。";
            } else if (args[1].equalsIgnoreCase("add")) {
                newPoints = currentPoints + points;
                if (newPoints < 0) {
                    newPoints = 0;
                }
                totalPoints += points;
                if (totalPoints < 0) {
                    totalPoints = 0;
                }
                message = ChatColor.GREEN + String.valueOf(points) + ChatColor.WHITE + " ポイント追加されました。";
            }

            comboPointsCache.put(playerId, newPoints);
            totalComboPointsCache.put(playerId, totalPoints);

            savePlayerComboDataAsync(targetPlayer, newPoints, totalPoints);

            MineCombo mineCombo = MineCombo.getInstance();
            mineCombo.loadPlayerComboData(targetPlayer);
            mineCombo.updatePlayerLevelDisplay(targetPlayer);
            mineCombo.updateBossBar(targetPlayer);

            targetPlayer.sendMessage(message);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> selectors = new ArrayList<>();
            selectors.add("@a");
            selectors.add("@p");
            selectors.add("@r");
            selectors.add("@e");
            selectors.add("@s");
            for (Player player : Bukkit.getOnlinePlayers()) {
                selectors.add(player.getName());
            }
            return selectors;
        } else if (args.length == 2) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("set");
            subCommands.add("add");
            return subCommands;
        }
        return null;
    }

    private void savePlayerComboDataAsync(Player player, int comboPoints, int totalComboPoints) {
        CompletableFuture.runAsync(() -> {
            File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("comboPoints", comboPoints);
            config.set("totalComboPoints", totalComboPoints);
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().severe("コンボポイントの保存中にエラーが発生しました。");
                e.printStackTrace();
            }
        });
    }
}