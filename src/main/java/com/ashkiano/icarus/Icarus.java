package com.ashkiano.icarus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//TODO udělat překlady
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
    private String reloadPermission; // This will hold the reload permission set in the config file
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
        reloadPermission = config.getString("reload-permission", "icarus.reload"); // Load the reload permission from the config file
        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);
        // Setup metrics for the plugin
        Metrics metrics = new Metrics(this, 18887);

        boolean showDonateMessage = getConfig().getBoolean("ShowDonateMessage", true);
        if (showDonateMessage) {
            this.getLogger().info("Thank you for using the Icarus plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");
        }

        checkForUpdates();
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("icarus") && sender.hasPermission(reloadPermission)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage("Icarus configuration reloaded.");
                return true;
            }
        }
        return false;
    }

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://plugins.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }
}