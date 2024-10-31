package com.mixfry.mixfryplugin;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private static final Map<UUID, PlayerData> allPlayerData = new HashMap<>();

    private final Player player;
    private final UUID playerUUID;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private int cookieCount;
    private int allTimeCookies;
    private int giantHandLevel;
    private int cookiesPerClick;
    private int grandmaLevel;
    private int farmLevel;
    private int mineLevel;
    private int factoryLevel;
    private int cookiesPerSecond;
    private long lastLogoutTime;
    private int offlineCookies;

    public PlayerData(Player player) {
        this.player = player;
        this.playerUUID = player.getUniqueId();
        this.dataFile = new File("path/to/data/folder", playerUUID.toString() + ".yml");
        loadData();
        allPlayerData.put(playerUUID, this);
    }

    public static Collection<PlayerData> getAllPlayerData() {
        return allPlayerData.values();
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
        grandmaLevel = dataConfig.getInt("grandmaLevel", 1);
        farmLevel = dataConfig.getInt("farmLevel", 0);
        mineLevel = dataConfig.getInt("mineLevel", 0);
        factoryLevel = dataConfig.getInt("factoryLevel", 0);
        cookiesPerSecond = dataConfig.getInt("cookiesPerSecond", 0);
        lastLogoutTime = dataConfig.getLong("lastLogoutTime", System.currentTimeMillis());
        offlineCookies = dataConfig.getInt("offlineCookies", 0);
    }

    public void saveData() {
        dataConfig.set("cookieCount", cookieCount);
        dataConfig.set("allTimeCookies", allTimeCookies);
        dataConfig.set("giantHandLevel", giantHandLevel);
        dataConfig.set("cookiesPerClick", cookiesPerClick);
        dataConfig.set("grandmaLevel", grandmaLevel);
        dataConfig.set("farmLevel", farmLevel);
        dataConfig.set("mineLevel", mineLevel);
        dataConfig.set("factoryLevel", factoryLevel);
        dataConfig.set("cookiesPerSecond", cookiesPerSecond);
        dataConfig.set("lastLogoutTime", lastLogoutTime);
        dataConfig.set("offlineCookies", offlineCookies);
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

    public UUID getPlayerUUID() {
        return player.getUniqueId();
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

    public int getFarmLevel() {
        return farmLevel;
    }

    public int getMineLevel() {
        return mineLevel;
    }

    public int getFactoryLevel() {
        return factoryLevel;
    }

    public int getCookiesPerSecond() {
        return cookiesPerSecond;
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public int getOfflineCookies() {
        return offlineCookies;
    }

    public void resetOfflineCookies() {
        offlineCookies = 0;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public void setCookieCount(int cookieCount) {
        this.cookieCount = cookieCount;
    }

    public void setFarmLevel(int farmLevel) {
        this.farmLevel = farmLevel;
    }

    public void setMineLevel(int mineLevel) {
        this.mineLevel = mineLevel;
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
        int cost = calculateGrandmaCost(grandmaLevel);
        if (cookieCount >= cost) {
            cookieCount -= cost;
            grandmaLevel++;
            cookiesPerSecond++;
            return true;
        }
        return false;
    }

    public boolean upgradeFarm() {
        int cost = calculateFarmCost(farmLevel);
        if (cookieCount >= cost) {
            cookieCount -= cost;
            farmLevel++;
            cookiesPerSecond += 2;
            return true;
        }
        return false;
    }

    public boolean upgradeMine() {
        int cost = calculateMineCost(mineLevel);
        if (cookieCount >= cost) {
            cookieCount -= cost;
            mineLevel++;
            cookiesPerSecond += 3;
            return true;
        }
        return false;
    }

    public boolean upgradeFactory() {
        int cost = calculateFactoryCost(factoryLevel);
        if (cookieCount >= cost) {
            cookieCount -= cost;
            factoryLevel++;
            cookiesPerSecond += 4;
            return true;
        }
        return false;
    }

    private int calculateGrandmaCost(int level) {
        return (int) (50 * Math.pow(1.1, level) + 10 * level);
    }

    private int calculateFarmCost(int level) {
        return (int) (1400 * Math.pow(1.1, level) + 10 * level);
    }

    private int calculateMineCost(int level) {
        return (int) (3200 * Math.pow(1.1, level) + 10 * level);
    }

    private int calculateFactoryCost(int level) {
        return (int) (5700 * Math.pow(1.1, level) + 10 * level);
    }

    public void incrementCookiesPerSecond() {
        cookieCount += cookiesPerSecond;
        allTimeCookies += cookiesPerSecond;
    }

    public void calculateOfflineCookies() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastLogoutTime;
        int secondsElapsed = (int) (elapsedTime / 1000);
        offlineCookies += secondsElapsed * cookiesPerSecond;
        lastLogoutTime = currentTime;
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