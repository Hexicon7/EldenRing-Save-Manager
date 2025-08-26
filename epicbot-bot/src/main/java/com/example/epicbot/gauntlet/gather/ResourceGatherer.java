package com.example.epicbot.gauntlet.gather;

import com.epicbot.api.shared.APIContext;
import com.example.epicbot.gauntlet.CorruptedGauntletConfig;
import com.example.epicbot.gauntlet.ResourceTracker;

/**
 * Handles resource gathering behaviors in the Gauntlet.
 * Concrete object/NPC names should be adjusted to match the client.
 */
public class ResourceGatherer {

    private final APIContext api;
    private final CorruptedGauntletConfig config;
    private final ResourceTracker tracker;

    public ResourceGatherer(APIContext api, CorruptedGauntletConfig config, ResourceTracker tracker) {
        this.api = api;
        this.config = config;
        this.tracker = tracker;
    }

    public int gatherBasic() {
        // Priorities per strategy: weapon frame, shards, fish, base mats
        // 1) Weapon frame via weak creatures
        if (!tracker.hasWeaponFrame()) {
            killWeakCreatureForFrame();
            return 600;
        }
        // 2) Gather shards to allow crafting
        if (tracker.shardCount() < Math.max(150, config.minShardCount)) {
            gatherShards();
            return 600;
        }
        // 3) Fish paddlefish and cook
        if (tracker.cookedFoodCount() < config.minFoodCount) {
            fishAndCook();
            return 800;
        }
        return 400;
    }

    public int gatherUpgrades() {
        // Gather mats to hit target weapon tier and armor piece counts
        if (!tracker.hasUpgrades()) {
            gatherWeaponMaterials();
            gatherArmorMaterials();
            return 700;
        }
        return 400;
    }

    private void killWeakCreatureForFrame() {
        // Use api.npcs() to find a nearby weak creature and attack
        // api.combat(), api.movement() as needed
    }

    private void gatherShards() {
        // Mine/Chop/Fish to collect shards quickly
    }

    private void fishAndCook() {
        // Interact with fishing spot; then cook at range
    }

    private void gatherWeaponMaterials() {
        // Mine ore, chop wood, gather fibre to upgrade to target tier
    }

    private void gatherArmorMaterials() {
        // Gather for helm/body/legs as configured
    }
}

