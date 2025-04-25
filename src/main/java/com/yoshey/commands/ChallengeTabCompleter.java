package com.yoshey.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChallengeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("start", "resume", "pause", "end", "cleanup");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("cleanup")) {
            return Arrays.asList("confirm");
        }
        return new ArrayList<>();
    }
}
