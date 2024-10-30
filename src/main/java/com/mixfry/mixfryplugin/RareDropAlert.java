package com.mixfry.mixfryplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RareDropAlert implements Listener {

    private final Map<Material, Double> rareDrops = new HashMap<>();

    public RareDropAlert() {
        Bukkit.getPluginManager().registerEvents(this, MixfryPlugin.getInstance());
        initializeRareDrops();
    }

    private void initializeRareDrops() {
        rareDrops.put(Material.IRON_INGOT, 0.025); //Zombie 2.5%
        rareDrops.put(Material.CARROT, 0.025); //Zombie 2.5%
        rareDrops.put(Material.POTATO, 0.025); //Zombie 2.5%
        rareDrops.put(Material.WITHER_SKELETON_SKULL, 0.025); //Wither Skeleton 2.5%
        rareDrops.put(Material.RABBIT_FOOT, 0.1); //Rabbit 10%
        rareDrops.put(Material.BONE_MEAL, 0.05); //Fish 5%
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player && event.getEntityType() != EntityType.IRON_GOLEM) {
            Player player = event.getEntity().getKiller();
            for (ItemStack drop : event.getDrops()) {
                if (rareDrops.containsKey(drop.getType())) {
                    double probability = rareDrops.get(drop.getType()) * 100;
                    String itemName = drop.getType().name().replace("_", " ").toLowerCase();
                    String message = ChatColor.BLUE + "RARE DROP! " + ChatColor.GOLD + itemName + " " + ChatColor.AQUA + "(" + probability + "%)";
                    player.sendMessage(message);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }
}