package me.albert.oresheep;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OreSheepTabCompleter implements TabCompleter {
    private Main main = Main.getInstance();
    String[] subcommands = {"get","give"};
    @Override
    public List<String> onTabComplete(CommandSender cs, Command command, String args2, String[] args) {
        if(cs instanceof Player) {
            Player p = (Player) cs;
            if(!p.hasPermission("oresheep.use")) return null;
        }
        switch (args.length){
            case 1:
                return Arrays.asList(subcommands);
            case 2:
                if (args[0].equalsIgnoreCase("get")) {
                    return main.sheeps;
                } else {
                    return null;
                }
            case 3:
                if (args[0].equalsIgnoreCase("give")) {
                    return main.sheeps;
                }
        }
        return null;
    }
}
