package com.example.epicbot.gauntlet;

/**
 * Configuration for Corrupted Gauntlet routing and targets.
 * Values are conservative defaults; tune for your account/latency.
 */
public class CorruptedGauntletConfig {

    public enum WeaponTier { BASIC, ATTUNED, PERFECTED }

    public static CorruptedGauntletConfig defaults() {
        CorruptedGauntletConfig cfg = new CorruptedGauntletConfig();
        cfg.targetWeaponTier = WeaponTier.ATTUNED; // efficient and reliable
        cfg.targetArmorPieces = 2; // helm + body recommended
        cfg.minFoodCount = 10; // paddlefish
        cfg.minPotionCount = 1; // fishing/cooking potion
        cfg.minShardCount = 250; // covers gear + room for crafting
        cfg.scoutMaxTimeMs = 45_000; // timebox scouting
        cfg.prepMaxTimeMs = 7 * 60_000; // overall prep budget
        cfg.lowHpEatThreshold = 35; // eat if HP <= 35
        return cfg;
    }

    public WeaponTier targetWeaponTier;
    public int targetArmorPieces;
    public int minFoodCount;
    public int minPotionCount;
    public int minShardCount;
    public long scoutMaxTimeMs;
    public long prepMaxTimeMs;
    public int lowHpEatThreshold;
}

