package fd.breweryDistributor.handlers;

import fd.breweryDistributor.util.BookCreator;
import fd.breweryDistributor.util.ConfigUtil;

import fd.breweryDistributor.util.FdUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import org.bukkit.inventory.ItemStack;
import com.dre.brewery.recipe.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

;



public class PlayerDiscovery implements Listener {

    private final Random random = new Random();
    private final Map<UUID, Boolean> zombieFromSpawner = new HashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Player p = event.getPlayer();
        Material blockType = b.getType();


        if (blockType == Material.TALL_GRASS || blockType ==  Material.SHORT_GRASS || blockType == Material.SNOW) {
            ConfigUtil config = ConfigUtil.instance;
            ItemStack drop = BookCreator.createBookOfIngredients(FdUtils.GetRandomRecipe(), PassedEvent.BREAK);

            int chance = random.nextInt(config.getGrassBreakChance());

            if (chance < 10) {
                b.getWorld().dropItemNaturally(b.getLocation(), drop);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            // Store whether the zombie was spawned by a mob spawner
            boolean fromSpawner = event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER;
            zombieFromSpawner.put(zombie.getUniqueId(), fromSpawner);
        }
    }

//    //TODO: More efficient handling, apparently this is depreciated
//    @EventHandler
//    public void onEntityRemove(EntityRemoveEvent event) {
//        if (event.getEntity() instanceof Zombie) {
//            Zombie zombie = (Zombie) event.getEntity();
//            UUID zombieId = zombie.getUniqueId();
//            // Remove the zombie from the map
//            zombieFromSpawner.remove(zombieId);
//        }
//    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            UUID zombieId = zombie.getUniqueId();

            // Check if we have information about this zombie's spawn reason
            if (zombieFromSpawner.containsKey(zombieId)) {
                boolean wasSpawnedBySpawner = zombieFromSpawner.get(zombieId);

                // Only drop the item if the zombie wasn't spawned by a spawner
                if (!wasSpawnedBySpawner) {
                    ConfigUtil config = ConfigUtil.instance;
                    Random random = new Random();
                    int chance = config.getZombieDropChance();

                    if (random.nextInt(10000) < chance) {
                        ItemStack customDrop = BookCreator.createBookOfIngredients(FdUtils.GetRandomRecipe(), PassedEvent.ZOMBIE);
                        event.getDrops().add(customDrop);
                    }
                }

                // Clean up the map after the zombie dies
                zombieFromSpawner.remove(zombieId);
            }
        }

    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        // Check if the event is related to catching a fish or other item
        if (event.getState() == State.CAUGHT_FISH) {
            // Determine the chance of hooking a special item
            Random random = new Random();
            ConfigUtil config = ConfigUtil.instance;
            int chance = config.getFishDropChance();

            if (random.nextInt(100) < chance) {
                ItemStack drop = BookCreator.createBookOfIngredients(FdUtils.GetRandomRecipe(), PassedEvent.FISH);

                // Add the special item as a fishing drop
                event.getCaught().getWorld().dropItemNaturally(event.getCaught().getLocation(), drop);
                Player player = event.getPlayer();

                // Send a colored message (gold/yellow)
                player.sendMessage(ChatColor.GOLD + "The rod seems to have hooked something...");

                // Play the level-up sound at the player's location
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
            }
        }
    }
}
