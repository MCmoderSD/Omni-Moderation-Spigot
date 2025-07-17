package de.MCmoderSD.spigot.omni.core;

import de.MCmoderSD.spigot.omni.commands.ConfigureCMD;
import de.MCmoderSD.spigot.omni.commands.ModerationCMD;
import de.MCmoderSD.spigot.omni.database.SQL;
import de.MCmoderSD.spigot.omni.config.ConfigHandler;
import de.MCmoderSD.spigot.omni.events.ChatListener;
import de.MCmoderSD.spigot.omni.main.Main;

import de.MCmoderSD.omni.Moderator;

import org.bukkit.plugin.java.JavaPlugin;

public class Moderation {

    // Singleton instance
    private static Moderation instance;

    // Attributes
    private final Moderator moderator;

    // Constructor
    private Moderation(JavaPlugin plugin) {

        // Get Logger
        var logger = plugin.getLogger();

        // Initialize Config Handler
        ConfigHandler.init(plugin);
        var config = ConfigHandler.getInstance();

        // Register Commands
        Main.registerCommand("moderation", new ModerationCMD());
        Main.registerCommand("configure", new ConfigureCMD());

        // Nullify Moderator if API Key is not set
        if (config.getApiKey() == null) {
            moderator = null;
            logger.severe("No API Key set. Plugin will not function properly.");
        } else {

            // Initialize Moderator
            moderator = new Moderator(
                    config.getApiKey(),             // API Key          (required)
                    config.getProjectId(),          // Project ID       (optional)
                    config.getOrganizationId()      // Organization ID  (optional)
            );

            logger.info("Moderator initialized successfully.");
        }

        // Initialize SQL
        SQL.init(plugin);

        // Register Message Handler
        Main.registerEventListener(new ChatListener());
    }

    // Initialize Singleton Instance
    public static void init(JavaPlugin plugin) {
        if (instance == null) instance = new Moderation(plugin);
    }

    // Get Singleton Instance
    public static Moderation getInstance() {
        return instance;
    }

    // Getter
    public Moderator getModerator() {
        return moderator;
    }
}