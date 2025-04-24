package com.yoshey.world;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class WorldManager {

    private final String lobbyWorldName = "lobby";

    public World getLobbyWorld() {
        return Bukkit.getWorld(lobbyWorldName);
    }

    public void teleportToLobby(Player player) {
        World lobby = getLobbyWorld();
        if (lobby != null) {
            player.teleport(lobby.getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            player.sendMessage("§cDie Lobby-Welt konnte nicht gefunden werden.");
        }
    }

    public World createChallengeWorld(String name) {
        WorldCreator overworld = new WorldCreator(name);
        overworld.environment(World.Environment.NORMAL);
        World world = Bukkit.createWorld(overworld); // ← das wird zurückgegeben

        WorldCreator nether = new WorldCreator(name + "_nether");
        nether.environment(World.Environment.NETHER);
        Bukkit.createWorld(nether);

        WorldCreator theEndCreator = new WorldCreator(name + "_the_end");
        theEndCreator.environment(World.Environment.THE_END);
        World theEnd = Bukkit.createWorld(theEndCreator);

        Location center = new Location(theEnd, 0, 60, 0);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                theEnd.getBlockAt(center.clone().add(x, 0, z)).setType(Material.OBSIDIAN);
            }
        }
        theEnd.setSpawnLocation(center);


        Bukkit.getLogger().info("[WorldManager] Welt '" + name + "' mit Nether & End wurde erstellt.");

        return world; // ← Hier kommt der wichtige Fix!
    }



    public void deleteWorld(World world) {
        if (world == null) return;

        String name = world.getName();
        Bukkit.unloadWorld(world, false);

        File folder = new File(Bukkit.getWorldContainer(), name);
        deleteFolder(folder);
    }

    private void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                deleteFolder(file);
            }
        }
        folder.delete();
    }
}

