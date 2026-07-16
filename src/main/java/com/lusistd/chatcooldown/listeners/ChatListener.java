package com.lusistd.chatcooldown.listeners;

import com.lusistd.chatcooldown.ChatCooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lusistd
 */
public class ChatListener implements Listener {

    private final ChatCooldown plugin;
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private final Map<UUID, String> lastMessageContent = new HashMap<>();

    public ChatListener(ChatCooldown plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("chatcooldown.bypass")) {
            return;
        }

        UUID uuid = player.getUniqueId();
        String message = event.getMessage();

        boolean cooldownEnabled = plugin.getConfig().getBoolean("cooldown.enabled");
        boolean duplicateCheckEnabled = plugin.getConfig().getBoolean("duplicate-message.enabled");

        if (duplicateCheckEnabled) {
            String lastMsg = lastMessageContent.get(uuid);
            if (lastMsg != null && lastMsg.equalsIgnoreCase(message)) {
                event.setCancelled(true);
                player.sendMessage(colorize(
                        plugin.getConfig().getString("messages.duplicate-message")
                ));
                return;
            }
        }

        if (cooldownEnabled) {
            long cooldownMillis = plugin.getConfig().getLong("cooldown.seconds") * 1000L;
            long now = System.currentTimeMillis();
            Long last = lastMessageTime.get(uuid);

            if (last != null && (now - last) < cooldownMillis) {
                long remainingMs = cooldownMillis - (now - last);
                long remainingSec = (remainingMs / 1000) + 1;

                event.setCancelled(true);
                player.sendMessage(colorize(
                        plugin.getConfig().getString("messages.cooldown-wait")
                                .replace("%time%", String.valueOf(remainingSec))
                ));
                return;
            }

            lastMessageTime.put(uuid, now);
        }

        lastMessageContent.put(uuid, message);
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
