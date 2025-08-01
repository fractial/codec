package com.fractial.codec.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TemplateItemStack {
  private final ResourceLocation template;
  private final Item item;
  private final int count;
  private final DataComponentPatch components;

  public TemplateItemStack(ResourceLocation template, Holder<Item> item, int count, DataComponentPatch components) {
    this.template = template;
    this.item = item.value();
    this.count = count;
    this.components = components;
  }

  public ResourceLocation getTemplate() {
    return template;
  }

  @SuppressWarnings("deprecation")
  public Holder<Item> getItem() {
    return item.builtInRegistryHolder();
  }

  public int getCount() {
    return count;
  }

  public DataComponentPatch getComponents() {
    return components;
  }

  public static final MapCodec<TemplateItemStack> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("template").forGetter(TemplateItemStack::getTemplate),
    Item.CODEC.fieldOf("id").forGetter(TemplateItemStack::getItem),
    Codec.INT.fieldOf("count").orElse(1).forGetter(TemplateItemStack::getCount),
    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(TemplateItemStack::getComponents)
  ).apply(instance, TemplateItemStack::new));

  public static final Codec<TemplateItemStack> CODEC = MAP_CODEC.codec();

  public ItemStack asItemStack() {
    ItemStack baseStack = CodecItems.ITEM.get(template);
    if (baseStack == null) return new ItemStack(item.builtInRegistryHolder(), count, components);
    ItemStack stack = baseStack.copy();
    stack.setItem(item);
    stack.setCount(count);
    stack.applyComponents(components);
    return stack;
  }
}
