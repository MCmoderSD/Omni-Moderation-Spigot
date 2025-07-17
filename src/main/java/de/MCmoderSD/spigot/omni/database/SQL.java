package de.MCmoderSD.spigot.omni.database;

import de.MCmoderSD.omni.objects.Rating;

import de.MCmoderSD.spigot.omni.enums.Flagged;
import de.MCmoderSD.sql.Driver;
import de.MCmoderSD.tools.GZIP;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import java.util.logging.Logger;

@SuppressWarnings({"FieldCanBeLocal ", "unused"})
public class SQL extends Driver {

    // Singleton instance
    private static SQL instance;

    // Attributes
    private final Logger logger;

    // Private constructor to prevent instantiation
    private SQL(JavaPlugin plugin) {

        // Initialize the database driver with SQLite configuration
        super(DatabaseType.SQLITE, null, null, plugin.getDataFolder() + "/omni.db", null, null);

        // Set logger
        logger = plugin.getLogger();

        // Initialize Tables
        try {

            // Load SQL tables from resource
            ArrayList<String> tables = new ArrayList<>(
                    Arrays.asList(
                            new String(Objects.requireNonNull(plugin.getResource("tables.sql")).readAllBytes()).split("CREATE TABLE IF NOT EXISTS")
                    )
            );

            // Connect to the database
            if (!isConnected()) connect();

            // Create tables
            for (String table : tables) if (!table.isBlank()) {
                PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (IOException | SQLException e) {
            logger.severe("Failed to initialize SQL tables: " + e.getMessage());
        }
    }

    // Initialize Singleton instance
    public static void init(JavaPlugin plugin) {
        if (instance == null) instance = new SQL(plugin);
    }

    // Get Singleton Instance
    public static SQL getInstance() {
        return instance;
    }

    public UUID getContentID(String content) throws SQLException {

        // Connect to the database if not already connected
        if (!isConnected()) connect();

        // Generate a UUID based on the content string
        UUID randomUUID = UUID.nameUUIDFromBytes(content.getBytes());

        // Prepare SQL statement to check if the content exists
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM Content WHERE content = ?"
        );

        // Set the content value
        preparedStatement.setString(1, content);
        ResultSet resultSet = preparedStatement.executeQuery();

        // If the content exists, return its ID
        if (resultSet.next()) return UUID.fromString(resultSet.getString("id"));

        // Close the ResultSet and PreparedStatement
        resultSet.close();
        preparedStatement.close();

        // If the content does not exist, insert it into the Content table
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO Content (id, content) VALUES (?, ?)"
        );

        // Set the values for the Content table
        insertStatement.setString(1, randomUUID.toString());    // ID
        insertStatement.setString(2, content);                  // Content
        insertStatement.executeUpdate(); // Execute

        // Close the insert statement
        insertStatement.close();

        // Return the generated UUID
        return randomUUID;
    }

    public void logMessage(UUID content, Player player) {
        new Thread(() -> {

            // Variables
            String uuid = player.getUniqueId().toString();
            String name = player.getName();
            String displayName = player.getDisplayName();

            try {

                // Connect to the database if not already connected
                if (!isConnected()) connect();

                // Insert or update the user in the User table
                PreparedStatement userStatement = connection.prepareStatement(
                        "INSERT INTO User (uuid, name, displayName) VALUES (?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET name = ?, displayName = ?"
                );

                // Set the values for the User table
                userStatement.setString(1, uuid);           // UUID
                userStatement.setString(2, name);           // Name
                userStatement.setString(3, displayName);    // Display Name
                userStatement.setString(4, name);           // Name
                userStatement.setString(5, displayName);    // Display Name
                userStatement.executeUpdate(); // Execute

                // Insert the message into the Message table
                PreparedStatement messageStatement = connection.prepareStatement(
                        "INSERT INTO Message (id, user, content, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)"
                );

                // Set the values for the Message table
                messageStatement.setString(1, UUID.randomUUID().toString());    // Message Event ID
                messageStatement.setString(2, uuid);                            // User UUID
                messageStatement.setString(3, content.toString());              // Content ID
                messageStatement.executeUpdate(); // Execute

                // Close the prepared statements
                userStatement.close();
                messageStatement.close();

            } catch (SQLException e) {
                logger.severe("Failed to log message: " + e.getMessage());
            }
        }).start();
    }

    public Flagged isFlagged(UUID content) throws SQLException {

        // Connect to the database if not already connected
        if (!isConnected()) connect();

        // Prepare SQL statement to check if the content is flagged
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT flagged FROM Content WHERE id = ?"
        );

        // Set the content ID
        preparedStatement.setString(1, content.toString());
        ResultSet resultSet = preparedStatement.executeQuery();

        // If the content exists, return its flagged status
        if (resultSet.next()) return switch (resultSet.getInt("flagged")) {
                case 0 -> Flagged.NO;
                case 1 -> Flagged.YES;
                default -> Flagged.UNKNOWN;
        };

        // Close the ResultSet and PreparedStatement
        resultSet.close();
        preparedStatement.close();
        return null; // Content not found
    }

    public void logRating(UUID content, Rating rating) {
        new Thread(() -> {
            try {

                // Variables
                String ratingData = Base64.getEncoder().encodeToString(GZIP.deflateObject(rating));

                // Rating Flags
                var harassment = rating.getHarassment();
                var harassmentThreatening = rating.getHarassmentThreatening();
                var hate = rating.getHate();
                var hateThreatening = rating.getHateThreatening();
                var illicit = rating.getIllicit();
                var illicitViolent = rating.getIllicitViolent();
                var selfHarm = rating.getSelfHarm();
                var selfHarmIntent = rating.getSelfHarmIntent();
                var selfHarmInstructions = rating.getSelfHarmInstructions();
                var sexual = rating.getSexual();
                var sexualMinors = rating.getSexualMinors();
                var violence = rating.getViolence();
                var violenceGraphic = rating.getViolenceGraphic();

                // Connect to the database if not already connected
                if (!isConnected()) connect();

                // Insert rating into the Rating table
                PreparedStatement ratingStatement = connection.prepareStatement(
                        "INSERT INTO Rating (id, flagged, rating) VALUES (?, ?, ?)"
                );

                // Set the values for the Rating table
                ratingStatement.setString(1, content.toString());   // Rating ID
                ratingStatement.setBoolean(2, rating.isFlagged());  // Flagged status
                ratingStatement.setString(3, ratingData);           // Serialized and encoded rating
                ratingStatement.executeUpdate(); // Execute

                // Insert categories into the Category table
                PreparedStatement categoryStatement = connection.prepareStatement(
                        "INSERT INTO Category (id, harassment, harassmentThreatening, hate, hateThreatening, illicit, illicitViolent, selfHarm, selfHarmIntent, selfHarmInstructions, sexual, sexualMinors, violence, violenceGraphic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );

                // Set the values for the Category table
                categoryStatement.setString(1, content.toString());                 // Rating ID
                categoryStatement.setBoolean(2, harassment.flagged());              // Harassment
                categoryStatement.setBoolean(3, harassmentThreatening.flagged());   // Harassment Threatening
                categoryStatement.setBoolean(4, hate.flagged());                    // Hate
                categoryStatement.setBoolean(5, hateThreatening.flagged());         // Hate Threatening
                categoryStatement.setBoolean(6, illicit.flagged());                 // Illicit
                categoryStatement.setBoolean(7, illicitViolent.flagged());          // Illicit Violent
                categoryStatement.setBoolean(8, selfHarm.flagged());                // Self Harm
                categoryStatement.setBoolean(9, selfHarmIntent.flagged());          // Self Harm Intent
                categoryStatement.setBoolean(10, selfHarmInstructions.flagged());   // Self Harm Instructions
                categoryStatement.setBoolean(11, sexual.flagged());                 // Sexual
                categoryStatement.setBoolean(12, sexualMinors.flagged());           // Sexual Minors
                categoryStatement.setBoolean(13, violence.flagged());               // Violence
                categoryStatement.setBoolean(14, violenceGraphic.flagged());        // Violence Graphic
                categoryStatement.executeUpdate(); // Execute

                // Insert scores into the Score table
                PreparedStatement scoreStatement = connection.prepareStatement(
                        "INSERT INTO Score (id, harassment, harassmentThreatening, hate, hateThreatening, illicit, illicitViolent, selfHarm, selfHarmIntent, selfHarmInstructions, sexual, sexualMinors, violence, violenceGraphic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );

                // Set the values for the Score table
                scoreStatement.setString(1, content.toString());                // Rating ID
                scoreStatement.setDouble(2, harassment.score());                // Harassment
                scoreStatement.setDouble(3, harassmentThreatening.score());     // Harassment Threatening
                scoreStatement.setDouble(4, hate.score());                      // Hate
                scoreStatement.setDouble(5, hateThreatening.score());           // Hate Threatening
                scoreStatement.setDouble(6, illicit.score());                   // Illicit
                scoreStatement.setDouble(7, illicitViolent.score());            // Illicit Violent
                scoreStatement.setDouble(8, selfHarm.score());                  // Self Harm
                scoreStatement.setDouble(9, selfHarmIntent.score());            // Self Harm Intent
                scoreStatement.setDouble(10, selfHarmInstructions.score());     // Self Harm Instructions
                scoreStatement.setDouble(11, sexual.score());                   // Sexual
                scoreStatement.setDouble(12, sexualMinors.score());             // Sexual Minors
                scoreStatement.setDouble(13, violence.score());                 // Violence
                scoreStatement.setDouble(14, violenceGraphic.score());          // Violence Graphic
                scoreStatement.executeUpdate(); // Execute

                // Close the prepared statements
                ratingStatement.close();
                categoryStatement.close();
                scoreStatement.close();

            } catch (IOException | SQLException e) {
                logger.severe("Failed to log rating: " + e.getMessage());
            }
        }).start();
    }

    public void setContent(UUID content, boolean flagged) throws SQLException {

        // Connect to the database if not already connected
        if (!isConnected()) connect();

        // Prepare SQL statement to update the flagged status of the content
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE Content SET flagged = ? WHERE id = ?"
        );

        // Set the values for the Content table
        preparedStatement.setInt(1, flagged ? 1 : 0);           // Flagged status
        preparedStatement.setString(2, content.toString());     // Content ID
        preparedStatement.executeUpdate(); // Execute

        // Close the prepared statement
        preparedStatement.close();
    }

    @SuppressWarnings("SqlWithoutWhere")
    public void resetFlags() throws SQLException {

        // Connect to the database if not already connected
        if (!isConnected()) connect();

        // Delete all entries from the Score table
        PreparedStatement scoreStatement = connection.prepareStatement(
                "DELETE FROM Score"
        );

        // Delete all entries from the Category table
        PreparedStatement categoryStatement = connection.prepareStatement(
                "DELETE FROM Category"
        );

        // Delete all entries from the Rating table
        PreparedStatement ratingStatement = connection.prepareStatement(
                "DELETE FROM Rating"
        );

        // Set all contents flagged status to null
        PreparedStatement contentStatement = connection.prepareStatement(
                "UPDATE Content SET flagged = -1"
        );

        // Execute the statements
        scoreStatement.executeUpdate();
        categoryStatement.executeUpdate();
        ratingStatement.executeUpdate();
        contentStatement.executeUpdate();

        // Close the prepared statements
        scoreStatement.close();
        categoryStatement.close();
        ratingStatement.close();
        contentStatement.close();

        // Log the reset action
        logger.info("All flags have been reset successfully.");
    }
}