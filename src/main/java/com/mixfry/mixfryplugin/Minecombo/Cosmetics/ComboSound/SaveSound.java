package com.mixfry.mixfryplugin.Minecombo.Cosmetics.ComboSound;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SaveSound {

    private final MixfryPlugin plugin;

    public SaveSound(MixfryPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveComboSoundSetting(Player player, Sound sound) {
        File configFile = new File(plugin.getDataFolder(), player.getName() + "_config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("comboSound", sound == null ? "null" : sound.name());
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "コンボ音設定の保存中にエラーが発生しました。", e);
        }
    }
}