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

        // Basis-Weltname bereinigen (z. B. challenge_xyz_the_end → challenge_xyz)
        String baseName = fromName;
        if (baseName.endsWith("_nether")) {
            baseName = baseName.replace("_nether", "");
        } else if (baseName.endsWith("_the_end")) {
            baseName = baseName.replace("_the_end", "");
        }

        // Teleportlogik
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

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            // Nur überschreiben, wenn der Spieler AUS dem End kommt
            if (from.getEnvironment() == World.Environment.THE_END) {
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    Location returnLoc = overworld.getSpawnLocation().add(0, 1, 0);
                    event.setTo(returnLoc);
                    player.sendMessage("§7Du wurdest zurück in die Challenge-Welt teleportiert.");
                }
            }
        }
    }
}

