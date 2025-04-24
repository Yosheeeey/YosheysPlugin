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

    public World createChallengeWorld() {
        String name = "challenge-" + UUID.randomUUID().toString().substring(0, 6);

        WorldCreator creator = new WorldCreator(name);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.NORMAL);

        return Bukkit.createWorld(creator); // neue Welt wird automatisch geladen
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

