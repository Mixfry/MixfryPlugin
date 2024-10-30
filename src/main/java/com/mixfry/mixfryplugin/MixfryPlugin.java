package com.mixfry.mixfryplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MixfryPlugin extends JavaPlugin {

    private static MixfryPlugin instance;
    private CookieClicker cookieClicker;

    @Override
    public void onEnable() {
        instance = this;
        new DeathPoint();
        new Cords();
        new ToolExtention();
        new RareDropAlert();
        new ScoreBoard(this);
        cookieClicker = new CookieClicker();
        for (Player player : getServer().getOnlinePlayers()) {
            new PlayerData(player);
        }
    }

    public static MixfryPlugin getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーからのみ実行可能です。");
            return true;
        }

        Player player = (Player) sender;
        if (label.equalsIgnoreCase("cookie_clicker") || label.equalsIgnoreCase("cc")) {
            cookieClicker.openCookieInventory(player);
            return true;
        }

        if (label.equalsIgnoreCase("cords")) {
            new Cords().ShareCords(player);
            return true;
        }
        return false;
    }

    public void updatePlayerCookies(Player player, int cookies) {
        PlayerData playerData = new PlayerData(player);
        playerData.setCookieCount(cookies);
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            PlayerData playerData = new PlayerData(player);
            playerData.saveData();
        }
    }
}
