package com.mixfry.mixfryplugin;

import com.mixfry.mixfryplugin.Commands.Changelog;
import com.mixfry.mixfryplugin.Commands.CookieClicker;
import com.mixfry.mixfryplugin.Commands.Cords;
import com.mixfry.mixfryplugin.Commands.Setting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class MixfryPlugin extends JavaPlugin {

    private static MixfryPlugin instance;
    private CookieClicker cookieClicker;

    @Override
    public void onEnable() {
        instance = this;
        new DeathPoint();
        new Cords();
        ToolExtention.getInstance();
        RareDropAlert.getInstance();
        new ScoreBoard(this);
        new CookieClicker();
        new Changelog();
        cookieClicker = new CookieClicker();
        Setting setting = new Setting(this);
        getCommand("setting").setExecutor(setting);
        getServer().getPluginManager().registerEvents(setting, this);
        for (Player player : getServer().getOnlinePlayers()) {
            new PlayerData(player);
        }
    }

    public static MixfryPlugin getInstance() {
        return instance;
    }

    public FileConfiguration getPlayerConfig(Player player) {
        File configFile = new File(getDataFolder(), player.getName() + "_config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("設定ファイルの作成中にエラーが発生しました: " + e.getMessage());
            }
        }
        return YamlConfiguration.loadConfiguration(configFile);
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
