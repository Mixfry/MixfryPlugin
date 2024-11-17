package com.mixfry.mixfryplugin.CookieClicker;

import com.mixfry.mixfryplugin.MixfryPlugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.math.BigInteger;

public class PlayerData {
    private static final Map<UUID, PlayerData> allPlayerData = new HashMap<>();

    private final Player player;
    private final UUID playerUUID;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private long cookieCount;
    private long allTimeCookies;
    private int giantHandLevel;
    private int cookiesPerClick;
    private int grandmaLevel;
    private int farmLevel;
    private int mineLevel;
    private int factoryLevel;
    private int cookiesPerSecond;
    private long lastCloseTime;
    private long lastLogoutTime;
    private long offlineCookies;

    public PlayerData(Player player) {
        this.player = player;
        this.playerUUID = player.getUniqueId();
        this.dataFile = new File(MixfryPlugin.getInstance().getDataFolder(), player.getName() + "_cookies.yml");
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
        cookieCount = dataConfig.getLong("cookieCount", 0);
        allTimeCookies = dataConfig.getLong("allTimeCookies", 0);
        giantHandLevel = dataConfig.getInt("giantHandLevel", 1);
        cookiesPerClick = dataConfig.getInt("cookiesPerClick", 1);
        grandmaLevel = dataConfig.getInt("grandmaLevel", 1);
        farmLevel = dataConfig.getInt("farmLevel", 0);
        mineLevel = dataConfig.getInt("mineLevel", 0);
        factoryLevel = dataConfig.getInt("factoryLevel", 0);
        cookiesPerSecond = dataConfig.getInt("cookiesPerSecond", 0);
        lastLogoutTime = dataConfig.getLong("lastLogoutTime", System.currentTimeMillis());
        offlineCookies = dataConfig.getLong("offlineCookies", 0);
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
        dataConfig.set("lastCloseTime", lastCloseTime);
        dataConfig.set("lastLogoutTime", lastLogoutTime);
        dataConfig.set("offlineCookies", offlineCookies);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getCookieCount() {
        return cookieCount;
    }

    public long getAllTimeCookies() {
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

    public long getOfflineCookies() {
        return offlineCookies;
    }

    public long getLastCloseTime() {
        return lastCloseTime;
    }

    public void resetOfflineCookies() {
        offlineCookies = 0;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public void setCookieCount(long cookieCount) {
        this.cookieCount = cookieCount;
    }

    public void setAllTimeCookies(long allTimeCookies) {
        this.allTimeCookies = allTimeCookies;
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
        BigInteger cost = calculateGiantHandCost(giantHandLevel);
        if (BigInteger.valueOf(cookieCount).compareTo(cost) >= 0) {
            cookieCount -= cost.longValue();
            giantHandLevel++;
            cookiesPerClick++;
            return true;
        }
        return false;
    }

    public boolean upgradeGrandma() {
        BigInteger cost = calculateGrandmaCost(grandmaLevel);
        if (BigInteger.valueOf(cookieCount).compareTo(cost) >= 0) {
            cookieCount -= cost.longValue();
            grandmaLevel++;
            cookiesPerSecond++;
            return true;
        }
        return false;
    }

    public boolean upgradeFarm() {
        BigInteger cost = calculateFarmCost(farmLevel);
        if (BigInteger.valueOf(cookieCount).compareTo(cost) >= 0) {
            cookieCount -= cost.longValue();
            farmLevel++;
            cookiesPerSecond += 2;
            return true;
        }
        return false;
    }

    public boolean upgradeMine() {
        BigInteger cost = calculateMineCost(mineLevel);
        if (BigInteger.valueOf(cookieCount).compareTo(cost) >= 0) {
            cookieCount -= cost.longValue();
            mineLevel++;
            cookiesPerSecond += 3;
            return true;
        }
        return false;
    }

    public boolean upgradeFactory() {
        BigInteger cost = calculateFactoryCost(factoryLevel);
        if (BigInteger.valueOf(cookieCount).compareTo(cost) >= 0) {
            cookieCount -= cost.longValue();
            factoryLevel++;
            cookiesPerSecond += 4;
            return true;
        }
        return false;
    }

    public BigInteger calculateGiantHandCost(int level) {
        return BigInteger.valueOf(25).multiply(BigInteger.valueOf((long) Math.pow(1.1, level))).add(BigInteger.valueOf(10 * level));
    }

    public BigInteger calculateGrandmaCost(int level) {
        return BigInteger.valueOf(50).multiply(BigInteger.valueOf((long) Math.pow(1.1, level))).add(BigInteger.valueOf(10 * level));
    }

    public BigInteger calculateFarmCost(int level) {
        return BigInteger.valueOf(1400).multiply(BigInteger.valueOf((long) Math.pow(1.1, level))).add(BigInteger.valueOf(10 * level));
    }

    public BigInteger calculateMineCost(int level) {
        return BigInteger.valueOf(3200).multiply(BigInteger.valueOf((long) Math.pow(1.1, level))).add(BigInteger.valueOf(10 * level));
    }

    public BigInteger calculateFactoryCost(int level) {
        return BigInteger.valueOf(5700).multiply(BigInteger.valueOf((long) Math.pow(1.1, level))).add(BigInteger.valueOf(10 * level));
    }

    public void incrementCookiesPerSecond() {
        cookieCount += cookiesPerSecond;
        allTimeCookies += cookiesPerSecond;
    }

    public void setLastCloseTime(long lastCloseTime) {
        this.lastCloseTime = lastCloseTime;
    }

    private void addOfflineCookies() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastCloseTime;
        int secondsElapsed = (int) (elapsedTime / 1000);
        long offlineCookies = secondsElapsed * cookiesPerSecond;
        cookieCount += offlineCookies;
        allTimeCookies += offlineCookies;
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

    public void calculateOfflineCookies() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastLogoutTime;
        int secondsElapsed = (int) (elapsedTime / 1000);
        offlineCookies += secondsElapsed * cookiesPerSecond;
        lastLogoutTime = currentTime;
    }

    public String formatNumber(long number) {
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