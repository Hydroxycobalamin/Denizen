package com.denizenscript.denizen.scripts.containers.core;
import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.nms.util.jnbt.CompoundTag;
import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.events.bukkit.ScriptReloadEvent;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptBuilder;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.Deprecations;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class ItemScriptHelper implements Listener {

    public static final Map<String, ItemScriptContainer> item_scripts = new HashMap<>();
    public static final Map<String, ItemScriptContainer> item_scripts_by_hash_id = new HashMap<>();
    public static final Map<String, ItemScriptContainer> recipeIdToItemScript = new HashMap<>();

    public ItemScriptHelper() {
        Denizen.getInstance().getServer().getPluginManager()
                .registerEvents(this, Denizen.getInstance());
    }

    public static void removeDenizenRecipes() {
        recipeIdToItemScript.clear();
        NMSHandler.getItemHelper().clearDenizenRecipes();
    }

    public String getIdFor(ItemScriptContainer container, String type, int id) {
        String basicId = type + "_" + Utilities.cleanseNamespaceID(container.getName()) + "_" + id;
        if (!recipeIdToItemScript.containsKey(basicId)) {
            recipeIdToItemScript.put("denizen:" + basicId, container);
            return basicId;
        }
        int newNumber = 1;
        String newId = basicId + "_1";
        while (recipeIdToItemScript.containsKey(newId)) {
            newId = basicId + "_" + newNumber++;
        }
        recipeIdToItemScript.put("denizen:" + newId, container);
        return newId;
    }

    public static List<String> splitByNonBracketedSlashes(String str) {
        boolean brackets = false;
        int start = 0;
        List<String> output = new ArrayList<>(4);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[') {
                brackets = true;
            }
            else if (c == ']') {
                brackets = false;
            }
            else if (c == '/' && !brackets) {
                output.add(str.substring(start, i));
                start = i + 1;
            }
        }
        output.add(str.substring(start));
        return output;
    }

    public ItemStack[] textToItemArray(ItemScriptContainer container, String text, boolean exact) {
        if (CoreUtilities.toLowerCase(text).equals("air")) {
            return new ItemStack[0];
        }
        List<String> ingredientText = splitByNonBracketedSlashes(text);
        List<ItemStack> outputItems = new ArrayList<>(ingredientText.size());
        for (int i = 0; i < ingredientText.size(); i++) {
            String entry = ingredientText.get(i);
            if (ScriptEvent.isAdvancedMatchable(entry)) {
                boolean any = false;
                ScriptEvent.MatchHelper matcher = ScriptEvent.createMatcher(entry);
                for (Material material : Material.values()) {
                    if (matcher.doesMatch(CoreUtilities.toLowerCase(material.name()))) {
                        outputItems.add(new ItemStack(material, 1));
                        any = true;
                    }
                }
                if (exact) {
                    for (ItemScriptContainer possibleContainer : ItemScriptHelper.item_scripts.values()) {
                        if (matcher.doesMatch(CoreUtilities.toLowerCase(possibleContainer.getName()))) {
                            outputItems.add(possibleContainer.getCleanReference().getItemStack());
                            any = true;
                        }
                    }
                }
                if (!any) {
                    Debug.echoError("Invalid ItemTag ingredient (empty advanced matcher), recipe will not be registered for item script '" + container.getName() + "': " + entry);
                    return null;
                }
            }
            else {
                ItemTag ingredient = ItemTag.valueOf(entry, container);
                if (ingredient == null) {
                    Debug.echoError("Invalid ItemTag ingredient, recipe will not be registered for item script '" + container.getName() + "': " + entry);
                    return null;
                }
                outputItems.add(ingredient.getItemStack().clone());
            }
        }
        return outputItems.toArray(new ItemStack[0]);
    }

    public void registerShapedRecipe(ItemScriptContainer container, ItemStack item, List<String> recipeList, String internalId, String group) {
        for (int n = 0; n < recipeList.size(); n++) {
            recipeList.set(n, TagManager.tag(ScriptBuilder.stripLinePrefix(recipeList.get(n)), new BukkitTagContext(null, null, new ScriptTag(container))));
        }
        List<ItemStack[]> ingredients = new ArrayList<>();
        List<Boolean> exacts = new ArrayList<>();
        int width = 1;
        for (String recipeRow : recipeList) {
            String[] elements = recipeRow.split("\\|", 3);
            if (width < 3 && elements.length == 3) {
                width = 3;
            }
            if (width < 2 && elements.length >= 2) {
                width = 2;
            }
            for (String element : elements) {
                String itemText = element;
                boolean isExact = !itemText.startsWith("material:");
                if (!isExact) {
                    itemText = itemText.substring("material:".length());
                }
                exacts.add(isExact);
                ItemStack[] items = textToItemArray(container, itemText, isExact);
                if (items == null) {
                    return;
                }
                ingredients.add(items);
            }
        }
        NamespacedKey key = new NamespacedKey("denizen", internalId);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.setGroup(group);
        String shape1 = "ABC".substring(0, width);
        String shape2 = "DEF".substring(0, width);
        String shape3 = "GHI".substring(0, width);
        String itemChars = shape1 + shape2 + shape3;
        if (recipeList.size() == 3) {
            recipe = recipe.shape(shape1, shape2, shape3);
        }
        else if (recipeList.size() == 2) {
            recipe = recipe.shape(shape1, shape2);
        }
        else {
            recipe = recipe.shape(shape1);
        }
        for (int i = 0; i < ingredients.size(); i++) {
            if (ingredients.get(i).length != 0) {
                NMSHandler.getItemHelper().setShapedRecipeIngredient(recipe, itemChars.charAt(i), ingredients.get(i), exacts.get(i));
            }
        }
        Bukkit.addRecipe(recipe);
    }

    public void registerShapelessRecipe(ItemScriptContainer container, ItemStack item, String shapelessString, String internalId, String group) {
        TagContext context = new BukkitTagContext(null, null, new ScriptTag(container));
        String list = TagManager.tag(shapelessString, context);
        List<ItemStack[]> ingredients = new ArrayList<>();
        List<Boolean> exacts = new ArrayList<>();
        for (String element : ListTag.valueOf(list, context)) {
            String itemText = element;
            boolean isExact = !itemText.startsWith("material:");
            if (!isExact) {
                itemText = itemText.substring("material:".length());
            }
            exacts.add(isExact);
            ItemStack[] items = textToItemArray(container, itemText, isExact);
            if (items == null) {
                return;
            }
            ingredients.add(items);
        }
        boolean[] bools = new boolean[exacts.size()];
        for (int i = 0; i < exacts.size(); i++) {
            bools[i] = exacts.get(i);
        }
        NMSHandler.getItemHelper().registerShapelessRecipe(internalId, group, item, ingredients, bools);
    }

    public void registerFurnaceRecipe(ItemScriptContainer container, ItemStack item, String furnaceItemString, float exp, int time, String type, String internalId, String group) {
        boolean exact = true;
        if (furnaceItemString.startsWith("material:")) {
            exact = false;
            furnaceItemString = furnaceItemString.substring("material:".length());
        }
        ItemStack[] items = textToItemArray(container, furnaceItemString, exact);
        if (items == null) {
            return;
        }
        NMSHandler.getItemHelper().registerFurnaceRecipe(internalId, group, item, items, exp, time, type, exact);
    }

    public void registerStonecuttingRecipe(ItemScriptContainer container, ItemStack item, String inputItemString, String internalId, String group) {
        boolean exact = true;
        if (inputItemString.startsWith("material:")) {
            exact = false;
            inputItemString = inputItemString.substring("material:".length());
        }
        ItemStack[] items = textToItemArray(container, inputItemString, exact);
        if (items == null) {
            return;
        }
        NMSHandler.getItemHelper().registerStonecuttingRecipe(internalId, group, item, items, exact);
    }

    public void rebuildRecipes() {
        for (ItemScriptContainer container : item_scripts.values()) {
            try {
                if (container.contains("recipes")) {
                    YamlConfiguration section = container.getConfigurationSection("recipes");
                    int id = 0;
                    for (StringHolder key : section.getKeys(false)) {
                        id++;
                        YamlConfiguration subSection = section.getConfigurationSection(key.str);
                        String type = CoreUtilities.toLowerCase(subSection.getString("type"));
                        String internalId = subSection.contains("recipe_id") ? subSection.getString("recipe_id") : getIdFor(container, type + "_recipe", id);
                        String group = subSection.contains("group") ? subSection.getString("group") : "";
                        ItemStack item = container.getCleanReference().getItemStack().clone();
                        if (subSection.contains("output_quantity")) {
                            item.setAmount(Integer.parseInt(subSection.getString("output_quantity")));
                        }
                        if (type.equals("shaped")) {
                            registerShapedRecipe(container, item, subSection.getStringList("input"), internalId, group);
                        }
                        else if (type.equals("shapeless")) {
                            registerShapelessRecipe(container, item, subSection.getString("input"), internalId, group);
                        }
                        else if (type.equals("stonecutting")) {
                            registerStonecuttingRecipe(container, item, subSection.getString("input"), internalId, group);
                        }
                        else if (type.equals("furnace") || type.equals("blast") || type.equals("smoker") || type.equals("campfire")) {
                            float exp = 0;
                            int cookTime = 40;
                            if (subSection.contains("experience")) {
                                exp = Float.parseFloat(subSection.getString("experience"));
                            }
                            if (subSection.contains("cook_time")) {
                                cookTime = DurationTag.valueOf(subSection.getString("cook_time"), new BukkitTagContext(container)).getTicksAsInt();
                            }
                            registerFurnaceRecipe(container, item, subSection.getString("input"), exp, cookTime, type, internalId, group);
                        }
                    }
                }
                // Old script style
                if (container.contains("RECIPE")) {
                    Deprecations.oldRecipeScript.warn(container);
                    registerShapedRecipe(container, container.getCleanReference().getItemStack().clone(), container.getStringList("RECIPE"), getIdFor(container, "old_recipe", 0), "custom");
                }
                if (container.contains("SHAPELESS_RECIPE")) {
                    Deprecations.oldRecipeScript.warn(container);
                    registerShapelessRecipe(container, container.getCleanReference().getItemStack().clone(), container.getString("SHAPELESS_RECIPE"), getIdFor(container, "old_shapeless", 0), "custom");
                }
                if (container.contains("FURNACE_RECIPE")) {
                    Deprecations.oldRecipeScript.warn(container);
                    registerFurnaceRecipe(container, container.getCleanReference().getItemStack().clone(), container.getString("FURNACE_RECIPE"), 0, 40, "furnace", getIdFor(container, "old_furnace", 0), "custom");
                }
            }
            catch (Exception ex) {
                Debug.echoError("Error while rebuilding item script recipes for '" + container.getName() + "'...");
                Debug.echoError(ex);
            }
        }
    }

    @EventHandler
    public void scriptReload(ScriptReloadEvent event) {
        rebuildRecipes();
    }

    public static boolean isItemscript(ItemTag item) {
        return getItemScriptContainer(item) != null;
    }

    public static ItemScriptContainer getItemScriptContainer(ItemTag item) {
        if (item == null) {
            return null;
        }
        CompoundTag tag = NMSHandler.getItemHelper().getNbtData(item.getItemStack());
        String scriptName = tag.getString("DenizenItemScript");
        if (scriptName != null && !scriptName.equals("")) {
            return item_scripts.get(scriptName);
        }
        // TODO: Legacy hashed format
        String nbt = tag.getString("Denizen Item Script");
        if (nbt != null && !nbt.equals("")) {
            return item_scripts_by_hash_id.get(nbt);
        }
        return null;
    }

    public static String ItemScriptHashID = ChatColor.RED.toString() + ChatColor.BLUE + ChatColor.BLACK;

    public static String createItemScriptID(ItemScriptContainer container) {
        String colors = createItemScriptID(container.getName());
        container.setHashID(colors);
        return colors;
    }

    public static String createItemScriptID(String name) {
        String script = name.toUpperCase();
        StringBuilder colors = new StringBuilder();
        colors.append(ItemScriptHashID);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = script.getBytes(StandardCharsets.UTF_8);
            md.update(bytes, 0, bytes.length);
            String hash = new BigInteger(1, md.digest()).toString(16);
            for (int i = 0; i < 16; i++) {
                colors.append(ChatColor.COLOR_CHAR).append(hash.charAt(i));
            }
        }
        catch (Exception ex) {
            Debug.echoError(ex);
            colors.append(ChatColor.BLUE);
        }
        return colors.toString();
    }
}
