package me.albert.oresheep;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin implements Listener, TabCompleter {
    CommandSender cs = Bukkit.getConsoleSender();
    private YamlConfiguration config;
    private File file = new File(this.getDataFolder() , "config.yml");
    List<String> sheeps = new ArrayList<>();
    List<String> sheepnames = new ArrayList<>();
    BukkitScheduler server = Bukkit.getServer().getScheduler();
    static {
        Integer value = Ints.tryParse(Bukkit.getServer().getClass().getPackage().getName().split("_")[1]);
        version = value != null ? value.intValue() : -1;
    }

    private static int version;
    @Override
    public void onEnable(){
        cs.sendMessage("§b[OreSheep] Loaded");
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        this.saveDefaultConfig();
        this.config = YamlConfiguration.loadConfiguration(this.file);
        getCommand("oresheep").setTabCompleter(this::onTabComplete);
        for (String key : this.getConfig().getConfigurationSection("Sheep").getKeys(false)) {
            sheeps.add(ChatColor.translateAlternateColorCodes('&',key));
            sheepnames.add(ChatColor.translateAlternateColorCodes('&',this.getConfig().getString(
                    "Sheep."+ key+".name")));
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label , String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!new File(this.getDataFolder(), "config.yml").exists()) {
                    saveResource("config.yml", true);
                    sender.sendMessage("§b[OreSheep] config regenerated");
                } else
                    this.reloadConfig();
                sender.sendMessage("§b[OreSheep] config reloaded");
                sheeps.clear();
                sheepnames.clear();
                for (String key : this.getConfig().getConfigurationSection("Sheep").getKeys(false)) {
                    sheeps.add(ChatColor.translateAlternateColorCodes('&',key));
                    sheepnames.add(ChatColor.translateAlternateColorCodes('&',this.getConfig().getString(
                            "Sheep."+ key+".name")));
                }
            } else
            if (args[0].equalsIgnoreCase("get")) {
                if (sender instanceof Player) {
                    Player p = (Player)sender;
                    if (args.length == 2) {
                        if (sheeps.contains(args[1])) {
                            ItemStack egg = null;
                            if (version <= 12) {
                                try {
                                    egg = createSpawnEgg("Sheep");
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                egg = new ItemStack(Material.valueOf("SHEEP_SPAWN_EGG"));
                            }
                            ItemMeta meta = egg.getItemMeta();
                            String name = ChatColor.translateAlternateColorCodes('&',
                                    this.getConfig().getString("Sheep."+args[1]+".name"));
                            meta.setDisplayName(name);
                            egg.setItemMeta(meta);
                            p.getInventory().addItem(egg);
                            p.sendMessage("§b[OreSheep] §aYou received 1x "+name);
                        } else {
                            sender.sendMessage("§b[OreSheep] §cSheep not exist!");
                        }

                    } else {
                        sender.sendMessage(sheeps.toString());
                    }
                } else {
                    sender.sendMessage("§cPlayer only!");
                }

            } else {
                sender.sendMessage("§b[OreSheep] §cIncorrect usage");
            }
        } else{
            sender.sendMessage("§b/oresheep get <sheep>");
            sender.sendMessage("§b/oresheep reload");
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender cs, Command command, String args2, String[] args) {
        if(!(cs instanceof Player))return null;
        Player p = (Player)cs;

        if(!p.hasPermission("oresheep.use")) return null;
        if(args.length == 2){
            return sheeps;
        }
        return null;
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent e){
        Entity entity = e.getEntity();
        if (entity instanceof Sheep){
                Sheep sheep = (Sheep) e.getEntity();
                if (sheepnames.contains(sheep.getName())) {
                    e.setCancelled(true);
                    sheep.setSheared(true);
                    ItemStack itemonshear = dropOnShear(Sheepcfg(sheep.getName()));
                    e.getPlayer().getLocation().getWorld().playSound(e.getPlayer().getLocation(), Sounds.SHEEP_SHEAR.bukkitSound(),10,1);
                    e.getPlayer().getLocation().getWorld().dropItem(e.getEntity().getLocation(), itemonshear);
                }
            }
        }
        public String Sheepcfg(String name){
            String sheepcfg = null;
            for (String key : this.getConfig().getConfigurationSection("Sheep").getKeys(false)){
                if (
                        ChatColor.translateAlternateColorCodes
                                ('&',this.getConfig().getString("Sheep."+key+".name"))
                                .equalsIgnoreCase(name)){
                    sheepcfg = key;
                    break;
                }
            }
            return  sheepcfg;

        }

        public ItemStack dropOnShear(String key){
        ItemStack drop = new ItemStack(Material.AIR);
        String item = this.getConfig().getString("Sheep."+key+".drop-on-shear").toUpperCase();
        drop.setType(Material.getMaterial(item));
        String dropAmount = this.getConfig().getString("Sheep."+key+".drop-amount");
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

        @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        if (e.getEntityType()== EntityType.SHEEP){
            server.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    Sheep sheep = (Sheep) e.getEntity();
                    if (sheepnames.contains(sheep.getName())) {
                        String cfg = Sheepcfg(sheep.getName());
                        sheep.setColor(DyeColor.valueOf(getConfig().getString("Sheep." + cfg + ".color").toUpperCase()));
                    }

                }
            },0L);
        }

        }
        @EventHandler
        public void onDye(SheepDyeWoolEvent e){
        if (sheepnames.contains(e.getEntity().getName())){
            server.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    Sheep sheep = e.getEntity();
                    String cfg = Sheepcfg(sheep.getName());
                    sheep.setColor(DyeColor.valueOf(getConfig().getString("Sheep." + cfg + ".color").toUpperCase()));
                }
            },10L);

        }


        }

        @EventHandler
    public void onRename(PlayerInteractEntityEvent e){
        if (e.getPlayer().getItemInHand().getType() == Material.NAME_TAG
                && sheepnames.contains(e.getRightClicked().getName())){
            e.setCancelled(true);
            }

        }
    public ItemStack createSpawnEgg(String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException{
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

    private Class<?> getNmsClass(String className) throws ClassNotFoundException {

        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + className);
    }

    private Class<?> getCraftbukkitClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + className);
    }

    }

