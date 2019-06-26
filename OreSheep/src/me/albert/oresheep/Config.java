package me.albert.oresheep;

import org.bukkit.ChatColor;

public class Config {
    public String prefix = getConfigString("Messages.prefix");
    public String egg_give = getConfigString("Messages.egg_give");
    public String egg_get = getConfigString("Messages.egg_get");
    public String config_reload = getConfigString("Messages.config_reload");
    public String sheep_not_found = getConfigString("Messages.sheep_not_found");
    public String player_not_found = getConfigString("Messages.player_not_found");
    public String player_only = getConfigString("Messages.player_only");
    public String invalid_usage = getConfigString("Messages.invalid_usage");

    public String getConfigString(String key){
        key = Main.getInstance().getConfig().getString(key);
        return ChatColor.translateAlternateColorCodes('&',key);
    }


}
