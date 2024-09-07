package fd.breweryDistributor.util;

import com.dre.brewery.recipe.BRecipe;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class FdUtils {
    private final static Random random = new Random();
    private static final Logger logger = Bukkit.getLogger();

    public static BRecipe GetRandomRecipe()
    {
        BRecipe recipe = BRecipe.getAllRecipes().get(random.nextInt(BRecipe.getAllRecipes().size() - 1));
        ConfigUtil cfg = ConfigUtil.instance;
        List<String> bl = cfg.getConfig().getStringList("blacklist");
        if (bl.contains(recipe.getRecipeName()))
        {
            logger.info("test");
            return GetRandomRecipe();
        }
        return recipe;
    }
}
