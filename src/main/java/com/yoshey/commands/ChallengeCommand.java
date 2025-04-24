package com.yoshey.commands;

import com.yoshey.TimerManager;
import com.yoshey.YosheysPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChallengeCommand implements CommandExecutor {

    private final YosheysPlugin plugin;

    public ChallengeCommand(YosheysPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§eVerwende: /challenge start | resume | pause | end");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                startChallenge();
                break;

            case "resume":
                resumeChallenge(player);
                break;

            case "pause":
                pauseChallenge();
                break;

            case "end":
                endChallenge(player);
                break;

            default:
                player.sendMessage("§cUnbekannter Sub-Befehl.");
        }

        return true;
    }

    private void startChallenge() {
        // Neue Welt erstellen
        World world = plugin.getWorldManager().createChallengeWorld();

        // Weltname speichern
        plugin.getConfig().set("active-challenge-world", world.getName());
        plugin.saveConfig();

        // Alle Spieler teleportieren
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(world.getSpawnLocation());
            p.setGameMode(GameMode.SURVIVAL);
        }

        // Timer starten
        plugin.getTimerManager().resetTimer();
        plugin.getTimerManager().startTimer();
    }

    private void resumeChallenge(Player player) {
        String worldName = plugin.getConfig().getString("active-challenge-world");
        if (worldName == null || worldName.isEmpty()) {
            player.sendMessage("§cKeine gespeicherte Challenge gefunden.");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            String base = "saved-positions." + p.getName();

            if (plugin.getConfig().contains(base)) {
                String wName = plugin.getConfig().getString(base + ".world");
                double x = plugin.getConfig().getDouble(base + ".x");
                double y = plugin.getConfig().getDouble(base + ".y");
                double z = plugin.getConfig().getDouble(base + ".z");
                float yaw = (float) plugin.getConfig().getDouble(base + ".yaw");
                float pitch = (float) plugin.getConfig().getDouble(base + ".pitch");

                World w = Bukkit.getWorld(wName);
                if (w == null) {
                    w = Bukkit.createWorld(new WorldCreator(wName));
                }

                Location loc = new Location(w, x, y, z, yaw, pitch);
                p.teleport(loc);
                p.setGameMode(GameMode.SURVIVAL);

                plugin.getLogger().info("[DEBUG] Spieler " + p.getName() + " wurde zurück teleportiert in " + w.getName());

                // Inventar der Welt laden
                plugin.getInventoryManager().loadInventory(p);
            } else {
                p.sendMessage("§cKeine gespeicherte Position für dich gefunden. Du wirst zum Spawn teleportiert.");
                p.teleport(world.getSpawnLocation());
            }
        }

        plugin.getTimerManager().startTimer();
    }



    private void pauseChallenge() {
        plugin.getTimerManager().pauseTimer();

        String challengeWorldName = plugin.getConfig().getString("active-challenge-world");

        for (Player p : Bukkit.getOnlinePlayers()) {
            // Inventar der Challenge-Welt speichern
            plugin.getInventoryManager().saveInventory(p, challengeWorldName);
            plugin.getLogger().info("[DEBUG] Inventar gespeichert für " + p.getName() + " in Welt " + challengeWorldName);

            // Position speichern
            Location loc = p.getLocation();
            plugin.getConfig().set("saved-positions." + p.getName() + ".world", loc.getWorld().getName());
            plugin.getConfig().set("saved-positions." + p.getName() + ".x", loc.getX());
            plugin.getConfig().set("saved-positions." + p.getName() + ".y", loc.getY());
            plugin.getConfig().set("saved-positions." + p.getName() + ".z", loc.getZ());
            plugin.getConfig().set("saved-positions." + p.getName() + ".yaw", loc.getYaw());
            plugin.getConfig().set("saved-positions." + p.getName() + ".pitch", loc.getPitch());

            // Teleport zur Lobby
            plugin.getWorldManager().teleportToLobby(p);
        }

        plugin.saveConfig();
    }



    private void endChallenge(Player player) {
        String worldName = plugin.getConfig().getString("active-challenge-world");
        if (worldName == null) {
            player.sendMessage("§cKeine aktive Challenge zu beenden.");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        plugin.getTimerManager().pauseTimer();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(world)) {
                plugin.getWorldManager().teleportToLobby(p);
            }
        }

        if (world != null) {
            plugin.getWorldManager().deleteWorld(world);
        }

        plugin.getConfig().set("active-challenge-world", null);
        plugin.getConfig().set("saved-positions", null);
        plugin.saveConfig();

        player.sendMessage("§cChallenge beendet und Welt gelöscht.");
    }
}
