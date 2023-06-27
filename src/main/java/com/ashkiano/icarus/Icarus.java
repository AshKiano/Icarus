package com.ashkiano.icarus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

//TODO udělat v configu na jak dlouho má být zapálen
//TODO udělat možnost vypsat hlášku s tím že bude zapálen a configurovat výšky hlášek
//TODO přidat že kdo má permisi nebude zapálen
//TODO přidat na git a odkaz do spigotu
//inspired by https://www.spigotmc.org/resources/icarus.62287/
public class Icarus extends JavaPlugin implements Listener {
    private int maxHeight;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        maxHeight = config.getInt("max-height");
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 18887);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getBlockY() > maxHeight) {
            player.setFireTicks(20 * 5);  // This will set the player on fire for 5 seconds
        }
    }
}
