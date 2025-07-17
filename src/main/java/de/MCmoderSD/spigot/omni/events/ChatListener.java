package de.MCmoderSD.spigot.omni.events;

import de.MCmoderSD.spigot.omni.config.ConfigHandler;
import de.MCmoderSD.spigot.omni.core.Moderation;
import de.MCmoderSD.spigot.omni.database.SQL;

import de.MCmoderSD.omni.objects.Rating;

import de.MCmoderSD.spigot.omni.enums.Flagged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) throws SQLException {

        // Variables
        SQL sql = SQL.getInstance();
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Get Content ID
        UUID content = sql.getContentID(message);

        // Log Message
        sql.logMessage(content, player);

        // Check if Moderation is Initialized
        var moderator = Moderation.getInstance().getModerator();
        if (moderator == null) return;

        // Check if Content is Flagged
        Flagged isFlagged = sql.isFlagged(content);

        // Moderate if not rated
        if (isFlagged == Flagged.UNKNOWN) {

            // Get Rating
            Rating rating = moderator.moderate(message);

            // Log Rating
            sql.logRating(content, rating);

            // Check if Rating is flagged and set content accordingly
            boolean flagged = ConfigHandler.getInstance().getProfile().check(rating);

            // Set Content in SQL
            sql.setContent(content, flagged);

            // If flagged, cancel the event
            if (flagged) cancelEvent(event, player);
        }  else if (isFlagged == Flagged.YES) cancelEvent(event, player);
    }

    private void cancelEvent(AsyncPlayerChatEvent event, Player player) {
        event.setCancelled(true);
        player.sendMessage("Your message has been blocked due to its content.");
    }
}