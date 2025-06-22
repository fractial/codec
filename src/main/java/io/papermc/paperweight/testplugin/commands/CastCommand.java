package io.papermc.paperweight.testplugin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paperweight.testplugin.TestPlugin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class CastCommand {
  public static final int MAX_ALLOWED_ITEMSTACKS = 100;
  private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((item, quantity) -> Component.translatableEscape("arguments.item.overstacked", new Object[]{item, quantity}));

  private static final SuggestionProvider<CommandSourceStack> ITEM_SUGGESTIONS = CastCommand::listSuggestions;

  private static CompletableFuture<Suggestions> listSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
    String rawInput = builder.getInput().substring(builder.getStart()).toLowerCase();

    boolean hasColon = rawInput.contains(":");
    String inputNamespace = "";
    String inputPath = "";

    if (hasColon) {
      String[] split = rawInput.split(":", 2);
      inputNamespace = split[0];
      inputPath = split.length > 1 ? split[1] : "";
    } else {
      // No colon yet - could be partial namespace or partial path
      // We'll treat it as partial namespace or partial path

      // We want to suggest:
      // - all items where namespace starts with rawInput OR
      // - all items where path starts with rawInput
    }

    for (ResourceLocation id : TestPlugin.ITEMS.keySet()) {
      String fullId = id.toString().toLowerCase();
      String namespace = id.getNamespace().toLowerCase();
      String path = id.getPath().toLowerCase();

      boolean matches;

      if (hasColon) {
        // Input is namespaced like "mc:" or "mc:n"
        matches = fullId.startsWith(rawInput);
      } else {
        // No colon - suggest if either namespace or path starts with input
        matches = namespace.startsWith(rawInput) || path.startsWith(rawInput);
      }

      if (matches) {
        builder.suggest(id.toString());
      }
    }

    return builder.buildFuture();
  }

  public CastCommand() {
  }

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("cast").requires((source) -> source.hasPermission(2))).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ResourceLocationArgument.id()).suggests(ITEM_SUGGESTIONS).executes((context1) -> giveItem((CommandSourceStack)context1.getSource(), ResourceLocationArgument.getId(context1, "item"), EntityArgument.getPlayers(context1, "targets"), 1))).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((context1) -> giveItem((CommandSourceStack)context1.getSource(), ResourceLocationArgument.getId(context1, "item"), EntityArgument.getPlayers(context1, "targets"), IntegerArgumentType.getInteger(context1, "count")))))));
  }

  private static int giveItem(CommandSourceStack source, ResourceLocation item, Collection<ServerPlayer> targets, int count) throws CommandSyntaxException {
    ItemStack itemStack2 = TestPlugin.ITEMS.get(item);
    ItemStack itemStack = createItemStack(itemStack2, 1, false);
    Component displayName = itemStack.getDisplayName();
    int maxStackSize = itemStack.getMaxStackSize();
    int i = maxStackSize * 100;
    if (count > i) {
      source.sendFailure(Component.translatable("commands.give.failed.toomanyitems", new Object[]{i, itemStack.getDisplayName()}));
      return 0;
    } else {
      for(ServerPlayer serverPlayer : targets) {
        int i1 = count;

        while(i1 > 0) {
          int min = Math.min(maxStackSize, i1);
          i1 -= min;
          ItemStack itemStack1 = createItemStack(itemStack2, min, false);
          boolean flag = serverPlayer.getInventory().add(itemStack1);
          if (flag && itemStack1.isEmpty()) {
            ItemEntity itemEntity = serverPlayer.drop(itemStack, false, false, false, (Consumer)null);
            if (itemEntity != null) {
              itemEntity.makeFakeItem();
            }

            serverPlayer.level().playSound((Entity)null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            serverPlayer.containerMenu.broadcastChanges();
          } else {
            ItemEntity itemEntity = serverPlayer.drop(itemStack1, false, false, false, (Consumer)null);
            if (itemEntity != null) {
              itemEntity.setNoPickUpDelay();
              itemEntity.setTarget(serverPlayer.getUUID());
            }
          }
        }
      }

      if (targets.size() == 1) {
        source.sendSuccess(() -> Component.translatable("commands.give.success.single", new Object[]{count, displayName, ((ServerPlayer)targets.iterator().next()).getDisplayName()}), true);
      } else {
        source.sendSuccess(() -> Component.translatable("commands.give.success.single", new Object[]{count, displayName, targets.size()}), true);
      }

      return targets.size();
    }
  }

  private static ItemStack createItemStack(ItemStack itemStack, int count, boolean allowOversizedStacks) throws CommandSyntaxException {
    itemStack.setCount(count);
    if (allowOversizedStacks && count > itemStack.getMaxStackSize()) {
      throw ERROR_STACK_TOO_BIG.create(itemStack.getItemName(), itemStack.getMaxStackSize());
    } else {
      return itemStack;
    }
  }
}
