package com.yoshey.listeners;

import com.yoshey.YosheysPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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

        // Hole aktive Challenge-Welt
        String activeChallenge = plugin.getConfig().getString("active-challenge-world");

        // NUR wenn es eine aktive Challenge gibt und wir uns auch in dieser Challenge bewegen
        boolean isChallengeActive = activeChallenge != null && !activeChallenge.isEmpty();
        boolean isInChallengeWorld = baseName.equalsIgnoreCase(activeChallenge);

        if (!isChallengeActive || !isInChallengeWorld) {
            // Challenge beendet oder Spieler in keiner Challenge-Welt → NICHTS ändern
            return;
        }

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
                // Spieler betritt das End
                World end = Bukkit.getWorld(baseName + "_the_end");
                if (end != null) {
                    Location spawn = new Location(end, 100.5, 50, 0.5);
                    buildEndSpawnPlatform(end, 100, 49, 0);
                    event.setTo(spawn);
                }
            } else if (from.getEnvironment() == World.Environment.THE_END) {
                // Spieler verlässt das End zurück in die Challenge-Overworld
                World overworld = Bukkit.getWorld(baseName);
                if (overworld != null) {
                    event.setTo(overworld.getSpawnLocation().add(0, 1, 0));
                }
            }
        }
    }

    private void buildEndSpawnPlatform(World world, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                Block block = world.getBlockAt(x + dx, y, z + dz);
                if (block.getType() == Material.AIR) {
                    block.setType(Material.OBSIDIAN);
                }
            }
        }
    }
}
