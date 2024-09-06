package fd.breweryDistributor;

import com.dre.brewery.recipe.BRecipe;
import fd.breweryDistributor.handlers.PassedEvent;
import fd.breweryDistributor.handlers.PlayerDiscovery;
import fd.breweryDistributor.util.BookCreator;
import fd.breweryDistributor.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ObjectInputFilter;
import java.util.Random;

public final class BreweryDistributor extends JavaPlugin {
    private final Random random = new Random();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        ConfigUtil cfg = new ConfigUtil(this, "config.yml");
        this.getCommand("fdbrew").setExecutor(this);
        try {
            // Attempt to retrieve and check BreweryX classes
            PluginManager pluginManager = Bukkit.getPluginManager();
            Plugin breweryPlugin = pluginManager.getPlugin("BreweryX");

            if (breweryPlugin == null) {
                getLogger().severe("BreweryX plugin not found!");
                return;
            }

            getLogger().info("BreweryX plugin found");

            // Register event listeners
            getServer().getPluginManager().registerEvents(new PlayerDiscovery(), this);

        } catch (Exception e) {
            getLogger().severe("Error accessing BreweryX classes: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if the command is /fdbrew
        if (command.getName().equalsIgnoreCase("fdbrew")) {
            // Make sure the player specified a subcommand
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /fdbrew <give|reload>");
                return true;
            }

            // Handle subcommands
            switch (args[0].toLowerCase()) {
                case "give":
                    // Check permission
                    if (sender instanceof Player && sender.hasPermission("fdbrew.give")) {
                        // Ensure the sender is a player
                        if (!(sender instanceof Player)) {
                            sender.sendMessage("Only players can use this command.");
                            return true;
                        }
                        Player player = (Player) sender;
                        // Call the custom function to give an item
                        ItemStack drop = BookCreator.createBookOfIngredients(BRecipe.getAllRecipes().get(random.nextInt(BRecipe.getAllRecipes().size() - 1)), PassedEvent.COMMAND);
                        player.getInventory().addItem(drop);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                        return true;
                    }

                case "reload":
                    // Check permission for reload command
                    if (sender.hasPermission("fdbrew.reload")) {
                        // Reload the config
                        reloadConfig();
                        ConfigUtil cfg = ConfigUtil.instance;
                        cfg.reload();
                        sender.sendMessage(ChatColor.GREEN + "The BreweryDistributor config has been reloaded.");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                        return true;
                    }

                default:
                    // If an invalid subcommand is provided
                    sender.sendMessage(ChatColor.RED + "Invalid subcommand. Use /fdbrew <give|reload>.");
                    return true;
            }
        }

        return false;
    }
}