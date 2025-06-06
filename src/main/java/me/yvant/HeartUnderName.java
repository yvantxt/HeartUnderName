package me.yvant;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HeartUnderName extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("healthbar").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player) || !sender.isOp()) return true;
            reloadConfig();
            config = getConfig();
            sender.sendMessage(ChatColor.GREEN + "[HealthBar] Reloaded.");
            return true;
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateHealthBar(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) cancel();
                updateHealthBar(player);
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().setPlayerListName(null);
    }

    private void updateHealthBar(Player player) {
        if (!config.getBoolean("healthbar.enabled", true)) return;

        if (player.hasMetadata("NPC") && !config.getBoolean("healthbar.show-for-npcs", false)) return;

        String template = config.getString("healthbar.format", "&c❤ %health% | %ping%⚡");
        String formatted = PlaceholderAPI.setPlaceholders(player, template
                .replace("%health%", String.valueOf((int) player.getHealth()))
                .replace("%ping%", getPing(player) + ""));
        player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', formatted));
    }

    private int getPing(Player player) {
        try {
            return player.getPing();
        } catch (Exception e) {
            return -1;
        }
    }
}
