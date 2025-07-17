package de.MCmoderSD.spigot.omni.commands;

import de.MCmoderSD.spigot.omni.config.ConfigHandler;
import de.MCmoderSD.spigot.omni.enums.Profile;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigureCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check OP
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // Get Config Handler
        ConfigHandler config = ConfigHandler.getInstance();

        // Check if no arguments are provided
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "apikey" -> sender.sendMessage("Current API Key: " + (config.getApiKey() == null ? "Not set" : config.getApiKey()));
                case "projectid" -> sender.sendMessage("Current Project ID: " + (config.getProjectId() == null ? "Not set" : config.getProjectId()));
                case "organizationid" -> sender.sendMessage("Current Organization ID: " + (config.getOrganizationId() == null ? "Not set" : config.getOrganizationId()));
                case "profile" -> sender.sendMessage("Current Profile: " + (config.getProfile() == null ? "Not set" : config.getProfile().name()));
                default -> sender.sendMessage("Unknown option. Available options: ApiKey, ProjectID, OrganizationID, Profile.");
            }
            return true;
        }

        // Check arguments
        if (args.length < 2) {
            sender.sendMessage("Usage: /configure <option> [value]");
            return true;
        }

        // Handle API Key configuration
        if (args[0].equalsIgnoreCase("ApiKey")) {
            String apiKey = args[1];

            // Validate API Key
            if (apiKey.isBlank() || !ConfigHandler.API_KEY_PATTERN.matcher(apiKey).matches()) {
                sender.sendMessage("Invalid API Key provided. Please check your input.");
                return false;
            }

            // Set API Key
            boolean success = config.setApiKey(apiKey);
            if (success) sender.sendMessage("API Key has been set successfully. Please reload the plugin to apply changes.");
            else sender.sendMessage("Failed to set API Key. Please check your input.");
            return success;
        }

        // Handle Project ID configuration
        if (args[0].equalsIgnoreCase("ProjectID")) {
            String projectId = args[1];

            // Validate Project ID
            if (projectId.isBlank() || !ConfigHandler.PROJECT_ID_PATTERN.matcher(projectId).matches()) {
                sender.sendMessage("Invalid Project ID provided. Please check your input.");
                return false;
            }

            // Set Project ID
            boolean success = config.setProjectId(projectId);
            if (success) sender.sendMessage("Project ID has been set successfully. Please reload the plugin to apply changes.");
            else sender.sendMessage("Failed to set Project ID. Please check your input.");
            return success;
        }

        // Handle Organization ID configuration
        if (args[0].equalsIgnoreCase("OrganizationID")) {
            String organizationId = args[1];

            // Validate Organization ID
            if (organizationId.isBlank() || !ConfigHandler.ORGANIZATION_ID_PATTERN.matcher(organizationId).matches()) {
                sender.sendMessage("Invalid Organization ID provided. Please check your input.");
                return false;
            }

            // Set Organization ID
            boolean success = config.setOrganizationId(organizationId);
            if (success) sender.sendMessage("Organization ID has been set successfully. Please reload the plugin to apply changes.");
            else sender.sendMessage("Failed to set Organization ID. Please check your input.");
            return success;
        }

        // Handle Profile configuration
        if (args[0].equalsIgnoreCase("Profile")) {

            // Parse Profile
            Profile profile = Profile.getProfile(args[1].toUpperCase());

            // Validate Profile
            if (profile == null) {
                sender.sendMessage("Invalid profile provided. Available profiles: FULL, HIGH, MEDIUM, LOW, MINIMAL, NONE.");
                return false;
            }

            // Set Profile
            boolean success = config.setProfile(profile);
            if (success) sender.sendMessage("Profile has been set successfully.");
            else sender.sendMessage("Failed to set profile. Please check your input.");
            return success;
        }

        // Invalid command
        sender.sendMessage("Invalid command. Available options: ApiKey, ProjectID, OrganizationID, Profile.");
        return  false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> List.of("ApiKey", "ProjectID", "OrganizationID", "Profile");
            case 2 -> args[0].equalsIgnoreCase("Profile") ? List.of("FULL", "HIGH", "MEDIUM", "LOW", "MINIMAL", "NONE") : List.of();
            default -> List.of(); // No further completions
        };
    }
}