package com.example.epicbot.gauntlet.craft;

import com.epicbot.api.shared.APIContext;
import com.example.epicbot.gauntlet.CorruptedGauntletConfig;
import com.example.epicbot.gauntlet.ResourceTracker;

/**
 * Handles crafting at singing bowl/loom/anvil.
 */
public class Crafter {

    private final APIContext api;
    private final CorruptedGauntletConfig config;
    private final ResourceTracker tracker;

    public Crafter(APIContext api, CorruptedGauntletConfig config, ResourceTracker tracker) {
        this.api = api;
        this.config = config;
        this.tracker = tracker;
    }

    public int craftAll() {
        // Craft weapon to target tier
        craftWeapon();
        // Craft armor pieces
        craftArmorPieces();
        // Cook fish if any raw remain
        cookFish();
        return 600;
    }

    private void craftWeapon() {
        // Interact with singing bowl to craft bow/staff/halberd according to materials
    }

    private void craftArmorPieces() {
        // Interact with loom/anvil for helm/body/legs per mats and config
    }

    private void cookFish() {
        // Use range to cook any raw paddlefish
    }
}

