package com.mixfry.mixfryplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Changelog implements CommandExecutor {

    private final List<String> changelog = Arrays.asList(
        ChatColor.GOLD + "v1.1.0 - マルチアップデート" + ChatColor.GRAY + "- 2024/10/31",
        ChatColor.GOLD + "・金床装飾機能 追加",
        ChatColor.WHITE + "・名前の前に&aや&bなど、&をつけることで文字装飾が行えるようになりました",
        ChatColor.GOLD + "スコアボード機能 追加",
        ChatColor.WHITE + "・座標にバイオーム、時刻を表示するスコアボードを追加しました",
        ChatColor.GOLD + "レアドロップ機能 追加",
        ChatColor.WHITE + "・ウィザースケルトンの頭など、レアドロップが出た際に通知を行う機能を追加しました",
        ChatColor.GOLD + "耐久値の警告機能 追加",
        ChatColor.WHITE + "・耐久値が10%を切った際に警告を行う機能を追加しました",
        ChatColor.GOLD + "クッキークリッカー アップデート" + ChatColor.GREEN + " ver 1.1",
        ChatColor.WHITE + "・オフライン中のクッキーを回収できるようになりました、これは1%の確率で10倍受け取ることができます",
        ChatColor.WHITE + "・放置しながらでもクッキーを生産できるようになりました",
        ChatColor.WHITE + "・ランキング機能を修正しました",
        ChatColor.WHITE + "・クッキーの生産量を調整しました"
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