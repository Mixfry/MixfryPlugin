package com.mixfry.mixfryplugin.Minecombo.Cosmetics;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class TotalPointBonus {

    private final MixfryPlugin plugin;

    public TotalPointBonus(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    public int getPlayerLevel(Player player) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_combo.yml");
        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            int totalComboPoints = config.getInt("totalComboPoints", 0);
            return totalComboPoints / 10;
        }
        return 0;
    }
}