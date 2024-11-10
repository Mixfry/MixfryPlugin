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
        ChatColor.GOLD + "v1.1.1 - 設定・コンボ機能追加" + ChatColor.GRAY + "- 2024/11/10",
        ChatColor.GOLD + "・設定機能 " + ChatColor.WHITE + "追加",
        ChatColor.WHITE + "・耐久値の警告機能、レアドロップ通知機能のON/OFFが可能になりました",
        ChatColor.WHITE + "・/configで設定画面を開くことができます",
        ChatColor.GOLD + "・コンボ機能 " + ChatColor.WHITE + "追加",
        ChatColor.WHITE + "・ブロックを連続で壊すとコンボ数が増加します (100コンボから)",
        ChatColor.GOLD + "スコアボード " + ChatColor.WHITE + "修正",
        ChatColor.WHITE + "・他のプレイヤーのスコアボードが表示される不具合を修正しました",
        ChatColor.GOLD + "クッキークリッカー アップデート" + ChatColor.GREEN + " ver 1.1.1",
        ChatColor.WHITE + "・放置アップグレードのコストの計算方式を変更しました",
        ChatColor.WHITE + "・放置報酬が総クッキー数に加算されるようになりました。",
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