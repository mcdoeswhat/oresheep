package me.albert.oresheep;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class Utils {

    private static Main main = Main.getInstance();
    private static int version;
    static {
        Integer value = Ints.tryParse(Bukkit.getServer().getClass().getPackage().getName().split("_")[1]);
        version = value != null ? value.intValue() : -1;
    }

    public static ItemStack createSpawnEgg(String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException{
        ItemStack is = new ItemStack(Material.MONSTER_EGG, 1);
        Object nmsStack = getCraftbukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, is);
        Object tag = (Boolean)nmsStack.getClass().getMethod("hasTag").invoke(nmsStack) ? nmsStack.getClass().getMethod("getTag").invoke(nmsStack) : getNmsClass("NBTTagCompound").newInstance();
        Object nested = getNmsClass("NBTTagCompound").newInstance();
        nested.getClass().getMethod("setString", String.class, String.class).invoke(nested, "id", type);
        tag.getClass().getMethod("set", String.class, getNmsClass("NBTBase")).invoke(tag, "EntityTag", nested);
        nmsStack.getClass().getMethod("setTag", getNmsClass("NBTTagCompound")).invoke(nmsStack, tag);
        ItemStack finalitem = (ItemStack) getCraftbukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", getNmsClass("ItemStack")).invoke(null, nmsStack);
        return finalitem;
    }

    private static Class<?> getNmsClass(String className) throws ClassNotFoundException {

        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + className);
    }

    private static Class<?> getCraftbukkitClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + className);
    }

    public static String color(String message){
        return ChatColor.translateAlternateColorCodes('&',message);

    }

    public static ItemStack getEgg(String sheep){
        ItemStack egg = null;
        if (version <= 12) {
            try {
                egg = createSpawnEgg("Sheep");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            egg = new ItemStack(Material.valueOf("SHEEP_SPAWN_EGG"));
        }
        ItemMeta meta = egg.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&',
                main.getConfig().getString("Sheep."+sheep+".name"));
        meta.setDisplayName(name);
        egg.setItemMeta(meta);
        return egg;
    }

    public static boolean isNumeric(String str) {
        try{
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
    public static String Sheepcfg(String name){
        String sheepcfg = null;
        for (String key : main.getConfig().getConfigurationSection("Sheep").getKeys(false)){
            if (color(main.getConfig().getString("Sheep."+key+".name"))
                            .equalsIgnoreCase(name)){
                sheepcfg = key;
                break;
            }
        }
        return  sheepcfg;

    }

    public static ItemStack dropOnShear(String key){
        ItemStack drop = new ItemStack(Material.AIR);
        String item = main.getConfig().getString("Sheep."+key+".drop-on-shear").toUpperCase();
        drop.setType(Material.getMaterial(item));
        String dropAmount = main.getConfig().getString("Sheep."+key+".drop-amount");
        if (dropAmount.contains("-")){
            int min = Integer.parseInt(dropAmount.split("-")[0]);
            int max = Integer.parseInt(dropAmount.split("-")[1]);
            Random random = new Random();
            int s = random.nextInt(max) % (max - min + 1) + min;
            drop.setAmount(s);
        } else {
            drop.setAmount(Integer.parseInt(dropAmount));
        }
        return drop;
    }

}
