package de.MCmoderSD.spigot.omni.config;

import de.MCmoderSD.spigot.omni.enums.Profile;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ConfigHandler {

    // Placeholder templates
    private static final String API_KEY_TEMPLATE = "your_api_key_here";
    private static final String PROJECT_ID_TEMPLATE = "your_project_id_here";
    private static final String ORGANIZATION_ID_TEMPLATE = "your_organization_id_here";

    // Patterns for validation
    public static final Pattern API_KEY_PATTERN = Pattern.compile("^sk-[A-Za-z0-9_-]+$");
    public static final Pattern PROJECT_ID_PATTERN = Pattern.compile("^proj_[A-Za-z0-9]+$");
    public static final Pattern ORGANIZATION_ID_PATTERN = Pattern.compile("^org-[A-Za-z0-9]+$");

    // Singleton instance
    private static ConfigHandler instance;

    // Associations
    private final JavaPlugin plugin;
    private final Logger logger;

    // Attributes
    private String apiKey;
    private String projectId;
    private String organizationId;
    private Profile profile;

    // Constructor
    private ConfigHandler(JavaPlugin plugin) {

        // Set plugin and logger
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        // Initialize Config
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        var config = plugin.getConfig();

        // Load API Key
        apiKey = config.getString("api_key");

        // Check if API Key is set
        if (!isValid(apiKey, API_KEY_TEMPLATE, API_KEY_PATTERN)) {

            // Nullify API Key
            apiKey = null;

            // Set default API Key in config
            config.set("api_key", API_KEY_TEMPLATE);
            plugin.saveConfig();

            // Log error message
            logger.severe("API Key is not set or invalid. Please check your config.yml");
        } else logger.info("API Key loaded successfully.");


        // Load Project ID
        projectId = config.getString("Project");

        // Check if Project ID is set
        if (!isValid(projectId, PROJECT_ID_TEMPLATE, PROJECT_ID_PATTERN)) {

            // Nullify Project ID
            projectId = null;

            // Set default Project ID in config
            config.set("Project", PROJECT_ID_TEMPLATE);
            plugin.saveConfig();

            // Log error message
            logger.severe("Project ID is not set or invalid. Project ID is optional! Continuing...");
        } else logger.info("Project ID loaded successfully.");


        // Load Organization ID
        organizationId = config.getString("Organization");

        // Check if Organization ID is set
        if (!isValid(organizationId, ORGANIZATION_ID_TEMPLATE, ORGANIZATION_ID_PATTERN)) {

            // Nullify Organization ID
            organizationId = null;

            // Set default Organization ID in config
            config.set("Organization", ORGANIZATION_ID_TEMPLATE);
            plugin.saveConfig();

            // Log error message
            logger.severe("Organization ID is not set or invalid. Organization ID is optional! Continuing...");
        } else logger.info("Organization ID loaded successfully.");


        // Load Profile
        profile = Profile.getProfile(config.getString("Profile"));

        // Check if Profile is set
        if (profile == null) {

            // Set default Profile
            profile = Profile.FULL;

            // Set default Profile in config
            config.set("Profile", profile.name());
            plugin.saveConfig();

            // Log error message
            logger.severe("Profile is not set. Defaulting to FULL.");
        } else logger.info("Profile loaded successfully: " + profile.name());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValid(String input, String template, Pattern pattern) {
        return !(input == null || input.equalsIgnoreCase(template) || !pattern.matcher(input).matches());
    }

    // Initialize Singleton Instance
    public static void init(JavaPlugin plugin) {
        if (instance == null) instance = new ConfigHandler(plugin);
    }

    // Get Singleton Instance
    public static ConfigHandler getInstance() {
        return instance;
    }

    // Getters
    public String getApiKey() {
        return apiKey;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public Profile getProfile() {
        return profile;
    }

    // Setters
    public boolean setApiKey(String apiKey) {

        // Validate API Key
        if (!isValid(apiKey, API_KEY_TEMPLATE, API_KEY_PATTERN)) {
            logger.severe("Invalid API Key provided. Please check your input.");
            return false;
        }

        // Update API Key
        this.apiKey = apiKey;
        plugin.getConfig().set("api_key", apiKey);
        plugin.saveConfig();

        // Log success message
        logger.info("API Key updated successfully.");
        logger.info("Please reload the plugin to apply the new API Key.");
        return true;
    }

    public boolean setProjectId(String projectId) {

        // Validate Project ID
        if (!isValid(projectId, PROJECT_ID_TEMPLATE, PROJECT_ID_PATTERN)) {
            logger.severe("Invalid Project ID provided. Please check your input.");
            return false;
        }

        // Update Project ID
        this.projectId = projectId;
        plugin.getConfig().set("Project", projectId);
        plugin.saveConfig();

        // Log success message
        logger.info("Project ID updated successfully.");
        logger.info("Please reload the plugin to apply the new Project ID.");
        return true;
    }

    public boolean setOrganizationId(String organizationId) {

        // Validate Organization ID
        if (!isValid(organizationId, ORGANIZATION_ID_TEMPLATE, ORGANIZATION_ID_PATTERN)) {
            logger.severe("Invalid Organization ID provided. Please check your input.");
            return false;
        }

        // Update Organization ID
        this.organizationId = organizationId;
        plugin.getConfig().set("Organization", organizationId);
        plugin.saveConfig();

        // Log success message
        logger.info("Organization ID updated successfully.");
        logger.info("Please reload the plugin to apply the new Organization ID.");
        return true;
    }

    public boolean setProfile(Profile profile) {

        // Validate Profile
        if (profile == null) {
            logger.severe("Invalid Profile provided. Please check your input.");
            return false;
        }

        // Update Profile
        this.profile = profile;
        plugin.getConfig().set("Profile", profile.name());
        plugin.saveConfig();

        // Log success message
        logger.info("Profile updated successfully: " + profile.name());
        return true;
    }
}