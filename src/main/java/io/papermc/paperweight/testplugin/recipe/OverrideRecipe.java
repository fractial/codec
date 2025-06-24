package io.papermc.paperweight.testplugin.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.papermc.paperweight.testplugin.TestPlugin;
import io.papermc.paperweight.testplugin.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverrideRecipe {

  public static class ShapedRecipe {

    public static org.bukkit.inventory.ShapedRecipe create(org.bukkit.inventory.ShapedRecipe shapedRecipe, JsonElement json) {

      NamespacedKey namespacedKey = shapedRecipe.getKey();
      ItemStack result = shapedRecipe.getResult();
      String[] shape = shapedRecipe.getShape();

      JsonObject jsonObject = json.getAsJsonObject();

      if (jsonObject.has("override_result") && jsonObject.get("override_result").isJsonObject()) {

        JsonObject overrideResult = jsonObject.getAsJsonObject("override_result");

        String id = overrideResult.has("id") && !overrideResult.get("id").isJsonNull()
          ? overrideResult.get("id").getAsString()
          : null;

        int count = overrideResult.has("count") && !overrideResult.get("count").isJsonNull()
          ? overrideResult.get("count").getAsInt()
          : 1;

        TestPlugin.getInstance().getLogger().info("ID: " + id);
        TestPlugin.getInstance().getLogger().info("COUNT: " + count);

        ResourceLocation resourceLocation = ResourceLocation.parse(id);
        result = ItemRegistry.ITEMS.get(resourceLocation).getBukkitStack();
      }

      org.bukkit.inventory.ShapedRecipe shapedRecipe1 = new org.bukkit.inventory.ShapedRecipe(namespacedKey, result);

      if (jsonObject.has("override_key") && jsonObject.get("override_key").isJsonObject()) {

        JsonObject overrideKey = jsonObject.getAsJsonObject("override_key");

        JsonArray patternJsonArray = jsonObject.getAsJsonArray("pattern");
        Gson gson = new Gson();

        List<String> oldShape = List.of(shapedRecipe.getShape());
        List<String> newShape = gson.fromJson(patternJsonArray, new TypeToken<List<String>>(){}.getType());

        TestPlugin.getInstance().getLogger().info("OLD: " + oldShape.toString());
        TestPlugin.getInstance().getLogger().info("NEW: " + newShape.toString());

        Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
        Map<Character, Character> overrideChoiceMap = new HashMap<>();

        for (int row = 0; row < oldShape.size(); row++) {
          String oldRow = oldShape.get(row);
          String newRow = newShape.get(row);

          for (int col = 0; col < oldRow.length(); col++) {
            char oldChar = oldRow.charAt(col);
            char newChar = newRow.charAt(col);
            overrideChoiceMap.put(oldChar, newChar);
          }
        }

        shapedRecipe1.shape(newShape.toArray(new String[0]));

        for (Map.Entry<Character, RecipeChoice> entry : choiceMap.entrySet()) {
          Character oldChar = entry.getKey();
          RecipeChoice choice = entry.getValue();

          Character newChar = overrideChoiceMap.get(oldChar);

          if (newChar == null) continue;

          if (overrideKey.has(newChar.toString())) {
            TestPlugin.getInstance().getLogger().info("OVERRIDE: " + newChar);

            String key = overrideKey.has(newChar.toString()) && !overrideKey.get(newChar.toString()).isJsonNull()
              ? overrideKey.get(newChar.toString()).getAsString()
              : null;

            ItemStack itemStack = ItemRegistry.ITEMS.get(ResourceLocation.parse(key)).getBukkitStack();


            shapedRecipe1.setIngredient(newChar, itemStack);

          } else {
            
            shapedRecipe1.setIngredient(newChar, choice);
          }
        }
      }

      return shapedRecipe1;
    }
  }

  public static class ShapelessRecipe {

    public static org.bukkit.inventory.ShapelessRecipe create(org.bukkit.inventory.ShapelessRecipe shapelessRecipe, JsonElement json) {
      return shapelessRecipe;
    }
  }
}
