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
import java.util.Arrays;

public class CookieClicker implements Listener {

    private final String chestGuiTitle = "Cookie Clicker";
    private int cookieCount = 0;
    private int allTimeCookies = 0;
    private int giantHandLevel = 1;
    private int cookiesPerClick = 1;
    private static final int COST_MULTIPLIER = 500;
    private File configFile;
    private FileConfiguration config;

    public CookieClicker() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
        loadCookieData();
        startCookieGenerationTask();
    }

    private void loadCookieData() {
        configFile = new File(MixfryPlugin.getInstance().getDataFolder(), "cookies.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        cookieCount = config.getInt("cookieCount", 0);
        allTimeCookies = config.getInt("allTimeCookies", 0);
        giantHandLevel = config.getInt("giantHandLevel", 1);
        cookiesPerClick = config.getInt("cookiesPerClick", 1);
    }

    private void saveCookieData() {
        config.set("cookieCount", cookieCount);
        config.set("allTimeCookies", allTimeCookies);
        config.set("giantHandLevel", giantHandLevel);
        config.set("cookiesPerClick", cookiesPerClick);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startCookieGenerationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveCookieData();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory().getTitle().equals(chestGuiTitle)) {
                        updateCookieItem(player);
                    }
                }
            }
        }.runTaskTimer(MixfryPlugin.getInstance(), 0, 600);
    }

    public void openCookieInventory(Player player) {
        player.openInventory(createCookieInventory());
    }

    private Inventory createCookieInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, chestGuiTitle);
        inventory.setItem(13, createCookieItem());
        inventory.setItem(27, createGiantHandItem());

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

    private void updateCookieItem(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();

        if (inventory.getItem(13) != null && inventory.getItem(13).getType() == Material.COOKIE) {
            ItemStack cookieItem = createCookieItem();
            inventory.setItem(13, cookieItem);
        }
        if (inventory.getItem(27) != null && inventory.getItem(27).getType() == Material.GOLD_INGOT) {
            ItemStack giantHandItem = createGiantHandItem();
            inventory.setItem(27, giantHandItem);
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

            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().getType() == Material.COOKIE) {
                    cookieCount += cookiesPerClick;
                    allTimeCookies += cookiesPerClick;
                    updateCookieItem(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
                } else if (event.getCurrentItem().getType() == Material.GOLD_INGOT) {
                    int giantHandCost = giantHandLevel * COST_MULTIPLIER;
                    if (cookieCount >= giantHandCost) {
                        cookieCount -= giantHandCost;
                        giantHandLevel++;
                        cookiesPerClick++;
                        updateCookieItem(player);
                        player.sendMessage(ChatColor.GREEN + "You have upgraded to Giant Hand Level " + giantHandLevel + "!");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    } else {
                        player.sendMessage(ChatColor.RED + "Not enough cookies!");
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    }
                }
            }
        }
    }
    public void onDisable() {
        saveCookieData();
    }
}
