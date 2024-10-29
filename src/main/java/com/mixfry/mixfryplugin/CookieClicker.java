package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CookieClicker implements Listener {

    private final String chestGuiTitle = "Cookie Clicker";
    private int cookieCount = 0;
    private int allTimeCookies = 0;
    private int giantHandLevel = 1;
    private int cookiesPerClick = 1;
    private static final int COST_MULTIPLIER = 500;
    private Map<String, FileConfiguration> playerData = new HashMap<>();

    public CookieClicker() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
        startCookieGenerationTask();
    }

    private FileConfiguration loadPlayerData(Player player) {
        File playerFile = new File(MixfryPlugin.getInstance().getDataFolder(), player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    private void savePlayerData(Player player) {
        File playerFile = new File(MixfryPlugin.getInstance().getDataFolder(), player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("cookieCount", cookieCount);
        config.set("allTimeCookies", allTimeCookies);
        config.set("giantHandLevel", giantHandLevel);
        config.set("cookiesPerClick", cookiesPerClick);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startCookieGenerationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    FileConfiguration config = loadPlayerData(player);
                    cookieCount = config.getInt("cookieCount", 0);
                    allTimeCookies = config.getInt("allTimeCookies", 0);
                    giantHandLevel = config.getInt("giantHandLevel", 1);
                    cookiesPerClick = config.getInt("cookiesPerClick", 1);

                    updateRanking(player);
                    savePlayerData(player);

                    if (player.getOpenInventory().getTitle().equals(chestGuiTitle)) {
                        updateCookieItem(player);
                        updateRankingItem(player);
                    }
                }
            }
        }.runTaskTimer(MixfryPlugin.getInstance(), 0, 20);
    }

    public void openCookieInventory(Player player) {
        player.openInventory(createCookieInventory());
    }

    private Inventory createCookieInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, chestGuiTitle);
        inventory.setItem(13, createCookieItem());
        inventory.setItem(27, createGiantHandItem());
        inventory.setItem(53, createRankingItem());

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

    private ItemStack createCookieItem() {
        ItemStack cookie = new ItemStack(Material.COOKIE);
        ItemMeta meta = cookie.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + cookieCount + ChatColor.GOLD + " Cookie");

        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Cookie Production",
                ChatColor.GOLD + "0" + ChatColor.DARK_GRAY + " per second",
                ChatColor.GRAY + "All-Time Cookie: " + ChatColor.GOLD + allTimeCookies
        ));
        cookie.setItemMeta(meta);

        return cookie;
    }

    private ItemStack createGiantHandItem() {
        ItemStack giantHand = new ItemStack(Material.GOLD_INGOT, giantHandLevel);
        ItemMeta meta = giantHand.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Giant Hand " + getRomanNumerals(giantHandLevel));
        int nextCookiesPerClick = cookiesPerClick + 1;
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Cookie Per Click: +" + ChatColor.GOLD + cookiesPerClick + " Cookie",
                ChatColor.GREEN + "UPGRADE" + ChatColor.DARK_GRAY + "→" + ChatColor.LIGHT_PURPLE + "Giant Hand " + getRomanNumerals(giantHandLevel + 1),
                ChatColor.DARK_GRAY + "+" + cookiesPerClick + " Cookie per click",
                ChatColor.GOLD + "+" + nextCookiesPerClick + " Cookie per click",
                "",
                ChatColor.GRAY + "Cost",
                ChatColor.GOLD + String.valueOf(giantHandLevel * COST_MULTIPLIER) + " Cookie",
                "",
                ChatColor.YELLOW + "Click to upgrade!"
        ));
        giantHand.setItemMeta(meta);

        return giantHand;
    }

    private ItemStack createRankingItem() {
        ItemStack wheat = new ItemStack(Material.WHEAT);
        ItemMeta meta = wheat.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Cookie Clicker Ranking");

        List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            FileConfiguration config = loadPlayerData(player);
            int playerAllTimeCookies = config.getInt("allTimeCookies", 0);
            sortedPlayers.add(new AbstractMap.SimpleEntry<>(player.getName(), playerAllTimeCookies));
        }

        sortedPlayers.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        List<String> lore = new ArrayList<>();
        if (sortedPlayers.size() > 0) {
            lore.add(ChatColor.GOLD + "#1 " + sortedPlayers.get(0).getKey() + " " + sortedPlayers.get(0).getValue());
        }
        if (sortedPlayers.size() > 1) {
            lore.add(ChatColor.YELLOW + "#2 " + sortedPlayers.get(1).getKey() + " " + sortedPlayers.get(1).getValue());
        }
        if (sortedPlayers.size() > 2) {
            lore.add(ChatColor.GOLD + "#3 " + sortedPlayers.get(2).getKey() + " " + sortedPlayers.get(2).getValue());
        }

        lore.add("");
        Player player = Bukkit.getPlayer(Bukkit.getOnlinePlayers().iterator().next().getName());
        int playerRank = sortedPlayers.indexOf(new AbstractMap.SimpleEntry<>(player.getName(), allTimeCookies)) + 1;

        lore.add("#" + playerRank + " " + player.getName() + " " + allTimeCookies);

        meta.setLore(lore);
        wheat.setItemMeta(meta);
        return wheat;
    }

    private void updateRanking(Player player) {
        FileConfiguration config = loadPlayerData(player);
        allTimeCookies = config.getInt("allTimeCookies", 0);
        config.set("allTimeCookies", allTimeCookies);
        savePlayerData(player);
    }

    private void updateRankingItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(53) != null && inventory.getItem(53).getType() == Material.WHEAT) {
            ItemStack rankingItem = createRankingItem();
            inventory.setItem(53, rankingItem);
        }
    }

    private String getRomanNumerals(int level) {
        switch (level) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return String.valueOf(level);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(chestGuiTitle)) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            FileConfiguration config = loadPlayerData(player);
            cookieCount = config.getInt("cookieCount", 0);
            allTimeCookies = config.getInt("allTimeCookies", 0);
            giantHandLevel = config.getInt("giantHandLevel", 1);
            cookiesPerClick = config.getInt("cookiesPerClick", 1);

            if (event.getCurrentItem() != null) {
                switch (event.getCurrentItem().getType()) {
                    case COOKIE:
                        cookieCount += cookiesPerClick;
                        allTimeCookies += cookiesPerClick;
                        config.set("cookieCount", cookieCount);
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
                        updateCookieItem(player);
                        break;

                    case GOLD_INGOT:
                        int cost = giantHandLevel * COST_MULTIPLIER;
                        if (cookieCount >= cost) {
                            cookieCount -= cost;
                            giantHandLevel++;
                            cookiesPerClick++;
                            config.set("cookieCount", cookieCount);
                            config.set("giantHandLevel", giantHandLevel);
                            config.set("cookiesPerClick", cookiesPerClick);
                            player.sendMessage(ChatColor.GREEN + "Giant Hand upgraded to " + getRomanNumerals(giantHandLevel) + "!");
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            updateGiantHandItem(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Not enough cookies to upgrade!");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        }
                        break;

                    case WHEAT:
                        player.sendMessage(ChatColor.AQUA + "Ranking: " + player.getName() + " " + allTimeCookies);
                        break;

                    default:
                        break;
                }
            }

            savePlayerData(player);
            updateRankingItem(player);
        }
    }

    private void updateCookieItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(13) != null && inventory.getItem(13).getType() == Material.COOKIE) {
            ItemStack cookieItem = createCookieItem();
            inventory.setItem(13, cookieItem);
        }
    }

    private void updateGiantHandItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getItem(27) != null && inventory.getItem(27).getType() == Material.GOLD_INGOT) {
            ItemStack giantHandItem = createGiantHandItem();
            inventory.setItem(27, giantHandItem);
        }
    }
}
