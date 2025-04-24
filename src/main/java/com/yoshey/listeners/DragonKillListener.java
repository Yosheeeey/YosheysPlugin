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

        if (!(entity instanceof EnderDragon)) return;

        World world = entity.getWorld();
        String activeWorld = plugin.getConfig().getString("active-challenge-world");

        // Ist es wirklich die Challenge-Welt?
        if (!world.getName().equals(activeWorld)) return;

        // Timer stoppen
        plugin.getTimerManager().pauseTimer();

        // Info im Chat für alle Spieler in der Welt
        for (Player player : world.getPlayers()) {
            player.sendMessage("§6§lChallenge abgeschlossen! Der Enderdrache wurde besiegt.");
        }

        Bukkit.getLogger().info("[YosheysPlugin] Enderdrache in " + world.getName() + " getötet – Challenge beendet.");
    }
}
