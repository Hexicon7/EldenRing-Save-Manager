package com.example.epicbot.gauntlet;

import com.epicbot.api.shared.APIContext;

import java.util.Arrays;
import java.util.List;

/**
 * Tracks inventory/resource thresholds for Gauntlet.
 * NOTE: Item names are placeholders; adjust to the exact in-game names.
 */
public class ResourceTracker {

    private final APIContext api;
    private final CorruptedGauntletConfig config;

    public ResourceTracker(APIContext api, CorruptedGauntletConfig config) {
        this.api = api;
        this.config = config;
    }

    public boolean hasBasicKit() {
        return hasWeaponFrame() && shardCount() >= 150 && fishCount() >= 6;
    }

    public boolean hasUpgrades() {
        boolean weaponOk = config.targetWeaponTier != CorruptedGauntletConfig.WeaponTier.PERFECTED
                || hasPerfectedMaterials();
        boolean armorOk = armorPieceCount() >= config.targetArmorPieces;
        return weaponOk && armorOk;
    }

    public boolean isFullyCrafted() {
        // If we already have the crafted items (weapon + armor pieces + cooked food)
        return hasCraftedWeapon() && armorPieceCount() >= config.targetArmorPieces && cookedFoodCount() >= config.minFoodCount;
    }

    public boolean isReadyForBoss() {
        // Final check before entering boss room
        return isFullyCrafted() && shardCount() >= 20 && api.prayer().points() > 10;
    }

    // ===== Inventory helpers (placeholders) =====
    public int shardCount() {
        return countByNames("Crystal shards", "Corrupted shards");
    }

    public int fishCount() {
        return countByNames("Raw paddlefish");
    }

    public int cookedFoodCount() {
        return countByNames("Paddlefish");
    }

    public int armorPieceCount() {
        return countByNames("Corrupted helm", "Corrupted cuirass", "Corrupted greaves",
                "Crystal helm", "Crystal body", "Crystal legs");
    }

    public boolean hasWeaponFrame() {
        return hasAny("Weapon frame");
    }

    public boolean hasCraftedWeapon() {
        return hasAny("Corrupted bow", "Corrupted halberd", "Corrupted staff",
                "Crystal bow", "Crystal halberd", "Crystal staff");
    }

    public boolean hasPerfectedMaterials() {
        // Rough proxy: enough ore/wood/fibre to craft upgraded weapon
        return countByNames("Corrupted ore") >= 3 && countByNames("Corrupted wood") >= 3 && countByNames("Corrupted fibre") >= 3;
    }

    public boolean eatIfLowHP(int threshold) {
        if (api.skills().hp() <= threshold && cookedFoodCount() > 0) {
            // Use first cooked food found
            api.inventory().interact("Eat", "Paddlefish");
            return true;
        }
        return false;
    }

    private boolean hasAny(String... names) {
        for (String name : names) {
            if (api.inventory().contains(name)) return true;
        }
        return false;
    }

    private int countByNames(String... names) {
        int total = 0;
        List<String> list = Arrays.asList(names);
        for (String name : list) {
            total += api.inventory().getCount(name);
        }
        return total;
    }
}

