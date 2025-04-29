package com.yoshey.challenge;

import com.yoshey.YosheysPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ChallengeCommand implements CommandExecutor {

    private final YosheysPlugin plugin;

    public ChallengeCommand(YosheysPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen.");
            return true;
        }

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

            case "cleanup":
                if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
                    cleanupChallenges(player);
                } else {
                    player.sendMessage("§cAchtung, dieser Befehl löscht ALLE Challenge-Welten");
                    player.sendMessage("§e/challenge cleanup confirm §7zum bestätigen");
                }
                break;

            default:
                player.sendMessage("§cUnbekannter Sub-Befehl.");
        }

        return true;
    }

    private void startChallenge() {
        String worldName = "challenge-" + UUID.randomUUID().toString().substring(0, 6);

        World world = plugin.getWorldManager().createChallengeWorld(worldName);

        plugin.getConfig().set("active-challenge-world", world.getName());
        plugin.saveConfig();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(world.getSpawnLocation());
            p.setGameMode(GameMode.SURVIVAL);
            p.setInvulnerable(true);

            Bukkit.getScheduler().runTask(plugin, () -> {
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.setLevel(0);
                p.setExp(0f);
                p.setTotalExperience(0);
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
                p.setSaturation(5f);
                p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
                p.setFireTicks(0);
            });

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                p.setInvulnerable(false);
                p.sendMessage("§7Dein Teleport-Schutz ist nun abgelaufen.");
            }, 5 * 20L);
        }
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
                p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
                p.setFireTicks(0);


                plugin.getLogger().info("[DEBUG] Spieler " + p.getName() + " wurde zurück teleportiert in " + w.getName());

                Bukkit.getScheduler().runTask(plugin, () -> plugin.getInventoryManager().loadInventory(p));
            } else {
                p.sendMessage("§cKeine gespeicherte Position für dich gefunden. Du wirst zum Spawn teleportiert.");
                p.teleport(world.getSpawnLocation());

                Bukkit.getScheduler().runTask(plugin, () -> plugin.getInventoryManager().loadInventory(p));
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
        String baseWorldName = plugin.getConfig().getString("active-challenge-world");
        if (baseWorldName == null) {
            player.sendMessage("§cKeine aktive Challenge zu beenden.");
            return;
        }

        plugin.getTimerManager().pauseTimer();

        // Alle betroffenen Welten
        World overworld = Bukkit.getWorld(baseWorldName);
        World nether = Bukkit.getWorld(baseWorldName + "_nether");
        World theEnd = Bukkit.getWorld(baseWorldName + "_the_end");

        for (Player p : Bukkit.getOnlinePlayers()) {
            World pw = p.getWorld();
            if (pw != null && (pw.equals(overworld) || pw.equals(nether) || pw.equals(theEnd))) {
                plugin.getWorldManager().teleportToLobby(p);
            }
        }

        // Jetzt alle Welten löschen
        plugin.getWorldManager().deleteChallengeWorlds(baseWorldName);

        plugin.getConfig().set("active-challenge-world", null);
        plugin.getConfig().set("saved-positions", null);
        plugin.saveConfig();

        player.sendMessage("§cChallenge beendet und alle Welten gelöscht.");
    }


    private void cleanupChallenges(Player player) {
        File worldFolder = Bukkit.getWorldContainer(); // Das ist normalerweise der Server-Ordner
        int deletedWorlds = 0;

        for (File file : worldFolder.listFiles()) {
            if (file.isDirectory() && file.getName().startsWith("challenge-")) {
                String worldName = file.getName();

                // Falls die Welt noch geladen ist → zuerst entladen
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Bukkit.unloadWorld(world, false);
                }

                // Ordner löschen
                plugin.getWorldManager().deleteFolder(file);
                deletedWorlds++;
            }
        }

        player.sendMessage("§aEs wurden §e" + deletedWorlds + " §aChallenge-Welten gelöscht.");
        plugin.getLogger().info("[WorldManager] " + deletedWorlds + " Challenge-Welten gelöscht.");
    }
}
