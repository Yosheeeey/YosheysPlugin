package com.yoshey;

import com.yoshey.listeners.InventoryListener;
import com.yoshey.listeners.DragonKillListener;
import com.yoshey.listeners.LobbyProtectionListener;
import com.yoshey.listeners.PortalListener;
import com.yoshey.world.WorldManager;
import com.yoshey.commands.ChallengeCommand;
import com.yoshey.commands.TimerCommand;
import com.yoshey.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class YosheysPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("YosheysPlugin wurde aktiviert!");

        timerManager = new TimerManager(this);
        getCommand("timer").setExecutor(new TimerCommand(this));
        worldManager = new WorldManager();
        getCommand("challenge").setExecutor(new ChallengeCommand(this));
        inventoryManager = new InventoryManager(getDataFolder());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new LobbyProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonKillListener(this), this);
        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("YosheysPlugin wurde deaktiviert!");

        // Timer stoppen, wenn aktiv
        if (timerManager != null && timerManager.isRunning()) {
            timerManager.resetTimer();
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        World lobby = Bukkit.getWorld("lobby"); // oder plugin.getWorldManager().getLobbyWorld()
        if (lobby != null && player.getWorld().equals(lobby)) {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    private WorldManager worldManager;
    private TimerManager timerManager;
    private InventoryManager inventoryManager;

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

}

