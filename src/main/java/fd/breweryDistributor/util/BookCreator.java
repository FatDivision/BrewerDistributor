package fd.breweryDistributor.util;

import com.dre.brewery.recipe.BRecipe;
import com.dre.brewery.recipe.RecipeItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.*;
import org.bukkit.Bukkit;
import fd.breweryDistributor.handlers.PassedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public class BookCreator {

    private static final Logger logger = Bukkit.getLogger();
    private static final Random random = new Random();
    private static final List<String> woodTypes = List.of("any", "Birch", "Oak", "Jungle", "Spruce", "Acacia", "Dark Oak", "Crimson", "Warped", "Mangrove", "Cherry", "Bamboo");;
    public static ItemStack createBookOfIngredients(BRecipe recipe, PassedEvent event) {
        ConfigUtil config = ConfigUtil.instance;
        List<RecipeItem> recipeItemList = recipe.getIngredients();
        List<String> pages = new ArrayList<>();

        String failText = "";
        String failIngText = "....";

        switch (event) {
            case COMMAND:
                failText = "Command Spawned";
                break;
            case FISH:
                failText = config.getRandomFishText();
                break;
            case BREAK:
                failText = config.getRandomBreakText();
                break;
            case ZOMBIE:
                failText = config.getRandomZombieText();
                break;
            default:
                logger.severe("Error occured in allocating fail text, bad Enum passed");
                break;
        }


        // Format the brew details
        String brewName = recipe.getDrinkTitle();
        String brewRecipeName = recipe.getRecipeName();
        String brewCookingTime = String.valueOf(recipe.getCookingTime());
        String brewDistillRuns = String.valueOf(recipe.getDistillRuns());
        String brewWood = woodTypes.get(recipe.getWood());

        brewCookingTime = brewCookingTime + (recipe.getCookingTime() > 1 ? " minutes" : " minute");
        brewDistillRuns = brewDistillRuns + (recipe.getDistillRuns() > 1 ? " times" : " time");

        String brewRecipeNameText = config.getRandomBrewNameText().replace("%brewRecipeName%", brewRecipeName);
        String brewCookingTimeText = config.getRandomBrewCookingText().replace("%brewCooking%",brewCookingTime);
        String brewDistillRunsText = config.getRandomBrewDistilText().replace("%brewDistillRuns%", brewDistillRuns);
        String brewWoodText = config.getRandomBrewWoodText().replace("%brewWoodType%", brewWood);

        int insHide = config.getHideInstructionChance();
        int ingHide = config.getHideIngredientChance();

        String brewDetailsPage = (random.nextInt(100) < insHide ? brewRecipeNameText : failText) + "\n"
                + (random.nextInt(100) < insHide ? brewCookingTimeText : failText) + "\n";

        if(recipe.needsDistilling())
            brewDetailsPage = brewDetailsPage + " " + (random.nextInt(100) < insHide ? brewDistillRunsText : failText) + "\n";
        if(recipe.getWood() > 0)
            brewDetailsPage = brewDetailsPage + " " + (random.nextInt(100) < insHide ? brewWoodText : failText) + "\n";
        if(recipe.needsToAge())
            brewDetailsPage = brewDetailsPage + " " + (random.nextInt(100) < insHide ? config.getRandomBrewNeedsAgeText() : failText) + "\n";


        // Concatenate all ingredients into a single page
        StringBuilder ingredientsPage = new StringBuilder();
        for (RecipeItem element : recipeItemList) {
            if(element.getMaterials() != null)
            {
                String ingredientDetails = (random.nextInt(100) < ingHide ? formatMaterialName(element.getMaterials().getFirst()) : failIngText)
                        + " x" + (random.nextInt(100) < ingHide ? element.getAmount() : failIngText );
                ingredientsPage.append(ingredientDetails).append("\n");
            }
        }

        // Add obfuscation to the ingredients page and add to pages
        if(brewDetailsPage.length() > 250)
        {
            String brewDetailsPage2 = brewDetailsPage.substring(250);
            brewDetailsPage = brewDetailsPage.substring(0, 250);
            pages.add(brewDetailsPage);
            pages.add(brewDetailsPage2);
            pages.add(obfuscate(ingredientsPage.toString()));
        }
        else {
            pages.add(brewDetailsPage);
            pages.add(obfuscate(ingredientsPage.toString()));
        }

        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("Torn Note");
        bookMeta.setAuthor("§kFD");
        bookMeta.setPages(pages);

        bookMeta.setGeneration(BookMeta.Generation.TATTERED);
        writtenBook.setItemMeta(bookMeta);

        return writtenBook;
    }

    private static String formatMaterialName(Material material) {
        // Convert the material name to lowercase and replace underscores with spaces
        String name = material.name().toLowerCase().replace('_', ' ');

        // Split the name into words, capitalize each word, and join them back together
        String[] words = name.split(" ");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        // Trim the trailing space and return the formatted name
        return formattedName.toString().trim();
    }

    private static String generateIngredientDescription(String ingredientName, int amount) {
        // Add a simple description that makes sense for a journal entry
        return amount + " " + ingredientName + (amount > 1 ? " pieces" : " piece");
    }

    private static String obfuscate(String inText) {
        StringBuilder obfuscatedText = new StringBuilder();
        boolean previousWasObfuscated = false;
        ConfigUtil config = ConfigUtil.instance;

        for (char c : inText.toCharArray()) {
            if (random.nextInt(100) < config.getObfuscateChance()) {
                if (!previousWasObfuscated) {
                    obfuscatedText.append("§k");
                    previousWasObfuscated = true;
                }
                obfuscatedText.append(c == '\n' ? c : "X");
            } else {
                if (previousWasObfuscated) {
                    obfuscatedText.append("§r");
                    previousWasObfuscated = false;
                }
                obfuscatedText.append(c);
            }
        }

        if (previousWasObfuscated) {
            obfuscatedText.append("§r");
        }

        return obfuscatedText.toString();
    }
}
