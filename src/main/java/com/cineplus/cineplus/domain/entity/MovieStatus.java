
package com.cineplus.cineplus.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum MovieStatus {
    CARTELERA("CARTELERA"),
    PREVENTA("PREVENTA"),
    PROXIMO("PROXIMO");

    private final String value;

    MovieStatus(String value) {
        this.value = value;
    }

    // Expose enum as its canonical name when serializing
    @JsonValue
    public String getValue() {
        return value;
    }

    // Accept several alternative input strings when deserializing JSON
    private static final Map<String, MovieStatus> LOOKUP = new HashMap<>();

    static {
        for (MovieStatus s : values()) {
            LOOKUP.put(s.name(), s);
            LOOKUP.put(s.value, s);
        }
        // English synonyms commonly used by clients
        LOOKUP.put("NOW_PLAYING", CARTELERA);
        LOOKUP.put("NOW PLAYING", CARTELERA);
        LOOKUP.put("NOWPLAYING", CARTELERA);
        LOOKUP.put("PRESALE", PREVENTA);
        LOOKUP.put("PRE_SALE", PREVENTA);
        LOOKUP.put("PRE-SALE", PREVENTA);
        LOOKUP.put("COMING_SOON", PROXIMO);
        LOOKUP.put("COMING SOON", PROXIMO);
        LOOKUP.put("UPCOMING", PROXIMO);
    }

    @JsonCreator
    public static MovieStatus forValue(String input) {
        if (input == null) return null;
        String key = input.trim().toUpperCase();
        MovieStatus found = LOOKUP.get(key);
        if (found != null) return found;
        // try replacing spaces/dashes with underscore and lookup again
        key = key.replace(" ", "_").replace("-", "_");
        return LOOKUP.get(key);
    }
}
