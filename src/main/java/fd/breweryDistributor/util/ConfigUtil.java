package fd.breweryDistributor.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class ConfigUtil {
    private File file;
    private FileConfiguration config;

    private static final Logger logger = Bukkit.getLogger();
    private static final Random random = new Random();
    public static ConfigUtil instance;
    public ConfigUtil(Plugin plugin, String path){
        this(plugin.getDataFolder().getAbsoluteFile() + "/" + path);
    }

    public ConfigUtil(String path){
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
        if(!verify())
        {
            logger.severe("Errors found in config, pls abort :/");
        };
        instance = this;
    }
    public boolean reload()
    {
        config = YamlConfiguration.loadConfiguration(file);
        return true;
    }

    public boolean save() {
        try {
        this.config.save(this.file);
        return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    //TODO: Needs Updating
    private boolean verify() {
        if (config.getStringList("brew-name-text").isEmpty())
        {
            logger.severe("Brew Name default text not found!");
            return false;
        }
        if (config.getStringList("brew-cooking-text").isEmpty())
        {
            logger.severe("Brew Cooking default text not found!");
            return false;
        }
        if (config.getStringList("brew-distil-text").isEmpty())
        {
            logger.severe("Brew Distillation default text not found!");
            return false;
        }
        if (config.getStringList("brew-wood-text").isEmpty())
        {
            logger.severe("Brew Wood default text not found!");
            return false;
        }
        if (config.getStringList("brew-age-text").isEmpty())
        {
            logger.severe("Brew Age default text not found!");
            return false;
        }
        if (config.getInt("obfuscate-chance") < 0)
        {
            logger.severe("Obfuscate chance less than 0");
            return false;
        }
        return true;
    }

    public File getFile() {
        return this.file;
    }

    public FileConfiguration getConfig(){
        return this.config;
    }

    //Getter functions
    public ConfigUtil get() { return instance;}
    public int getObfuscateChance()
    {
        return(config.getInt("obfuscate-chance"));
    }
    public int getGrassBreakChance()
    {
        return(config.getInt("grass-break-chance"));
    }
    public int getZombieDropChance()
    {
        return(config.getInt("zombie-drop-chance"));
    }
    public int getFishDropChance()
    {
        return(config.getInt("fish-drop-chance"));
    }
    public int getHideIngredientChance()
    {
        return(config.getInt("hide-ingredient-chance"));
    }
    public int getHideInstructionChance()
    {
        return(config.getInt("hide-instruction-chance"));
    }

    public String getRandomBrewNameText()
    {
        List<String> possible = config.getStringList("brew-name-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomBrewCookingText()
    {
        List<String> possible = config.getStringList("brew-cooking-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomBrewDistilText()
    {
        List<String> possible = config.getStringList("brew-distil-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomBrewWoodText()
    {
        List<String> possible = config.getStringList("brew-wood-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomBrewNeedsAgeText()
    {
        List<String> possible = config.getStringList("brew-age-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomZombieText()
    {
        List<String> possible = config.getStringList("zombie-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomBreakText()
    {
        List<String> possible = config.getStringList("break-text");
        return(possible.get(random.nextInt(possible.size())));
    }
    public String getRandomFishText()
    {
        List<String> possible = config.getStringList("fish-text");
        return(possible.get(random.nextInt(possible.size())));
    }
}
