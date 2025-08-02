package com.fractial.codec.api;

import com.fractial.codec.item.CodecItems;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@SuppressWarnings("unused")
public class CodecItemsApi {
  public static void setItem(NamespacedKey key, ItemStack stack) {
    CodecItems.ITEM.put(parseKey(key), parseItemStack(stack));
  }

  public static Optional<ItemStack> getItem(NamespacedKey key) {
    try {
      ResourceLocation location = parseKey(key);
      if (!CodecItems.ITEM.containsKey(location)) return Optional.empty();
      return Optional.of(parseBukkitItemStack(CodecItems.ITEM.get(location)));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static boolean hasItem(NamespacedKey key) {
    try {
      return CodecItems.ITEM.containsKey(parseKey(key));
    } catch (Exception e) {
      return false;
    }
  }

  private static ResourceLocation parseKey(NamespacedKey key) {
    return ResourceLocation.parse(key.toString());
  }

  private static net.minecraft.world.item.ItemStack parseItemStack(ItemStack stack) {
    return net.minecraft.world.item.ItemStack.fromBukkitCopy(stack);
  }

  private static ItemStack parseBukkitItemStack(net.minecraft.world.item.ItemStack stack) {
    return stack.asBukkitCopy();
  }
}
