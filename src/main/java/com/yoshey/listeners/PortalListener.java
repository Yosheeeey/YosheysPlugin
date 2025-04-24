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
        String baseName = from.getName();

        // Challenge-Welt erkennen (z. B. challenge-abc_nether → challenge-abc)
        if (baseName.endsWith("_nether")) {
            baseName = baseName.replace("_nether", "");
        } else if (baseName.endsWith("_the_end")) {
            baseName = baseName.replace("_the_end", "");
        }

        Location target;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                // Overworld → Nether
                World nether = Bukkit.getWorld(baseName + "_nether");
                if (nether != null) {
                    target = nether.getSpawnLocation();
                    event.setTo(target);
                }
            } else if (from.getEnvironment() == World.Environment.NETHER) {
                // Nether → Overworld
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    target = overworld.getSpawnLocation();
                    event.setTo(target);
                }
            }
        }

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (from.getEnvironment() == World.Environment.NORMAL) {
                World end = Bukkit.getWorld(baseName + "_the_end");
                if (end != null) {
                    target = end.getSpawnLocation();
                    event.setTo(target);
                }
            }
        }
    }
}
