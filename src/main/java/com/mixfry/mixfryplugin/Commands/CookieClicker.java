package com.mixfry.mixfryplugin.Commands;

import com.mixfry.mixfryplugin.MixfryPlugin;
import com.mixfry.mixfryplugin.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class CookieClicker implements Listener {

    private final String chestGuiTitle = "Cookie Clicker";
    private static final int COST_MULTIPLIER = 25;
    private static final double GRANDMA_COST_MULTIPLIER = 1.25;
    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public CookieClicker() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
        startCookieGenerationTask();
    }

    private PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player));
    }

    private void savePlayerData(Player player) {
        PlayerData data = getPlayerData(player);
        data.saveData();
    }

    private void startCookieGenerationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData data = getPlayerData(player);
                    data.updateRanking();
                    data.incrementCookiesPerSecond();
                    if (player.getOpenInventory().getTitle().equals(chestGuiTitle)) {
                        updateCookieItem(player);
                        updateRankingItem(player);
                        updateGrandmaItem(player);
                    }
                }
            }
        }.runTaskTimer(MixfryPlugin.getInstance(), 0, 20);
    }

    public void openCookieInventory(Player player) {
        player.openInventory(createCookieInventory(player));
    }

    private Inventory createCookieInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, chestGuiTitle);
        inventory.setItem(13, createCookieItem(player));
        inventory.setItem(27, createGiantHandItem(player));
        inventory.setItem(36, createGrandmaItem(player));
        inventory.setItem(37, createFarmItem(player));
        inventory.setItem(38, createMineItem(player));
        inventory.setItem(39, createFactoryItem(player));
        inventory.setItem(45, createOfflineRewardItem(player));
        inventory.setItem(53, createRankingItem(player));

        ItemStack barrierBlock = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrierBlock.getItemMeta();
        barrierMeta.setDisplayName(ChatColor.RED + "COMING SOON");
        barrierBlock.setItemMeta(barrierMeta);

        inventory.setItem(8, barrierBlock);
        for (int i = 28; i <= 35; i++) {inventory.setItem(i, barrierBlock);}
        for (int i = 40; i <= 44; i++) {inventory.setItem(i, barrierBlock);}

        ItemStack fillerGlass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerGlass.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.RESET + "");
        fillerGlass.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerGlass);
            }
        }
        return inventory;
    }

    private ItemStack createCookieItem(Player player) {
        PlayerData data = getPlayerData(player);
        ItemStack cookie = new ItemStack(Material.COOKIE);
        ItemMeta meta = cookie.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + data.formatNumber(data.getCookieCount()) + ChatColor.GOLD + " Cookie");

        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Cookie Production",
                ChatColor.GOLD + data.formatNumber(data.getCookiesPerSecond()) + ChatColor.DARK_GRAY + " per second",
                ChatColor.GRAY + "All-Time Cookie: " + ChatColor.GOLD + data.formatNumber(data.getAllTimeCookies())
        ));
        cookie.setItemMeta(meta);

        return cookie;
    }

    private ItemStack createGiantHandItem(Player player) {
        PlayerData data = getPlayerData(player);
        ItemStack giantHand = new ItemStack(Material.GOLD_INGOT, data.getGiantHandLevel());
        ItemMeta meta = giantHand.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Giant Hand " + getRomanNumerals(data.getGiantHandLevel()));
        int nextCookiesPerClick = data.getCookiesPerClick() + 1;
        int cost = (int) (25 * Math.pow(1.5, data.getGiantHandLevel() - 1));
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Cookie Per Click: +" + ChatColor.GOLD + data.getCookiesPerClick() + " Cookie",
                ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Giant Hand " + getRomanNumerals(data.getGiantHandLevel() + 1),
                ChatColor.DARK_GRAY + "+" + data.getCookiesPerClick() + " Cookie per click",
                ChatColor.GOLD + "+" + nextCookiesPerClick + " Cookie per click",
                "",
                ChatColor.GRAY + "Cost",
                ChatColor.GOLD + data.formatNumber(cost) + " Cookie",
                "",
                ChatColor.YELLOW + "Click to upgrade!"
        ));
        giantHand.setItemMeta(meta);

        return giantHand;
    }

    private ItemStack createGrandmaItem(Player player) {
        PlayerData data = getPlayerData(player);
        ItemStack grandma = new ItemStack(Material.FURNACE);
        ItemMeta meta = grandma.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Grandma [" + data.getGrandmaLevel() + "]");
        int nextGrandmaCookiesPerSecond = data.getGrandmaLevel() + 1;
        int cost = (int) (50 * Math.pow(GRANDMA_COST_MULTIPLIER, data.getGrandmaLevel()));
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Grandma Cookie Per Second: +" + ChatColor.GOLD + data.getGrandmaLevel() + " Cookie",
                ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Grandma [" + (data.getGrandmaLevel() + 1) + "]",
                ChatColor.DARK_GRAY + "+" + data.getGrandmaLevel() + " Cookie per second",
                ChatColor.GOLD + "+" + nextGrandmaCookiesPerSecond + " Cookie per second",
                "",
                ChatColor.GRAY + "Cost",
                ChatColor.GOLD + data.formatNumber(cost) + " Cookie",
                "",
                ChatColor.YELLOW + "Click to upgrade!"
        ));
        grandma.setItemMeta(meta);

        return grandma;
    }

    private ItemStack createFarmItem(Player player) {
        PlayerData data = getPlayerData(player);
        ItemStack farm = new ItemStack(Material.FARMLAND);
        ItemMeta meta = farm.getItemMeta();

        if (data.getGrandmaLevel() < 20) {
            meta.setDisplayName(ChatColor.RED + "Farm");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Requires Grandma level 20 to unlock!"
            ));
        } else {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Farm [" + data.getFarmLevel() + "]");
            int nextFarmCookiesPerSecond = (data.getFarmLevel() + 1) * 2;
            int cost = 1400 * (int) Math.pow(1.25, data.getFarmLevel());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Farm Cookie Per Second: +" + ChatColor.GOLD + (data.getFarmLevel() * 2) + " Cookie",
                    ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Farm [" + (data.getFarmLevel() + 1) + "]",
                    ChatColor.DARK_GRAY + "+" + (data.getFarmLevel() * 2) + " Cookie per second",
                    ChatColor.GOLD + "+" + nextFarmCookiesPerSecond + " Cookie per second",
                    "",
                    ChatColor.GRAY + "Cost",
                    ChatColor.GOLD + data.formatNumber(cost) + " Cookie",
                    "",
                    ChatColor.YELLOW + "Click to upgrade!"
            ));
        }
        farm.setItemMeta(meta);

        return farm;
    }

    private ItemStack createMineItem(Player player) {
        PlayerData data = getPlayerData(player);
        ItemStack mine = new ItemStack(Material.COAL_ORE);
        ItemMeta meta = mine.getItemMeta();

        if (data.getFarmLevel() < 20) {
            meta.setDisplayName(ChatColor.RED + "Mine");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Requires Farm level 20 to unlock!"
            ));
        } else {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mine [" + data.getMineLevel() + "]");
            int nextMineCookiesPerSecond = (data.getMineLevel() + 1) * 3;
            int cost = 3200 * (int) Math.pow(1.25, data.getMineLevel());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Mine Cookie Per Second: +" + ChatColor.GOLD + (data.getMineLevel() * 3) + " Cookie",
                    ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Mine [" + (data.getMineLevel() + 1) + "]",
                    ChatColor.DARK_GRAY + "+" + (data.getMineLevel() * 3) + " Cookie per second",
                    ChatColor.GOLD + "+" + nextMineCookiesPerSecond + " Cookie per second",
                    "",
                    ChatColor.GRAY + "Cost",
                    ChatColor.GOLD + data.formatNumber(cost) + " Cookie",
                    "",
                    ChatColor.YELLOW + "Click to upgrade!"
            ));
        }
        mine.setItemMeta(meta);

        return mine;
    }

    private ItemStack createFactoryItem(Player player) {
        PlayerData data = getPlayerData(player);
        ItemStack factory = new ItemStack(Material.STONECUTTER);
        ItemMeta meta = factory.getItemMeta();

        if (data.getMineLevel() < 20) {
            meta.setDisplayName(ChatColor.RED + "Factory");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Requires Mine level 20 to unlock!"
            ));
        } else {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Factory [" + data.getFactoryLevel() + "]");
            int nextFactoryCookiesPerSecond = (data.getFactoryLevel() + 1) * 4;
            int cost = 5700 * (int) Math.pow(1.25, data.getFactoryLevel());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Factory Cookie Per Second: +" + ChatColor.GOLD + (data.getFactoryLevel() * 4) + " Cookie",
                    ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Factory [" + (data.getFactoryLevel() + 1) + "]",
                    ChatColor.DARK_GRAY + "+" + (data.getFactoryLevel() * 4) + " Cookie per second",
                    ChatColor.GOLD + "+" + nextFactoryCookiesPerSecond + " Cookie per second",
                    "",
                    ChatColor.GRAY + "Cost",
                    ChatColor.GOLD + data.formatNumber(cost) + " Cookie",
                    "",
                    ChatColor.YELLOW + "Click to upgrade!"
            ));
        }
        factory.setItemMeta(meta);

        return factory;
    }

    private ItemStack createRankingItem(Player player) {
        ItemStack wheat = new ItemStack(Material.WHEAT);
        ItemMeta meta = wheat.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Cookie Clicker Ranking");

        List<Map.Entry<String, Long>> sortedPlayers = new ArrayList<>();
        for (PlayerData data : PlayerData.getAllPlayerData()) {
            sortedPlayers.add(new AbstractMap.SimpleEntry<>(Bukkit.getOfflinePlayer(data.getPlayerUUID()).getName(), data.getAllTimeCookies()));
        }

        sortedPlayers.sort(Map.Entry.<String, Long>comparingByValue().reversed());

        List<String> lore = new ArrayList<>();
        if (sortedPlayers.size() > 0) {
            lore.add(ChatColor.GOLD + "#1 " + sortedPlayers.get(0).getKey() + " " + getPlayerData(player).formatNumber(sortedPlayers.get(0).getValue()));
        }
        if (sortedPlayers.size() > 1) {
            lore.add(ChatColor.YELLOW + "#2 " + sortedPlayers.get(1).getKey() + " " + getPlayerData(player).formatNumber(sortedPlayers.get(1).getValue()));
        }
        if (sortedPlayers.size() > 2) {
            lore.add(ChatColor.GOLD + "#3 " + sortedPlayers.get(2).getKey() + " " + getPlayerData(player).formatNumber(sortedPlayers.get(2).getValue()));
        }

        lore.add("");
        PlayerData data = getPlayerData(player);
        int playerRank = -1;
        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getKey().equals(player.getName())) {
                playerRank = i + 1;
                break;
            }
        }

        if (playerRank != -1) {
            lore.add("#" + playerRank + " " + player.getName() + " " + data.formatNumber(data.getAllTimeCookies()));
        } else {
            lore.add(ChatColor.RED + "Rank not found");
        }

        meta.setLore(lore);
        wheat.setItemMeta(meta);
        return wheat;
    }

    private ItemStack createOfflineRewardItem(Player player) {
        PlayerData data = getPlayerData(player);
        long offlineCookies = data.getOfflineCookies();

        ItemStack rewardItem;
        if (offlineCookies > 0) {
            rewardItem = new ItemStack(Material.CHEST_MINECART);
        } else {
            rewardItem = new ItemStack(Material.MINECART);
        }

        ItemMeta meta = rewardItem.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Offline Reward");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "1% chance to ×10 cookie !");
        lore.add(" ");
        lore.add(ChatColor.GRAY + "Gathered " + ChatColor.GOLD + data.formatNumber(offlineCookies) + ChatColor.GRAY + " Cookie");

        long elapsedTime = System.currentTimeMillis() - data.getLastLogoutTime();
        int secondsElapsed = (int) (elapsedTime / 1000);
        int days = secondsElapsed / 86400;
        int hours = (secondsElapsed % 86400) / 3600;
        int minutes = (secondsElapsed % 3600) / 60;

        StringBuilder timeString = new StringBuilder();
        if (days > 0) {
            timeString.append(days).append("day ");
        }
        if (hours > 0) {
            timeString.append(hours).append("hour ");
        }
        timeString.append(minutes).append("minute");

        lore.add(ChatColor.GRAY + "Offline time: " + timeString.toString().trim());

        meta.setLore(lore);
        rewardItem.setItemMeta(meta);

        return rewardItem;
    }

    private void updateCookieItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(13) != null && inventory.getItem(13).getType() == Material.COOKIE) {
            ItemStack cookieItem = createCookieItem(player);
            inventory.setItem(13, cookieItem);
        }
    }

    private void updateGiantHandItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(27) != null && inventory.getItem(27).getType() == Material.GOLD_INGOT) {
            ItemStack giantHandItem = createGiantHandItem(player);
            inventory.setItem(27, giantHandItem);
        }
    }

    private void updateGrandmaItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(36) != null && inventory.getItem(36).getType() == Material.FURNACE) {
            ItemStack grandmaItem = createGrandmaItem(player);
            inventory.setItem(36, grandmaItem);
        }
    }

    private void updateFarmItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(37) != null && inventory.getItem(37).getType() == Material.FARMLAND) {
            ItemStack farmItem = createFarmItem(player);
            inventory.setItem(37, farmItem);
        }
    }

    private void updateMineItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(38) != null && inventory.getItem(38).getType() == Material.COAL_ORE) {
            ItemStack mineItem = createMineItem(player);
            inventory.setItem(38, mineItem);
        }
    }

    private void updateFactoryItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(39) != null && inventory.getItem(39).getType() == Material.STONECUTTER) {
            ItemStack factoryItem = createFactoryItem(player);
            inventory.setItem(39, factoryItem);
        }
    }

    private void updateRankingItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(53) != null && inventory.getItem(53).getType() == Material.WHEAT) {
            ItemStack rankingItem = createRankingItem(player);
            inventory.setItem(53, rankingItem);
        }
    }

    private void updateOfflineRewardItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(45) != null && (inventory.getItem(45).getType() == Material.MINECART || inventory.getItem(45).getType() == Material.CHEST_MINECART)) {
            ItemStack rewardItem = createOfflineRewardItem(player);
            inventory.setItem(45, rewardItem);
        }
    }

    private String getRomanNumerals(int level) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (level >= values[i]) {
                level -= values[i];
                result.append(numerals[i]);
            }
        }

        return result.toString();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(chestGuiTitle)) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            PlayerData data = getPlayerData(player);

            if (event.getCurrentItem() != null) {
                Material itemType = event.getCurrentItem().getType();
                boolean updateInventory = false;

                switch (itemType) {
                    case COOKIE:
                        data.incrementCookieCount();
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
                        updateInventory = true;
                        break;

                    case GOLD_INGOT:
                        if (data.upgradeGiantHand()) {
                            player.sendMessage(ChatColor.GREEN + "Giant Hand upgraded to " + getRomanNumerals(data.getGiantHandLevel()) + "!");
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            updateInventory = true;
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        }
                        break;

                    case FURNACE:
                        if (data.upgradeGrandma()) {
                            player.sendMessage(ChatColor.GREEN + "Grandma upgraded to [" + data.getGrandmaLevel() + "]!");
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            updateInventory = true;
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        }
                        break;

                    case FARMLAND:
                        if (data.getGrandmaLevel() >= 20) {
                            if (data.upgradeFarm()) {
                                player.sendMessage(ChatColor.GREEN + "Farm upgraded to [" + data.getFarmLevel() + "]!");
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                updateInventory = true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Requires Grandma level 20 to unlock!");
                        }
                        break;

                    case COAL_ORE:
                        if (data.getFarmLevel() >= 20) {
                            if (data.upgradeMine()) {
                                player.sendMessage(ChatColor.GREEN + "Mine upgraded to [" + data.getMineLevel() + "]!");
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                updateInventory = true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Requires Farm level 20 to unlock!");
                        }
                        break;

                    case STONECUTTER:
                        if (data.getMineLevel() >= 20) {
                            if (data.upgradeFactory()) {
                                player.sendMessage(ChatColor.GREEN + "Factory upgraded to [" + data.getFactoryLevel() + "]!");
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                updateInventory = true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Requires Mine level 20 to unlock!");
                        }
                        break;

                    case WHEAT:
                        player.sendMessage(ChatColor.GREEN + "Refreshing...");
                        savePlayerData(player);
                        break;

                    case MINECART:
                    case CHEST_MINECART:
                        long offlineCookies = data.getOfflineCookies();

                        if (offlineCookies > 0) {
                            double chance = Math.random();
                            if (chance < 0.01) {
                                offlineCookies *= 10;
                                player.sendMessage(ChatColor.GOLD + "Lucky! You received 10x cookies!");
                                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
                            }

                            data.setCookieCount(data.getCookieCount() + offlineCookies);
                            data.setAllTimeCookies(data.getAllTimeCookies() + offlineCookies); // allTimeCookiesを更新
                            data.updateRanking(); // ランキングを更新
                            player.sendMessage(ChatColor.GREEN + "You received " + data.formatNumber(offlineCookies) + " cookies from offline rewards!");
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            data.resetOfflineCookies();
                            savePlayerData(player);
                            updateInventory = true;
                        } else {
                            player.sendMessage(ChatColor.RED + "No cookies gathered while offline.");
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                        }
                        break;

                    default:
                        break;
                }

                if (updateInventory) {
                    updateCookieItem(player);
                    updateGiantHandItem(player);
                    updateGrandmaItem(player);
                    updateFarmItem(player);
                    updateMineItem(player);
                    updateFactoryItem(player);
                    updateRankingItem(player);
                    updateOfflineRewardItem(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(chestGuiTitle)) {
            Player player = (Player) event.getPlayer();
            PlayerData data = getPlayerData(player);
            data.setLastCloseTime(System.currentTimeMillis());
            savePlayerData(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = getPlayerData(player);
        data.calculateOfflineCookies();
        savePlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = getPlayerData(player);
        data.setLastLogoutTime(System.currentTimeMillis());
        savePlayerData(player);
    }
}