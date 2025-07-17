package de.MCmoderSD.spigot.omni.main;

import de.MCmoderSD.spigot.omni.core.Moderation;
import de.MCmoderSD.tools.GZIP;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {

    // Singleton instance of the JavaPlugin
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {

        // Initialize the plugin instance
        plugin = this;

        // Inflate Database
        inflateDatabase();

        // Initialize Moderation
        Moderation.init(this);
    }

    @Override
    public void onDisable() {

        // Deflate Database
        deflateDatabase();

        // Nullify Plugin
        plugin = null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean registerCommand(String name, CommandExecutor command) {

        // Register command
        PluginCommand pluginCommand = plugin.getCommand(name);

        // Check if registration was successful
        if (pluginCommand == null) {
            plugin.getLogger().severe("Failed to register command: " + name);
            return false;
        }

        // Set the executor for the command
        pluginCommand.setExecutor(command);

        // Log successful registration
        plugin.getLogger().info("Command registered: " + name);
        return true;
    }

    public static void registerEventListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void inflateDatabase() {
        try {

            // Files
            File database = new File(getDataFolder(), "omni.db");
            File archive = new File(getDataFolder(), "omni.gz");

            // Inflate
            if (!database.exists() && archive.exists()) {
                database.createNewFile();
                GZIP.inflate(archive, database);
                archive.delete();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to inflate database: " + e.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deflateDatabase() {
        try {

            // Files
            File database = new File(getDataFolder(), "omni.db");
            File archive = new File(getDataFolder(), "omni.gz");

            // Deflate
            if (database.exists() && !archive.exists()) {
                archive.createNewFile();
                GZIP.deflate(database, archive);
                database.delete();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to inflate database: " + e.getMessage());
        }
    }
}