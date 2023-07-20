package com.ashkiano.icarus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

//TODO udělat překlady
//TODO configurovat výšky hlášek
//inspired by https://www.spigotmc.org/resources/icarus.62287/

// The main class of the plugin, implementing the Listener interface to listen to events
public class Icarus extends JavaPlugin implements Listener {
    // Variables to hold plugin configuration options
    private int maxHeight;
    private int fireDuration;
    private String fireMessage;
    private boolean shouldDisplayMessage;
    private int messageHeight;
    private String message;
    private String bypassPermission; // This will hold the bypass permission set in the config file

    // Called when the plugin is enabled
    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        // Get the FileConfiguration object
        FileConfiguration config = this.getConfig();
        // Load values from the config file into our variables
        maxHeight = config.getInt("max-height");
        fireDuration = config.getInt("fire-duration");
        fireMessage = config.getString("fire-message");
        shouldDisplayMessage = config.getBoolean("display-message");
        messageHeight = config.getInt("message-height");
        message = config.getString("message");
        // Load the bypass permission from the config file, defaulting to "icarus.bypass" if it's not there
        bypassPermission = config.getString("bypass-permission", "icarus.bypass");
        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);
        // Setup metrics for the plugin
        Metrics metrics = new Metrics(this, 18887);
    }

    // Called when a player moves
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Get the Player who moved
        Player player = event.getPlayer();
        // If the player has the bypass permission, do nothing
        if (player.hasPermission(bypassPermission)) return;
        // If the player's Y position is greater than maxHeight, set them on fire
        if (player.getLocation().getBlockY() > maxHeight) {
            player.setFireTicks(20 * fireDuration);
            // If shouldDisplayMessage is true, send the player a message
            if (shouldDisplayMessage) player.sendMessage(fireMessage);
        }
        // If the player's Y position is greater than messageHeight, send them a warning
        else if (player.getLocation().getBlockY() > messageHeight) {
            player.sendMessage(message);
        }
    }
}