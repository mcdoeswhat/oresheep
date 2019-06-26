package me.albert.oresheep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    private static Main instance;
    public static List<String> sheeps = new ArrayList<>();
    public static List<String> sheepnames = new ArrayList<>();
    CommandSender cs = Bukkit.getConsoleSender();
    @Override
    public void onEnable() {
        cs.sendMessage("§b[OreSheep] Loaded");
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(new OreSheepListener(), this);
        this.saveDefaultConfig();
        getCommand("oresheep").setTabCompleter(new OreSheepTabCompleter());
        getCommand("oresheep").setExecutor(new Commands());
        MetricsLite ml = new MetricsLite(this);
        reload();
    }

    public void reload() {
        new Config();
        reloadConfig();
        sheeps.clear();
        sheepnames.clear();
        for (String key : getConfig().getConfigurationSection("Sheep").getKeys(false)) {
            sheeps.add(ChatColor.translateAlternateColorCodes('&', key));
            sheepnames.add(ChatColor.translateAlternateColorCodes('&', getConfig().getString(
                    "Sheep." + key + ".name")));
        }

    }

    public static Main getInstance() {
        return instance;
    }
    @Override
    public void onDisable() {
        cs.sendMessage("§c[OreSheep] Disabled..");

    }


}

