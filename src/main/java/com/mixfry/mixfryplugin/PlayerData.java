package com.mixfry.mixfryplugin;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class PlayerData {
    private final Player player;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public PlayerData(Player player) {
        this.player = player;
        this.dataFile = new File(MixfryPlugin.getInstance().getDataFolder(), player.getName() + "_cookies.yml");
        loadData();
    }

    public void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                saveData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCookieCount() {
        return dataConfig.getInt("cookieCount", 0);
    }

    public void setCookieCount(int count) {
        dataConfig.set("cookieCount", count);
        saveData();
        updateGlobalRanking(count);
    }

    public int getAllTimeCookies() {
        return dataConfig.getInt("allTimeCookies", 0);
    }

    public void setAllTimeCookies(int count) {
        dataConfig.set("allTimeCookies", count);
        saveData();
    }

    public int getGiantHandLevel() {
        return dataConfig.getInt("giantHandLevel", 1);
    }

    public void setGiantHandLevel(int level) {
        dataConfig.set("giantHandLevel", level);
        saveData();
    }

    public int getCookiesPerClick() {
        return dataConfig.getInt("cookiesPerClick", 1);
    }

    public void setCookiesPerClick(int count) {
        dataConfig.set("cookiesPerClick", count);
        saveData();
    }

    private void updateGlobalRanking(int cookiesEarned) {
        File rankingsFile = new File(MixfryPlugin.getInstance().getDataFolder(), "rankings.yml");
        FileConfiguration rankingsConfig = YamlConfiguration.loadConfiguration(rankingsFile);

        int totalCookies = getAllTimeCookies() + cookiesEarned;
        setAllTimeCookies(totalCookies);

        rankingsConfig.set(player.getName(), totalCookies);

        try {
            rankingsConfig.save(rankingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeData() {
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }
}
