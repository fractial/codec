package com.fractial.codec.event;

import com.fractial.codec.suggestion.CodecSuggestion;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.intellij.lang.annotations.RegExp;

import java.util.List;

public class EmojiListener implements Listener {
  private static TextReplacementConfig getTextReplacementConfig(@RegExp String emoji) {
    return TextReplacementConfig.builder().match(emoji).replacement(CodecSuggestion.EMOJI.get(emoji)).build();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    CodecSuggestion.setSuggestions(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(AsyncChatEvent event) {
    Component message = event.message();
    List<String> emojis = CodecSuggestion.asList();

    for (@RegExp String emoji : emojis) {
      message = message.replaceText(getTextReplacementConfig(emoji));
    }

    event.message(message);
  }

  @EventHandler
  public void onPrepareAnvil(PrepareAnvilEvent event) {
    ItemStack stack = event.getResult();
    if (stack == null) return;
    ItemMeta meta = stack.getItemMeta();
    if (meta == null) return;
    Component customName = meta.customName();
    if (customName == null) return;
    List<String> emojis = CodecSuggestion.asList();

    for (@RegExp String emoji : emojis) {
      customName = customName.replaceText(getTextReplacementConfig(emoji));
    }

    meta.customName(customName);
    stack.setItemMeta(meta);
    event.setResult(stack);
  }

  @EventHandler
  public void onSignChange(SignChangeEvent event) {
    List<Component> lines = event.lines();
    List<String> emojis = CodecSuggestion.asList();

    for (int index = 0; index < lines.size(); index++) {
      Component line = lines.get(index);
      for (@RegExp String emoji : emojis) {
        line = line.replaceText(getTextReplacementConfig(emoji));
      }
      if (line == null) return;
      event.line(index, line);
    }
  }
}
