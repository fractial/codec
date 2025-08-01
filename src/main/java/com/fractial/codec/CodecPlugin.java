package com.fractial.codec;

import com.fractial.codec.commands.CastCommand;
import com.fractial.codec.item.CodecItems;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.RecipeManager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.File;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class CodecPlugin extends JavaPlugin implements Listener {
  private static CodecPlugin plugin;

  @Override
  public void onEnable() {
    plugin = this;

    MinecraftServer server = ((CraftServer) getServer()).getServer();
    CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
    CastCommand.register(dispatcher);

    ResourceManager manager = server.getResourceManager();
    CodecResourceManager.reload(manager);

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
