package com.yoshey.listeners;

import com.yoshey.YosheysPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalListener implements Listener {

    private final YosheysPlugin plugin;

    public PortalListener(YosheysPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom().getWorld();
        String fromName = from.getName();

        // Basis-Weltname bereinigen
        String baseName = fromName.replace("_nether", "").replace("_the_end", "");

        // Nether-Portale
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                World nether = Bukkit.getWorld(baseName + "_nether");
                if (nether != null) {
                    event.setTo(nether.getSpawnLocation().add(0, 1, 0));
                }
            } else if (from.getEnvironment() == World.Environment.NETHER) {
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    event.setTo(overworld.getSpawnLocation().add(0, 1, 0));
                }
            }
        }

        // End-Portale
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                // Spieler betritt das End → teleportiere auf manuell gesetzte Plattform
                World targetEnd = Bukkit.getWorld(baseName + "_the_end");
                if (targetEnd != null) {
                    Location platformSpawn = new Location(targetEnd, 100.5, 50, 0.5);
                    event.setTo(platformSpawn);
                }
            } else if (from.getEnvironment() == World.Environment.THE_END) {
                // Spieler verlässt das End → zurück in Challenge-Overworld
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    event.setTo(overworld.getSpawnLocation().add(0, 1, 0));
                }
            }
        }
    }

}
