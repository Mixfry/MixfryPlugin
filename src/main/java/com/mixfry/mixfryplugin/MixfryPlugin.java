package com.mixfry.mixfryplugin;

import com.mixfry.mixfryplugin.Commands.MineComboDebug;
import com.mixfry.mixfryplugin.Commands.ResetEntity;
import com.mixfry.mixfryplugin.CookieClicker.PlayerData;
import com.mixfry.mixfryplugin.Function.DeathPoint;
import com.mixfry.mixfryplugin.Function.RareDropAlert;
import com.mixfry.mixfryplugin.Function.ScoreBoard;
import com.mixfry.mixfryplugin.Function.ToolExtention;
import com.mixfry.mixfryplugin.Menu.Menu;
import com.mixfry.mixfryplugin.Menu.Setting;
import com.mixfry.mixfryplugin.Commands.Changelog;
import com.mixfry.mixfryplugin.CookieClicker.CookieClicker;
import com.mixfry.mixfryplugin.Commands.Cords;
import com.mixfry.mixfryplugin.Minecombo.MineCombo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MixfryPlugin extends JavaPlugin {

    private static MixfryPlugin instance;
    private CookieClicker cookieClicker;
    private final Map<UUID, Integer> playerComboPoints = new HashMap<>();
    private final Map<UUID, Integer> playerTotalComboPoints = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        DeathPoint.getInstance();
        new Menu(this);
        new Cords();
        new ScoreBoard(this);
        new Changelog();
        new MineCombo(this);
        ToolExtention.getInstance();
        RareDropAlert.getInstance();
        cookieClicker = new CookieClicker();
        MineCombo.getInstance().loadSettings();
        Setting setting = new Setting(this);
        getCommand("setting").setExecutor(setting);
        getCommand("reset_entity").setExecutor(new ResetEntity(this));
        this.getCommand("MineCombo").setExecutor(new MineComboDebug(this));
        this.getCommand("changelog").setExecutor(new Changelog());

        getServer().getPluginManager().registerEvents(setting, this);
        for (Player player : getServer().getOnlinePlayers()) {
            new PlayerData(player);
        }
        for (Player player : getServer().getOnlinePlayers()) {
            MineCombo.getInstance().loadPlayerComboData(player);
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

    public CookieClicker getCookieClicker() {
        return cookieClicker;
    }

    public Map<UUID, Integer> getPlayerComboPoints() {
        return playerComboPoints;
    }

    public Map<UUID, Integer> getPlayerTotalComboPoints() {
        return playerTotalComboPoints;
    }

    @Override
    public void onDisable() {
        MineCombo mineCombo = MineCombo.getInstance();
        for (Player player : getServer().getOnlinePlayers()) {
            mineCombo.removeBossBar(player);
        }
        for (Player player : getServer().getOnlinePlayers()) {
            PlayerData playerData = new PlayerData(player);
            playerData.saveData();
        }
        for (Player player : getServer().getOnlinePlayers()) {
            mineCombo.savePlayerComboData(player);
        }
    }
}
