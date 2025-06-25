package io.papermc.paperweight.testplugin.serialization;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.papermc.paperweight.testplugin.recipe.OverrideRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;
import java.util.Objects;

public class Serializer {

  @SuppressWarnings("unchecked")
  public static <T> JsonElement serialize(T t) {
    try {
      Field codecField = t.getClass().getField("CODEC");
      Codec<T> codec = (Codec<T>) codecField.get(null);
      DataResult<JsonElement> result = codec.encodeStart(getOps(), t);
      return result.result().orElseThrow(() -> new IllegalStateException("Serialization failed: " + result.error().map(Objects::toString).orElse("Unknown error")));
    } catch (Exception e) {
      throw new RuntimeException("Could not find CODEC field on class " + t.getClass(), e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserialize(JsonElement json, Class<T> clazz) {
    try {
      Field codecField = clazz.getField("CODEC");
      Codec<T> codec = (Codec<T>) codecField.get(null);
      DataResult<T> result = codec.parse(getOps(), json);
      return result.result().orElseThrow(() -> new IllegalStateException("Deserialization failed: " + result.error().map(Objects::toString).orElse("Unknown error")));
    } catch (Exception e) {
      throw new RuntimeException("Could not deserialize with CODEC from " + clazz, e);
    }
  }

  protected static RegistryOps<JsonElement> getOps() {
    HolderLookup.Provider provider = MinecraftServer.getServer().registries().compositeAccess();
    return RegistryOps.create(JsonOps.INSTANCE, provider);
  }
}
