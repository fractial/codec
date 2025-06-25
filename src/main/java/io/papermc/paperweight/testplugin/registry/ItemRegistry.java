package io.papermc.paperweight.testplugin.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.papermc.paperweight.testplugin.TestPlugin;
import io.papermc.paperweight.testplugin.serialization.ItemStackSerializer;
import io.papermc.paperweight.testplugin.serialization.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

  private static final String FILE_TYPE = ".json";
  public static Map<ResourceLocation, ItemStack> ITEMS = new HashMap<>();

  public static void register(ResourceLocation resourceLocation, JsonElement json) {
    ItemStack itemStack = Serializer.deserialize(json, ItemStack.class);

    TestPlugin.getInstance().getLogger().info("Added Item: " + resourceLocation);

    ITEMS.put(resourceLocation, itemStack);
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
