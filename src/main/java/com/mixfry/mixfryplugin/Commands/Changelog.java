package com.mixfry.mixfryplugin.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Changelog implements CommandExecutor {

    private final List<String> changelog = Arrays.asList(
        ChatColor.GOLD + "v1.1.2 - コンボ機能 UPDATE" + ChatColor.GRAY + "- 2024/11/17",
        ChatColor.GOLD + "・コンボ機能 " + ChatColor.WHITE + "アップデート",
        ChatColor.WHITE + "・防具立ての出る頻度を" + ChatColor.YELLOW + "1" + ChatColor.WHITE + "→" + ChatColor.GOLD + "5" + ChatColor.WHITE + "コンボごとに変更しました",
        ChatColor.WHITE + "・コンボショップを追加しました",
        ChatColor.WHITE + "・コンボエフェクトを追加しました",
        ChatColor.WHITE + "・コンボレベルシステムを追加しました",
        ChatColor.GOLD + "クッキークリッカー" + ChatColor.WHITE + "アップデート" + ChatColor.GREEN + " ver 1.1.2",
        ChatColor.WHITE + "・処理が軽量化しました",
        ChatColor.WHITE + "・数値がオーバーフローする不具合を修正しました"
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.AQUA + "=== Mixfry's プラグインの変更点 ===");
            for (String change : changelog) {
                player.sendMessage(change);
            }
            player.sendMessage(ChatColor.AQUA + "============================");
            return true;
        }
        return false;
    }
}