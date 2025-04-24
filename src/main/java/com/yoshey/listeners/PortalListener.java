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
        String baseName = fromName.replace("_nether", "").replace("_the_end", "");

        // 🔁 Nether-Portale (optional)
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                World nether = Bukkit.getWorld(baseName + "_nether");
                if (nether != null) {
                    event.setTo(nether.getSpawnLocation().add(0, 1, 0));
                    player.sendMessage("§7Du betrittst die Nether-Dimension deiner Challenge.");
                }
            } else if (from.getEnvironment() == World.Environment.NETHER) {
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    event.setTo(overworld.getSpawnLocation().add(0, 1, 0));
                    player.sendMessage("§7Du kehrst zurück aus dem Nether.");
                }
            }
        }

        // 🌀 End-Portale
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                // Spieler will INS End
                World targetEnd = Bukkit.getWorld(baseName + "_the_end");
                if (targetEnd != null) {
                    Location safeEndSpawn = new Location(targetEnd, 0.5, 62, 0.5);
                    event.setTo(safeEndSpawn);
                    player.sendMessage("§7Du betrittst die End-Dimension deiner Challenge.");
                }
            } else if (from.getEnvironment() == World.Environment.THE_END) {
                // Spieler verlässt das End
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    event.setTo(overworld.getSpawnLocation().add(0, 1, 0));
                    player.sendMessage("§7Du wurdest zurück in die Challenge-Welt teleportiert.");
                }
            }
        }
    }
}
