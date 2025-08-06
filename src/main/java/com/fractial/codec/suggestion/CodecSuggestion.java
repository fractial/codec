package com.fractial.codec.suggestion;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodecSuggestion {
  public static HashMap<String, String> EMOJI = new HashMap<>();

  public static void setSuggestions(Player player) {
    player.addCustomChatCompletions(asList());
  }

  public static void removeSuggestions(Player player) {
    player.removeCustomChatCompletions(asList());
  }

  public static List<String> asList() {
    return new ArrayList<>(EMOJI.keySet());
  }
}
