package me.albert.oresheep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    private static Main instance;
    private static Config config;
    CommandSender cs = Bukkit.getConsoleSender();
    List<String> sheeps = new ArrayList<>();
    List<String> sheepnames = new ArrayList<>();

    @Override
    public void onEnable() {
        cs.sendMessage("§b[OreSheep] Loaded");
        instance = this;
        this.config = new Config();
        Bukkit.getServer().getPluginManager().registerEvents(new OreSheepListener(), this);
        this.saveDefaultConfig();
        getCommand("oresheep").setTabCompleter(new OreSheepTabCompleter());
        MetricsLite ml = new MetricsLite(this);
        this.reload();
    }

    public static Main getInstance() {
        return instance;
    }

    public void reload() {
        this.reloadConfig();
        sheeps.clear();
        sheepnames.clear();
        for (String key : this.getConfig().getConfigurationSection("Sheep").getKeys(false)) {
            sheeps.add(ChatColor.translateAlternateColorCodes('&', key));
            sheepnames.add(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(
                    "Sheep." + key + ".name")));
        }

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args.length){
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!new File(this.getDataFolder(), "config.yml").exists()) {
                        saveResource("config.yml", true);
                        sender.sendMessage(config.prefix+config.config_reload);
                        return true;
                    } else this.reload();
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
                    if (sheeps.contains(args[1])) {
                        String name = ChatColor.translateAlternateColorCodes('&',
                                this.getConfig().getString("Sheep." + args[1] + ".name"));
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
                    if (sheeps.contains(args[2])) {
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
                    if (sheeps.contains(args[2])) {
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
        String name =Utils.color(this.getConfig().getString("Sheep." + args[2] + ".name"));
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

