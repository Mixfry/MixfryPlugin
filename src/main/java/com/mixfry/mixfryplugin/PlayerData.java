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
    private int cookieCount;
    private int allTimeCookies;
    private int giantHandLevel;
    private int cookiesPerClick;
    private int grandmaLevel;
    private int cookiesPerSecond;
    private long lastCloseTime;

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
        cookieCount = dataConfig.getInt("cookieCount", 0);
        allTimeCookies = dataConfig.getInt("allTimeCookies", 0);
        giantHandLevel = dataConfig.getInt("giantHandLevel", 1);
        cookiesPerClick = dataConfig.getInt("cookiesPerClick", 1);
        grandmaLevel = dataConfig.getInt("grandmaLevel", 0);
        cookiesPerSecond = dataConfig.getInt("cookiesPerSecond", 0);
        lastCloseTime = dataConfig.getLong("lastCloseTime", System.currentTimeMillis());
    }

    public void saveData() {
        dataConfig.set("cookieCount", cookieCount);
        dataConfig.set("allTimeCookies", allTimeCookies);
        dataConfig.set("giantHandLevel", giantHandLevel);
        dataConfig.set("cookiesPerClick", cookiesPerClick);
        dataConfig.set("grandmaLevel", grandmaLevel);
        dataConfig.set("cookiesPerSecond", cookiesPerSecond);
        dataConfig.set("lastCloseTime", lastCloseTime);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCookieCount() {
        return cookieCount;
    }

    public int getAllTimeCookies() {
        return allTimeCookies;
    }

    public int getGiantHandLevel() {
        return giantHandLevel;
    }

    public int getCookiesPerClick() {
        return cookiesPerClick;
    }

    public int getGrandmaLevel() {
        return grandmaLevel;
    }

    public int getCookiesPerSecond() {
        return cookiesPerSecond;
    }

    public long getLastCloseTime() {
        return lastCloseTime;
    }

    public void setCookieCount(int cookieCount) {
        this.cookieCount = cookieCount;
    }

    public void incrementCookieCount() {
        cookieCount += cookiesPerClick;
        allTimeCookies += cookiesPerClick;
    }

    public boolean upgradeGiantHand() {
        int cost = (int) (25 * Math.pow(1.5, giantHandLevel - 1));
        if (cookieCount >= cost) {
            cookieCount -= cost;
            giantHandLevel++;
            cookiesPerClick++;
            return true;
        }
        return false;
    }

    public boolean upgradeGrandma() {
        int cost = (int) (50 * Math.pow(1.25, grandmaLevel));
        if (cookieCount >= cost) {
            cookieCount -= cost;
            grandmaLevel++;
            cookiesPerSecond++;
            return true;
        }
        return false;
    }

    public void incrementCookiesPerSecond() {
        cookieCount += cookiesPerSecond;
        allTimeCookies += cookiesPerSecond;
    }

    public void setLastCloseTime(long lastCloseTime) {
        this.lastCloseTime = lastCloseTime;
    }

    public void updateRanking() {
        File rankingsFile = new File(MixfryPlugin.getInstance().getDataFolder(), "rankings.yml");
        FileConfiguration rankingsConfig = YamlConfiguration.loadConfiguration(rankingsFile);

        rankingsConfig.set(player.getName(), allTimeCookies);

        try {
            rankingsConfig.save(rankingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatNumber(int number) {
        if (number >= 1_000_000_000_000_000_000L) {
            return String.format("%.2fQi", number / 1_000_000_000_000_000_000.0);
        } else if (number >= 1_000_000_000_000_000L) {
            return String.format("%.2fQa", number / 1_000_000_000_000_000.0);
        } else if (number >= 1_000_000_000_000L) {
            return String.format("%.2fT", number / 1_000_000_000_000.0);
        } else if (number >= 1_000_000_000L) {
            return String.format("%.2fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000L) {
            return String.format("%.2fm", number / 1_000_000.0);
        } else if (number >= 1_000L) {
            return String.format("%.2fk", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
}