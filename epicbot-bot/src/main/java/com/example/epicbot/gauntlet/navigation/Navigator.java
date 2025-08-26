package com.example.epicbot.gauntlet.navigation;

import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.model.Tile;

/**
 * Lightweight navigation utilities.
 */
public class Navigator {

    private final APIContext api;

    public Navigator(APIContext api) {
        this.api = api;
    }

    public boolean walkTo(Tile tile) {
        if (tile == null) return false;
        if (!api.walking().isWalking()) {
            return api.walking().walkTo(tile);
        }
        return true;
    }

    public boolean isWalking() {
        return api.walking().isWalking();
    }

    public void tickCamera() {
        // Optionally rotate camera or adjust zoom if stuck
    }
}

