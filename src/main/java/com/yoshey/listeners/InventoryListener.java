package com.yoshey.listeners;

import com.yoshey.YosheysPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InventoryListener implements Listener {

    private final YosheysPlugin plugin;

    public InventoryListener(YosheysPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String oldWorld = event.getFrom().getName();

        plugin.getInventoryManager().saveInventory(player, oldWorld); // speichert alte Welt
        plugin.getInventoryManager().loadInventory(player);           // lädt neue Welt
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getInventoryManager().loadInventory(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getInventoryManager().saveInventory(player);
    }


}

