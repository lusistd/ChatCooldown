package com.lusistd.chatcooldown;

import com.lusistd.chatcooldown.listeners.ChatListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author lusistd
 */
public class ChatCooldown extends JavaPlugin implements CommandExecutor {

    private static ChatCooldown instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("chatcooldown").setExecutor(this);
        getLogger().info("ChatCooldown aktif edildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatCooldown devre dışı bırakıldı.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("chatcooldown.admin")) {
                sender.sendMessage(colorize(getConfig().getString("messages.no-permission")));
                return true;
            }
            reloadConfig();
            sender.sendMessage(colorize(getConfig().getString("messages.reload-success")));
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "/chatcooldown reload");
        return true;
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static ChatCooldown getInstance() {
        return instance;
    }
}
