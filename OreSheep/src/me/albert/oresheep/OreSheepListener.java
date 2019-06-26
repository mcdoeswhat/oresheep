package me.albert.oresheep;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class OreSheepListener implements Listener {
    private Main main = Main.getInstance();
    BukkitScheduler server = Bukkit.getServer().getScheduler();
    @EventHandler
    public void onShear(PlayerShearEntityEvent e){
        Entity entity = e.getEntity();
        if (entity instanceof Sheep){
            Sheep sheep = (Sheep) e.getEntity();
            if (main.sheepnames.contains(sheep.getName())) {
                e.setCancelled(true);
                sheep.setSheared(true);
                ItemStack itemonshear = Utils.dropOnShear( Utils.Sheepcfg(sheep.getName()));
                e.getPlayer().getLocation().getWorld().playSound(e.getPlayer().getLocation(), Sounds.SHEEP_SHEAR.bukkitSound(),10,1);
                e.getPlayer().getLocation().getWorld().dropItem(e.getEntity().getLocation(), itemonshear);
            }
        }
    }
    @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        if (e.getEntityType()== EntityType.SHEEP){
            server.scheduleSyncDelayedTask(main, () -> {
                Sheep sheep = (Sheep) e.getEntity();
                if (main.sheepnames.contains(sheep.getName())) {
                    String cfg =  Utils.Sheepcfg(sheep.getName());
                    sheep.setColor(DyeColor.valueOf(main.getConfig().getString("Sheep." + cfg + ".color").toUpperCase()));
                }

            },0L);
        }

    }
    @EventHandler
    public void onDye(SheepDyeWoolEvent e){
        if (main.sheepnames.contains(e.getEntity().getName())){
            server.scheduleSyncDelayedTask(main, () -> {
                Sheep sheep = e.getEntity();
                String cfg = Utils.Sheepcfg(sheep.getName());
                sheep.setColor(DyeColor.valueOf(main.getConfig().getString("Sheep." + cfg + ".color").toUpperCase()));
            },10L);

        }


    }

    @EventHandler
    public void onRename(PlayerInteractEntityEvent e){
        if (e.getPlayer().getItemInHand().getType() == Material.NAME_TAG
                && main.sheepnames.contains(e.getRightClicked().getName())){
            e.setCancelled(true);
        }

    }
}
