package com.fractial.codec;

import com.fractial.codec.commands.CastCommand;
import com.fractial.codec.event.EmojiListener;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class CodecPlugin extends JavaPlugin {
  private static CodecPlugin plugin;

  @Override
  public void onEnable() {
    plugin = this;

    MinecraftServer server = ((CraftServer) this.getServer()).getServer();
    CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
    CastCommand.register(dispatcher);

    ResourceManager resourceManager = server.getResourceManager();
    CodecResourceManager.reload(resourceManager);

    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(new EmojiListener(), this);

//    File dataFolder = getDataFolder();
//
//    if (!dataFolder.exists())  dataFolder.mkdirs();
//
//    File[] namespaceFolders = Objects.requireNonNull(dataFolder.listFiles(File::isDirectory));
//
//    for (File namespaceFolder : namespaceFolders) {
//      CodecItems.registerItemFromFolder(namespaceFolder, "items");
//    }
  }

  public static CodecPlugin getPlugin() {
    return plugin;
  }
}
