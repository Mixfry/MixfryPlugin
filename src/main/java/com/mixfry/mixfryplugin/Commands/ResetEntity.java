package com.mixfry.mixfryplugin.Commands;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.boss.BossBar;

import java.util.Iterator;

public class ResetEntity implements CommandExecutor {

    private final MixfryPlugin plugin;

    public ResetEntity(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーからのみ実行可能です。");
            return true;
        }

        Player player = (Player) sender;
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                for (MetadataValue meta : entity.getMetadata("mineCombo")) {
                    if (meta.asBoolean()) {
                        entity.remove();
                        break;
                    }
                }
            }
        }

        Iterator<KeyedBossBar> bossBarIterator = Bukkit.getBossBars();
        while (bossBarIterator.hasNext()) {
            BossBar bossBar = bossBarIterator.next();
            bossBar.removeAll();
        }

        player.sendMessage(ChatColor.GREEN + "コンボの防具立てとバグったボスバーを消去しました。");
        return true;
    }
}