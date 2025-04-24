package com.yoshey.listeners;

import com.yoshey.YosheysPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DragonKillListener implements Listener {

    private final YosheysPlugin plugin;

    public DragonKillListener(YosheysPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDragonKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        Bukkit.getLogger().info("[DEBUG] Entity gestorben: " + entity.getType());

        if (!(entity instanceof EnderDragon)) return;

        World world = entity.getWorld();
        String activeWorld = plugin.getConfig().getString("active-challenge-world");

        Bukkit.getLogger().info("[DEBUG] Drachen ist gestorben in Welt: " + world.getName());
        Bukkit.getLogger().info("[DEBUG] Gespeicherte Challenge-Welt: " + activeWorld);

        // Ist es wirklich die End-Dimension der aktiven Challenge-Welt?
        if (!world.getName().equals(activeWorld + "_the_end")) {
            Bukkit.getLogger().info("[DEBUG] Weltname stimmt nicht überein. Kein Timer-Stopp.");
            return;
        }

        // Timer stoppen
        plugin.getTimerManager().pauseTimer();
        Bukkit.getLogger().info("[DEBUG] Timer wurde gestoppt.");

        // Nachricht an alle Spieler in der End-Welt
        int seconds = plugin.getTimerManager().getSeconds();
        String timeString = plugin.getTimerManager().formatTime(seconds);
        for (Player player : world.getPlayers()) {
            player.sendMessage("§6§lChallenge abgeschlossen! Der Enderdrache wurde in " + timeString + " besiegt.");
        }

        Bukkit.getLogger().info("[YosheysPlugin] Enderdrache in " + world.getName() + " getötet – Challenge als abgeschlossen markiert.");
    }
}
