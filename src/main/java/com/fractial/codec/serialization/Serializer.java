package com.fractial.codec.serialization;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class Serializer {
  public static final String NAME = "CODEC";

  public static <T> JsonElement serialize(T t) throws ReflectiveOperationException {
    return serialize(t, NAME);
  }

  public static <T> JsonElement serialize(T t, String name) throws ReflectiveOperationException {
    Codec<T> codec = getCodec((Class<T>) t.getClass(), name);
    return unwrapResult(codec.encodeStart(getOps(), t));
  }

  public static <T> T deserialize(JsonElement json, Class<T> clazz) throws ReflectiveOperationException {
    return deserialize(json, NAME, clazz);
  }

  public static <T> T deserialize(JsonElement json, String name, Class<T> clazz) throws ReflectiveOperationException {
      Codec<T> codec = getCodec(clazz, name);
      return unwrapResult(codec.parse(getOps(), json));
  }

  private static <T> Codec<T> getCodec(Class<T> clazz, String name) throws ReflectiveOperationException {
    Field field = clazz.getField(name);
    return (Codec<T>) field.get(null);
  }

  private static RegistryOps<JsonElement> getOps() {
    HolderLookup.Provider provider = MinecraftServer.getServer().registries().compositeAccess();
    return RegistryOps.create(JsonOps.INSTANCE, provider);
  }

  private static <T> T unwrapResult(DataResult<T> result) {
    return result.result().orElseThrow(() -> new IllegalStateException(result.error().map(Objects::toString).orElse("Unknown error")));
  }
}
