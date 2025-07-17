package de.MCmoderSD.spigot.omni.enums;

import de.MCmoderSD.omni.objects.Rating;

@SuppressWarnings("unused")
public enum Profile {

    // Profiles
    FULL(0),        // Flagged
    HIGH(35),       // Flagged and threshold 35% or more
    MEDIUM(55),     // Flagged and threshold 55% or more
    LOW(95),        // Flagged and threshold 95% or more
    MINIMAL(99),    // Flagged and threshold 99% or more
    NONE(100);      // Monitoring only, no flags

    // Percentage of the profile
    private final double threshold;

    // Constructor
    Profile(int threshold) {
        this.threshold = threshold / 100d;
    }

    // Getter
    public double getThreshold() {
        return threshold;
    }

    public boolean check(Rating rating) {
        return switch (this) {
            case NONE -> false;
            case FULL -> rating.isFlagged();
            case HIGH, MEDIUM, LOW, MINIMAL -> !rating.getFlags(true, threshold).isEmpty();
        };
    }

    // Static method to get Profile by name
    public static Profile getProfile(String string) {
        if (string == null || string.isBlank()) return null;
        for (Profile profile : values()) if (profile.name().equalsIgnoreCase(string)) return profile;
        return null;
    }
}