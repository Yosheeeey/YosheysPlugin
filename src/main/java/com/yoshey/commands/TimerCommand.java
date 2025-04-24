package com.yoshey.commands;

import com.yoshey.TimerManager;
import com.yoshey.YosheysPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TimerCommand implements CommandExecutor {

    private final YosheysPlugin plugin;

    public TimerCommand(YosheysPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TimerManager timer = plugin.getTimerManager();

        if (args.length == 0) {
            sender.sendMessage("§eVerwende: /timer start | pause | reset");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                timer.startTimer();
                break;
            case "pause":
                timer.pauseTimer();
                break;
            case "reset":
                timer.resetTimer();
                break;
            default:
                sender.sendMessage("§cUnbekannter Sub-Befehl. Nutze: start, pause oder reset.");
                break;
        }

        return true;
    }
}
