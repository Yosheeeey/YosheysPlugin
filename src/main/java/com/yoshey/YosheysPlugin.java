package com.yoshey;

import com.yoshey.challenge.ChallengeCommand;
import com.yoshey.challenge.ChallengeTabCompleter;
import com.yoshey.inventory.InventoryManager;
import com.yoshey.listeners.DragonKillListener;
import com.yoshey.listeners.InventoryListener;
import com.yoshey.listeners.LobbyProtectionListener;
import com.yoshey.listeners.PortalListener;
import com.yoshey.timer.TimerCommand;
import com.yoshey.timer.TimerManager;
import com.yoshey.timer.TimerTabCompleter;
import com.yoshey.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class YosheysPlugin extends JavaPlugin implements Listener {

    private WorldManager worldManager;
    private TimerManager timerManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        getLogger().info("YosheysPlugin wurde aktiviert!");

        initManagers();
        initCommands();
        initListeners();
    }

    @Override
    public void onDisable() {
        getLogger().info("YosheysPlugin wurde deaktiviert!");

        if (timerManager != null && timerManager.isRunning()) {
            timerManager.resetTimer();
        }
    }

    private void initManagers() {
        timerManager = new TimerManager(this);
        worldManager = new WorldManager();
        inventoryManager = new InventoryManager(getDataFolder());
    }

    private void initCommands() {
        getCommand("timer").setExecutor(new TimerCommand(this));
        getCommand("timer").setTabCompleter(new TimerTabCompleter());

        getCommand("challenge").setExecutor(new ChallengeCommand(this));
        getCommand("challenge").setTabCompleter(new ChallengeTabCompleter());
    }

    private void initListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new LobbyProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonKillListener(this), this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String activeChallengeWorld = getConfig().getString("active-challenge-world");

        World lobby = Bukkit.getWorld("lobby");

        if (activeChallengeWorld != null && !activeChallengeWorld.isEmpty()) {
            // Es läuft eine Challenge
            World challengeWorld = Bukkit.getWorld(activeChallengeWorld);

            if (challengeWorld != null) {
                player.teleport(challengeWorld.getSpawnLocation());
                player.setGameMode(GameMode.SURVIVAL);

                // Schutz aktivieren
                player.setInvulnerable(true);

                // Inventar und Status zurücksetzen
                Bukkit.getScheduler().runTask(this, () -> {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setLevel(0);
                    player.setExp(0f);
                    player.setTotalExperience(0);
                    player.setHealth(player.getMaxHealth());
                    player.setFoodLevel(20);
                    player.setSaturation(5f);
                });

                // Schutz nach 5 Sekunden deaktivieren
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    player.setInvulnerable(false);
                    player.sendMessage("§7Dein Teleport-Schutz ist nun abgelaufen.");
                }, 5 * 20L); // 5 Sekunden (Minecraft rechnet mit 20 Ticks = 1 Sekunde)

                player.sendMessage("§aDu bist einer laufenden Challenge beigetreten!");
                getLogger().info("[YosheysPlugin] Spieler " + player.getName() + " ist einer aktiven Challenge beigetreten.");
            } else {
                player.sendMessage("§cAktive Challenge-Welt nicht gefunden.");
            }
        } else if (lobby != null) {
            // Normale Lobby
            if (player.getWorld().equals(lobby)) {
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
    }

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
