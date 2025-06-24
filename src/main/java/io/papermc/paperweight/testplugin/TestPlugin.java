package io.papermc.paperweight.testplugin;

import com.mojang.brigadier.CommandDispatcher;

import io.papermc.paperweight.testplugin.commands.CastCommand;
import io.papermc.paperweight.testplugin.registry.ItemRegistry;
import io.papermc.paperweight.testplugin.registry.RecipeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class TestPlugin extends JavaPlugin {

  private static TestPlugin instance;

  public static System getPlugin() {
    return TestPlugin.getPlugin();
  }

  @Override
  public void onEnable() {
    instance = this;

    MinecraftServer minecraftServer = ((CraftServer) getServer()).getServer();
    CommandDispatcher<CommandSourceStack> dispatcher = minecraftServer.getCommands().getDispatcher();
    CastCommand.register(dispatcher);

    File dataFolder = this.getDataFolder();
    if (!dataFolder.exists()) dataFolder.mkdirs();

    File[] namespaceFolders = Objects.requireNonNull(dataFolder.listFiles(File::isDirectory));

    for (File namespaceFolder : namespaceFolders) {
      ItemRegistry.registerFromFolder(namespaceFolder, "items");
      RecipeRegistry.registerFromFolder(namespaceFolder, "recipe");
    }
  }

  public static TestPlugin getInstance() {
    return instance;
  }
}
