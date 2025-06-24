package io.papermc.paperweight.testplugin.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paperweight.testplugin.TestPlugin;
import io.papermc.paperweight.testplugin.recipe.OverrideRecipe;
import io.papermc.paperweight.testplugin.serialization.RecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.*;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;

public class RecipeRegistry {

  private static final String FILE_TYPE = ".json";

  public static void register(ResourceLocation resourceLocation, JsonElement json) {
    Server server = TestPlugin.getInstance().getServer();
    NamespacedKey namespacedKey = NamespacedKey.fromString(resourceLocation.toString());
    Recipe recipe = RecipeSerializer.deserialize(json).toBukkitRecipe(namespacedKey);

    if (recipe instanceof ShapedRecipe shapedRecipe) {
      ShapedRecipe shapedRecipe1 = OverrideRecipe.ShapedRecipe.create(shapedRecipe, json);

      if (server.getRecipe(namespacedKey) != null) {
        server.removeRecipe(namespacedKey);
      }

      server.addRecipe(shapedRecipe1);

    } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
      TestPlugin.getInstance().getLogger().info("Register SHAPELESS");

    } else {
      TestPlugin.getInstance().getLogger().info("WHEN YOU REALLY KNOW, THAT YOU HAVE F*CKED UP");
    }
  }

  public static void registerFromFile(String namespace, File file) {
    String path = file.getName().replace(FILE_TYPE, "");
    ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path);

    try (FileReader fileReader = new FileReader(file)) {
      JsonElement json = JsonParser.parseReader(fileReader);
      register(resourceLocation, json);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void registerFromFolder(File namespaceFolder, String keyFolder) {
    String namespace = namespaceFolder.getName();
    File folder = new File(namespaceFolder, keyFolder);

    File[] itemFiles = folder.listFiles(((dir, name) -> name.endsWith(FILE_TYPE)));
    if (itemFiles == null) return;

    for (File itemFile : itemFiles) {
      registerFromFile(namespace, itemFile);
    }
  }
}
