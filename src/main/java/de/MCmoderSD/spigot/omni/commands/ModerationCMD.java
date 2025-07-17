package de.MCmoderSD.spigot.omni.commands;

import de.MCmoderSD.spigot.omni.core.Moderation;
import de.MCmoderSD.spigot.omni.database.SQL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class ModerationCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check OP
        if (!sender.isOp()) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // Check if no arguments are provided
        if (args.length == 0) {
            sender.sendMessage("Usage: /moderation <option> [value]");
            return true;
        }

        // Check Status
        if (args[0].equalsIgnoreCase("status")) {
            if (Moderation.getInstance().getModerator() == null) sender.sendMessage("Moderation is inactive.");
            else sender.sendMessage("Moderation is active and running.");
            return true;
        }

        // Get SQL
        SQL sql = SQL.getInstance();

        // Reset Moderation
        if (args[0].equalsIgnoreCase("reset")) {
            try {
                sql.resetFlags();
                sender.sendMessage("Moderation flags have been reset successfully.");
                return true;
            } catch (SQLException e) {
                sender.sendMessage("An error occurred while resetting moderation flags: " + e.getMessage());
                return false;
            }
        }

        // Whitelist
        if (args[0].equalsIgnoreCase("whitelist") && args.length >= 2) {
            try {

                // Construct message
                StringBuilder message = new StringBuilder();
                for (var i = 1; i < args.length; i++) message.append(args[i]).append(" ");
                String content = message.toString().trim();

                // Get Content ID
                UUID contentId = sql.getContentID(content);

                // Set Content as Whitelisted
                sql.setContent(contentId, false);

                // Respond to sender
                sender.sendMessage("Content has been whitelisted: " + content);
                return true;

            } catch (SQLException e) {
                sender.sendMessage("An error occurred while whitelisting content: " + e.getMessage());
                return false;
            }
        }

        // Blacklist
        if (args[0].equalsIgnoreCase("blacklist") && args.length >= 2) {
            try {

                // Construct message
                StringBuilder message = new StringBuilder();
                for (var i = 1; i < args.length; i++) message.append(args[i]).append(" ");
                String content = message.toString().trim();

                // Get Content ID
                UUID contentId = sql.getContentID(content);

                // Set Content as Blacklisted
                sql.setContent(contentId, true);

                // Respond to sender
                sender.sendMessage("Content has been blacklisted: " + content);
                return true;

            } catch (SQLException e) {
                sender.sendMessage("An error occurred while blacklisting content: " + e.getMessage());
                return false;
            }
        }

        // Invalid command
        sender.sendMessage("Invalid command. Usage: /moderation <reset|whitelist|blacklist|status> [value]");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (args.length) {
            case 1 -> List.of("reset", "whitelist", "blacklist", "status");
            default ->  List.of(); // No further completions
        };
    }
}