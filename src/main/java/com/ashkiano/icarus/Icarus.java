package com.ashkiano.icarus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

//TODO udělat překlady
//TODO configurovat výšky hlášek
//TODO udelat permisi configurovatelnou
//inspired by https://www.spigotmc.org/resources/icarus.62287/
public class Icarus extends JavaPlugin implements Listener {
    private int maxHeight;
    private int fireDuration;
    private String fireMessage;
    private boolean shouldDisplayMessage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        maxHeight = config.getInt("max-height");
        fireDuration = config.getInt("fire-duration");
        fireMessage = config.getString("fire-message");
        shouldDisplayMessage = config.getBoolean("display-message");
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 18887);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("icarus.bypass")) return;
        if (player.getLocation().getBlockY() > maxHeight) {
            player.setFireTicks(20 * fireDuration);
            if (shouldDisplayMessage) player.sendMessage(fireMessage);
        }
    }
}