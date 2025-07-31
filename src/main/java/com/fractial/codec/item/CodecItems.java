package com.fractial.codec.item;

import com.fractial.codec.CodecPlugin;
import com.fractial.codec.serialization.Serializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

@SuppressWarnings("unused")
public class CodecItems {
  private static final String FILE_TYPE = ".json";

  public static HashMap<ResourceLocation, ItemStack> ITEM = new HashMap<>();

  private static ResourceLocation codecItemId(String name) {
    return ResourceLocation.fromNamespaceAndPath("codec", name);
  }

  public static void registerItem(ResourceLocation location, ItemStack stack) {
    CodecPlugin.getPlugin().getLogger().info("Added item: " + location);
    ITEM.put(location, stack);
  }

  public static void registerItem(ResourceLocation location, JsonElement json) throws ReflectiveOperationException {
    ItemStack stack = Serializer.deserialize(json, ItemStack.class);
    registerItem(location, stack);
  }

  public static void registerItem(String name, ItemStack stack) {
    registerItem(codecItemId(name), stack);
  }

  public static void registerItem(String name, JsonElement json) throws ReflectiveOperationException {
    registerItem(codecItemId(name), json);
  }

  public static void registerItemFromFile(String namespace, File file) {
    String path = file.getName().replace(FILE_TYPE, "");
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, path);

    try (FileReader reader = new FileReader(file)) {
      JsonElement json = JsonParser.parseReader(reader);
      registerItem(location, json);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void registerItemFromFolder(File parent, String child) {
    String namespace = parent.getName();
    File folder = new File(parent, child);

    File[] files = folder.listFiles((dir, name) -> name.endsWith(FILE_TYPE));
    if (files == null) return;

    for (File file : files) {
      registerItemFromFile(namespace, file);
    }
  }
}
