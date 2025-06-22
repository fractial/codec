package io.papermc.paperweight.testplugin.serialization;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeSerializer {
  private static final Codec<Recipe<?>> RECIPE_CODEC = Recipe.CODEC;

  public static JsonElement serialize(Recipe<?> recipe) {
    DataResult<JsonElement> result = RECIPE_CODEC.encodeStart(getOps(), recipe);
    return result.result().orElseThrow(() -> new IllegalStateException("Serialization failed: " + result.error().map(Object::toString).orElse("Unknown error")));
  }

  public static Recipe<?> deserialize(JsonElement json) {
    DataResult<Recipe<?>> result = RECIPE_CODEC.parse(getOps(), json);
    return result.result().orElseThrow(() -> new IllegalStateException("Deserialization failed: " + result.error().map(Object::toString).orElse("Unknown error")));
  }

  protected static RegistryOps<JsonElement> getOps() {
    HolderLookup.Provider provider = MinecraftServer.getServer().registries().compositeAccess();
    return RegistryOps.create(JsonOps.INSTANCE, provider);
  }
}
