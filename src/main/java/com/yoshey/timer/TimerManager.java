package com.yoshey.timer;

import com.yoshey.YosheysPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerManager {

    private final YosheysPlugin plugin;
    private int seconds = 0;
    private boolean running = false;
    private BukkitRunnable timerTask;
    private BukkitRunnable displayTask;

    public TimerManager(YosheysPlugin plugin) {
        this.plugin = plugin;
        startDisplayTask(); // Anzeige-Task direkt starten
    }

    public void startTimer() {
        if (running) return;

        running = true;
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                seconds++;
            }
        };
        timerTask.runTaskTimer(plugin, 0L, 20L); // jede Sekunde erhöhen
    }

    public void pauseTimer() {
        if (!running) return;

        running = false;
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    public void resetTimer() {
        pauseTimer();
        seconds = 0;
    }

    public void showTime() {
        String timeString = formatTime(seconds);
        String message = running ? "§1§b" + timeString
                : "§b§oPausiert ";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(message);
        }
    }


    public int getSeconds() {
        return seconds;
    }

    public boolean isRunning() {
        return running;
    }

    private void startDisplayTask() {
        displayTask = new BukkitRunnable() {
            @Override
            public void run() {
                showTime(); // wird alle 20 Ticks aktualisiert
            }
        };
        displayTask.runTaskTimer(plugin, 0L, 20L);
    }

    public String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
