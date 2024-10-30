package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        inventory.setItem(36, createGrandmaItem(player)); // ここを確認
        inventory.setItem(53, createRankingItem(player));

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
                ChatColor.GOLD + "0" + ChatColor.DARK_GRAY + " per second",
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
    ItemStack grandma = new ItemStack(Material.CAKE, data.getGrandmaLevel());
    ItemMeta meta = grandma.getItemMeta();

    meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Grandma [" + data.getGrandmaLevel() + "]");
    int nextCookiesPerSecond = data.getCookiesPerSecond() + 1;
    int cost = (int) (50 * Math.pow(GRANDMA_COST_MULTIPLIER, data.getGrandmaLevel()));
    meta.setLore(Arrays.asList(
            ChatColor.GRAY + "Cookie Per Second: +" + ChatColor.GOLD + data.getCookiesPerSecond() + " Cookie",
            ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Grandma [" + (data.getGrandmaLevel() + 1) + "]",
            ChatColor.DARK_GRAY + "+" + data.getCookiesPerSecond() + " Cookie per second",
            ChatColor.GOLD + "+" + nextCookiesPerSecond + " Cookie per second",
            "",
            ChatColor.GRAY + "Cost",
            ChatColor.GOLD + data.formatNumber(cost) + " Cookie",
            "",
            ChatColor.YELLOW + "Click to upgrade!"
    ));
    grandma.setItemMeta(meta);

    return grandma;
}

    private ItemStack createRankingItem(Player player) {
        ItemStack wheat = new ItemStack(Material.WHEAT);
        ItemMeta meta = wheat.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Cookie Clicker Ranking");

        List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerData data = getPlayerData(onlinePlayer);
            sortedPlayers.add(new AbstractMap.SimpleEntry<>(onlinePlayer.getName(), data.getAllTimeCookies()));
        }

        sortedPlayers.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

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
        if (inventory.getItem(36) != null && inventory.getItem(36).getType() == Material.CAKE) {
            ItemStack grandmaItem = createGrandmaItem(player);
            inventory.setItem(36, grandmaItem);
        }
    }

    private void updateRankingItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(53) != null && inventory.getItem(53).getType() == Material.WHEAT) {
            ItemStack rankingItem = createRankingItem(player);
            inventory.setItem(53, rankingItem);
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
                switch (event.getCurrentItem().getType()) {
                    case COOKIE:
                        data.incrementCookieCount();
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
                        updateCookieItem(player);
                        break;

                    case GOLD_INGOT:
                        if (data.upgradeGiantHand()) {
                            player.sendMessage(ChatColor.GREEN + "Giant Hand upgraded to " + getRomanNumerals(data.getGiantHandLevel()) + "!");
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            updateGiantHandItem(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        }
                        break;

                    case CAKE:
                        if (data.upgradeGrandma()) {
                            player.sendMessage(ChatColor.GREEN + "Grandma upgraded to [" + data.getGrandmaLevel() + "]!");
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            updateGrandmaItem(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        }
                        break;

                    case WHEAT:
                        player.sendMessage(ChatColor.GREEN + "更新中...");
                        savePlayerData(player);
                        break;

                    default:
                        break;
                }
            }

            updateRankingItem(player);
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
}