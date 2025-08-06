package com.fractial.codec;

import com.fractial.codec.item.CodecItems;
import com.fractial.codec.item.TemplateItemStack;
import com.fractial.codec.serialization.Serializer;
import com.fractial.codec.suggestion.CodecSuggestion;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;

public class CodecResourceManager {
  public static void reload(ResourceManager manager) {
    CodecItems.ITEM.clear();

    // TODO: Implement & remove placeholder for emoji
    CodecSuggestion.EMOJI.clear();
    CodecSuggestion.EMOJI.put(":gg:", "Good Game");
    CodecSuggestion.EMOJI.put(":hw:", "Hello, World!");

    Map<ResourceLocation, Resource> resources = manager.listResources("item", resourceLocation -> resourceLocation.getPath().endsWith(".json"));
    for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
      String namespace = entry.getKey().getNamespace().toLowerCase(Locale.ROOT);
      String path = entry.getKey().getPath().toLowerCase(Locale.ROOT).replace(".json", "");
      ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, path);
      try {
        JsonElement json = JsonParser.parseReader(new InputStreamReader(entry.getValue().open()));
        CodecItems.registerItem(location, json);
      } catch (ReflectiveOperationException | IOException e) {
        throw new RuntimeException(e);
      }
    }

    Map<ResourceLocation, Resource> resources1 = manager.listResources("extend", resourceLocation -> resourceLocation.getPath().endsWith(".json"));
    for (Map.Entry<ResourceLocation, Resource> entry : resources1.entrySet()) {
      String namespace = entry.getKey().getNamespace().toLowerCase(Locale.ROOT);
      String path = entry.getKey().getPath().toLowerCase(Locale.ROOT).replace(".json", "");
      ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, path);
      try {
        JsonElement json = JsonParser.parseReader(new InputStreamReader(entry.getValue().open()));
        ItemStack stack = Serializer.deserialize(json, TemplateItemStack.class).asItemStack();
        CodecItems.ITEM.put(location, stack);
      } catch (ReflectiveOperationException | IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
