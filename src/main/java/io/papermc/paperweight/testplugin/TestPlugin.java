package io.papermc.paperweight.testplugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;

import io.papermc.paperweight.testplugin.commands.CastCommand;
import io.papermc.paperweight.testplugin.serialization.ItemStackSerializer;
import io.papermc.paperweight.testplugin.serialization.RecipeSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class TestPlugin extends JavaPlugin {
  public static Map<ResourceLocation, ItemStack> ITEMS = new HashMap<>();

  @Override
  public void onEnable() {
    MinecraftServer minecraftServer = ((CraftServer) getServer()).getServer();
    CommandDispatcher<CommandSourceStack> dispatcher = minecraftServer.getCommands().getDispatcher();
    CastCommand.register(dispatcher);

    this.getServer().addRecipe(((ShapedRecipe) RecipeSerializer.deserialize(JsonParser.parseString("""
      {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "group": "boat",
        "key": {
          "#": "minecraft:dirt"
        },
        "pattern": [
          "# #",
          "###"
        ],
        "result": {
          "components": {
            "minecraft:container": [
              {
                "slot": 0,
                "item": {
                  "id": "minecraft:acacia_trapdoor"
                }
              }
            ]
          },
          "replace": "hi",
          "count": 1,
          "id": "minecraft:acacia_boat"
        }
      }
      """)).toBukkitRecipe(NamespacedKey.fromString("test:test"))));

    File dataFolder = this.getDataFolder();
    if (!dataFolder.exists()) dataFolder.mkdirs();

    File[] namespaceFolders = dataFolder.listFiles(File::isDirectory);
    if (namespaceFolders == null) return;

    for (File namespaceFolder : namespaceFolders) {
      File itemsFolder = new File(namespaceFolder, "items");
      if (!itemsFolder.exists() || !itemsFolder.isDirectory()) continue;

      String namespace = namespaceFolder.getName();

      File[] itemFiles = itemsFolder.listFiles((dir, name) -> name.endsWith(".json"));
      if (itemFiles == null) continue;

      for (File itemFile : itemFiles) {
        String path = itemFile.getName().replace(".json", "");
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path);

        try (FileReader reader = new FileReader(itemFile)) {
          JsonElement json = JsonParser.parseReader(reader);
          ItemStack itemStack = ItemStackSerializer.deserialize(json);

          try {
            Item item = itemStack.getItem();
            ItemStack itemStack1 = new ItemStack(item);
            this.getLogger().info(Objects.requireNonNull(itemStack1.get(DataComponents.AXOLOTL_VARIANT)).toString());
          } catch (Exception exception) {
            this.getLogger().severe(exception.toString());
          }

          ITEMS.put(resourceLocation, itemStack);

          this.getLogger().info("Loaded item: '" + resourceLocation + "'");
        } catch (Exception exception) {
          this.getLogger().severe("Failed to load item: '" + resourceLocation + "'");
        }
      }
    }
  }
}
