package me.albert.oresheep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;


public class Commands implements CommandExecutor {
    private static Main main = Main.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Config config = new Config();
        switch (args.length){
            case 0:
                sender.sendMessage(Utils.color("&b/oresheep get <sheep>"));
                sender.sendMessage(Utils.color("&b/oresheep give <player> <sheep> [amount]"));
                return true;
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!new File(main.getDataFolder(), "config.yml").exists()) {
                        main.saveResource("config.yml", true);
                        sender.sendMessage(config.prefix+config.config_reload);
                        return true;
                    } else main.reload();
                    sender.sendMessage(config.prefix+config.config_reload);
                    return true;
                }
                if (args[0].equalsIgnoreCase("give")) {
                    sender.sendMessage(Utils.color("&a/oresheep give <player> <sheep> [amount]"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("get")) {
                    sender.sendMessage(Utils.color("&a/oresheep get <sheep>"));
                    return true;
                } else {
                    sender.sendMessage(config.prefix+config.invalid_usage);
                    return true;
                }
            case 2:
                if (args[0].equalsIgnoreCase("get") && sender instanceof Player) {
                    Player p = (Player)sender;
                    if (main.sheeps.contains(args[1])) {
                        String name = Utils.color(main.getConfig().getString("Sheep." + args[1] + ".name"));
                        p.getInventory().addItem(Utils.getEgg(args[1]));
                        String message = config.egg_give.replace("%amount%","1").replace("%sheep%",name)
                                .replace("%player%",sender.getName());
                        p.sendMessage(config.prefix+message);
                        return true;
                    } else {
                        sender.sendMessage(config.prefix+config.sheep_not_found);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("give")) {
                    sender.sendMessage(Utils.color("&a/oresheep give <player> <sheep> [amount]"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("get") &&!(sender instanceof Player)){
                    sender.sendMessage(config.prefix+config.player_only);
                    return true;
                }
                sender.sendMessage(config.prefix+config.invalid_usage);
                return true;
            case 3:
                if (args[0].equalsIgnoreCase("give")) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(config.prefix+config.player_not_found);
                        return true;
                    }
                    Player p = Bukkit.getPlayer(args[1]);
                    if (main.sheeps.contains(args[2])) {
                        int amount = 1;
                        GiveSheep(sender, args, p, amount);
                        return true;
                    } else {
                        sender.sendMessage(config.prefix + config.sheep_not_found);
                        return true;
                    }

                }
            case 4:
                if (args[0].equalsIgnoreCase("give")) {
                    if (Bukkit.getPlayer(args[1]) == null) {
                        sender.sendMessage(config.prefix+config.player_not_found);
                        return true;
                    }
                    Player p = Bukkit.getPlayer(args[1]);
                    if (main.sheeps.contains(args[2])) {
                        int amount = Utils.isNumeric(args[3]) ? Integer.parseInt(args[3]): 1;
                        GiveSheep(sender, args, p, amount);
                        return true;
                    } else {
                        sender.sendMessage(config.prefix + config.sheep_not_found);
                        return true;
                    }

                }
                sender.sendMessage(config.prefix+config.invalid_usage);
                return true;
            default:
                sender.sendMessage(config.prefix+config.invalid_usage);
                break;

        }
        return true;
    }

    private void GiveSheep(CommandSender sender, String[] args, Player p, int amount) {
        Config config = new Config();
        String name =Utils.color(main.getConfig().getString("Sheep." + args[2] + ".name"));
        ItemStack egg = Utils.getEgg(args[2]);
        egg.setAmount(amount);
        p.getInventory().addItem(egg);
        String message = config.egg_get.replace("%amount%", String.valueOf(amount)).replace("%sheep%", name);
        String message_sender = config.egg_give.replace("%amount%", String.valueOf(amount)).replace("%sheep%", name)
                .replace("%player%", p.getName());
        p.sendMessage(config.prefix + message);
        sender.sendMessage(config.prefix + message_sender);
    }
}
