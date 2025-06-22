package io.papermc.paperweight.testplugin.serialization;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

public class ItemStackSerializer {
  private static final Codec<ItemStack> ITEM_STACK_CODEC = ItemStack.CODEC;

  public static JsonElement serialize(ItemStack item) {
    DataResult<JsonElement> result = ITEM_STACK_CODEC.encodeStart(getOps(), item);
    return result.result().orElseThrow(() -> new IllegalStateException("Serialization failed: " + result.error().map(Object::toString).orElse("Unknown error")));
  }

  public static ItemStack deserialize(JsonElement json) {
    DataResult<ItemStack> result = ITEM_STACK_CODEC.parse(getOps(), json);
    return result.result().orElseThrow(() -> new IllegalStateException("Deserialization failed: " + result.error().map(Object::toString).orElse("Unknown error")));
  }

  protected static RegistryOps<JsonElement> getOps() {
    HolderLookup.Provider provider = MinecraftServer.getServer().registries().compositeAccess();
    return RegistryOps.create(JsonOps.INSTANCE, provider);
  }
}
