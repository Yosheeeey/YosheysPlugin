package com.yoshey.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyProtectionListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && isInLobby(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && isInLobby(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isInLobby(event.getPlayer()) && event.getClickedBlock() != null) {
            Material type = event.getClickedBlock().getType();
            if (type.name().endsWith("_DOOR") || type.name().endsWith("TRAPDOOR")) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isInLobby(Player player) {
        World lobby = Bukkit.getWorld("lobby");
        return lobby != null && player.getWorld().equals(lobby);
    }
}

