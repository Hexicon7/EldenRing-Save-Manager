package com.example.epicbot.gauntlet;

import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.GameType;
import com.epicbot.api.shared.script.LoopScript;
import com.epicbot.api.shared.script.ScriptManifest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

@ScriptManifest(name = "CorruptedGauntletPro", gameType = GameType.OS)
public class CorruptedGauntletAllInOne extends LoopScript {

    private enum State {
        START,
        PREP_INSTANCE,
        SCOUT,
        GATHER_BASIC,
        GATHER_UPGRADE,
        CRAFT,
        PRE_BOSS,
        FIGHT_BOSS,
        EXIT,
        FAILSAFE
    }

    private enum WeaponTier { BASIC, ATTUNED, PERFECTED }

    private static class Config {
        WeaponTier targetWeaponTier = WeaponTier.ATTUNED;
        int targetArmorPieces = 2;
        int minFoodCount = 10;
        int minPotionCount = 1;
        int minShardCount = 250;
        long scoutMaxTimeMs = 45_000;
        long prepMaxTimeMs = 7 * 60_000;
        int lowHpEatThreshold = 35;
        long expectedChestValueGp = 1_100_000L; // average profit per CG chest (tune to your data)
    }

    private static class Stats {
        long startMs;
        int runsCompleted;
        long totalProfitGp;
        String status = "Idle";

        Stats(long startMs) { this.startMs = startMs; }

        long elapsedMs() { return System.currentTimeMillis() - startMs; }

        long profitPerHour() {
            long ms = elapsedMs();
            if (ms <= 0) return 0L;
            return (totalProfitGp * 3_600_000L) / ms;
        }
    }

    private static class ResourceTracker {
        private final APIContext api;
        private final Config cfg;

        ResourceTracker(APIContext api, Config cfg) {
            this.api = api;
            this.cfg = cfg;
        }

        boolean hasBasicKit() {
            return hasWeaponFrame() && shardCount() >= 150 && fishCount() >= 6;
        }

        boolean hasUpgrades() {
            boolean weaponOk = cfg.targetWeaponTier != WeaponTier.PERFECTED || hasPerfectedMaterials();
            boolean armorOk = armorPieceCount() >= cfg.targetArmorPieces;
            return weaponOk && armorOk;
        }

        boolean isFullyCrafted() {
            return hasCraftedWeapon() && armorPieceCount() >= cfg.targetArmorPieces && cookedFoodCount() >= cfg.minFoodCount;
        }

        boolean isReadyForBoss() {
            return isFullyCrafted() && shardCount() >= 20 && api.prayer().points() > 10;
        }

        int shardCount() { return count("Crystal shards", "Corrupted shards"); }
        int fishCount() { return count("Raw paddlefish"); }
        int cookedFoodCount() { return count("Paddlefish"); }
        int armorPieceCount() { return count("Corrupted helm", "Corrupted cuirass", "Corrupted greaves", "Crystal helm", "Crystal body", "Crystal legs"); }
        boolean hasWeaponFrame() { return has("Weapon frame"); }
        boolean hasCraftedWeapon() { return has("Corrupted bow", "Corrupted halberd", "Corrupted staff", "Crystal bow", "Crystal halberd", "Crystal staff"); }
        boolean hasPerfectedMaterials() { return count("Corrupted ore") >= 3 && count("Corrupted wood") >= 3 && count("Corrupted fibre") >= 3; }

        boolean eatIfLowHP(int threshold) {
            if (api.skills().hp() <= threshold && cookedFoodCount() > 0) {
                api.inventory().interact("Eat", "Paddlefish");
                return true;
            }
            return false;
        }

        private boolean has(String... names) {
            for (String n : names) if (api.inventory().contains(n)) return true;
            return false;
        }

        private int count(String... names) {
            int total = 0;
            for (String n : names) total += api.inventory().getCount(n);
            return total;
        }
    }

    private final Config cfg = new Config();
    private Stats stats;
    private ResourceTracker tracker;
    private State state = State.START;
    private long scriptStartMs = 0L;
    private long prepStartMs = 0L;

    @Override
    public boolean onStart(String... args) {
        scriptStartMs = System.currentTimeMillis();
        prepStartMs = scriptStartMs;
        stats = new Stats(scriptStartMs);
        tracker = new ResourceTracker(getAPIContext(), cfg);
        stats.status = "Starting";
        log("CorruptedGauntletPro starting");
        return true;
    }

    @Override
    public void onStop() {
        log("CorruptedGauntletPro stopping");
    }

    @Override
    protected int loop() {
        try {
            ensureSafety();
            state = determineState();
            switch (state) {
                case START: return doStart();
                case PREP_INSTANCE: return doPrepInstance();
                case SCOUT: return doScout();
                case GATHER_BASIC: return doGatherBasic();
                case GATHER_UPGRADE: return doGatherUpgrade();
                case CRAFT: return doCraft();
                case PRE_BOSS: return doPreBoss();
                case FIGHT_BOSS: return doFightBoss();
                case EXIT: return doExit();
                case FAILSAFE:
                default: return doFailsafe();
            }
        } catch (Exception ex) {
            stats.status = "Error: " + ex.getMessage();
            log("Exception: " + ex.getMessage());
            return 600;
        }
    }

    private APIContext ctx() { return getAPIContext(); }

    private State determineState() {
        if (!isInsideGauntlet()) return State.PREP_INSTANCE;
        if (!tracker.hasBasicKit()) return State.GATHER_BASIC;
        if (!tracker.hasUpgrades()) return State.GATHER_UPGRADE;
        if (!tracker.isFullyCrafted()) return State.CRAFT;
        if (!tracker.isReadyForBoss()) return State.PRE_BOSS;
        if (isInBossRoom()) return State.FIGHT_BOSS;
        return State.SCOUT;
    }

    private int doStart() {
        stats.status = "Initializing";
        return 300;
    }

    private int doPrepInstance() {
        stats.status = "Entering Gauntlet";
        // TODO: Interact with entry; toggle corrupted; confirm
        // ctx().objects()/ctx().npcs() interactions
        return 600;
    }

    private int doScout() {
        stats.status = "Scouting rooms";
        // TODO: Quick pathing to locate resource rooms
        if (System.currentTimeMillis() - prepStartMs > cfg.prepMaxTimeMs) {
            stats.status = "Prep time exceeded";
            return doPreBoss();
        }
        return 400;
    }

    private int doGatherBasic() {
        stats.status = "Gathering basics";
        // 1) Weapon frame via weak creatures
        if (!tracker.hasWeaponFrame()) {
            // TODO: Attack weak creature for frame
            return 600;
        }
        // 2) Shards
        if (tracker.shardCount() < Math.max(150, cfg.minShardCount)) {
            // TODO: Gather shards efficiently
            return 600;
        }
        // 3) Food
        if (tracker.cookedFoodCount() < cfg.minFoodCount) {
            // TODO: Fish paddlefish, then cook
            return 800;
        }
        return 400;
    }

    private int doGatherUpgrade() {
        stats.status = "Gathering upgrades";
        // TODO: Gather mats for target weapon tier and armor pieces
        return 600;
    }

    private int doCraft() {
        stats.status = "Crafting gear";
        // TODO: Singing bowl/loom/anvil crafting; cook remaining fish
        return 600;
    }

    private int doPreBoss() {
        stats.status = "Pre-boss prep";
        // TODO: Ensure loadout, restore if needed, path to boss door
        return 400;
    }

    private int doFightBoss() {
        stats.status = "Fighting Hunllef";
        // TODO: Prayer switching, step pattern, tornado avoidance, attack timing
        // Minimal placeholder behaviors
        performPrayerSwitchIfNeeded();
        stepToSafeTileIfNeeded();
        attackBossIfReady();
        eatIfLowHP();
        return 300;
    }

    private int doExit() {
        stats.status = "Looting & exiting";
        // TODO: Loot chest; increase runs + profit
        onRunCompleted();
        // TODO: Exit instance
        prepStartMs = System.currentTimeMillis();
        return 600;
    }

    private int doFailsafe() {
        stats.status = "Failsafe";
        // TODO: Try to recover to a known state or stop
        return 800;
    }

    private void ensureSafety() {
        if (tracker == null) return;
        if (tracker.eatIfLowHP(cfg.lowHpEatThreshold)) {
            stats.status = "Eating";
        }
        // TODO: Add prayer restore and stuck detection
    }

    private void eatIfLowHP() {
        tracker.eatIfLowHP(cfg.lowHpEatThreshold);
    }

    private void onRunCompleted() {
        stats.runsCompleted += 1;
        stats.totalProfitGp += cfg.expectedChestValueGp;
    }

    // ===== Environment checks (placeholders; implement via API queries) =====
    private boolean isInsideGauntlet() { return true; }
    private boolean isInBossRoom() { return false; }

    // ===== Boss helpers (placeholders) =====
    private void performPrayerSwitchIfNeeded() { /* TODO */ }
    private void stepToSafeTileIfNeeded() { /* TODO */ }
    private void attackBossIfReady() { /* TODO */ }

    // ===== HUD Overlay =====
    @Override
    public void onPaint(Graphics2D g) {
        if (stats == null) return;
        long elapsed = stats.elapsedMs();
        long hrs = elapsed / 3_600_000L;
        long mins = (elapsed % 3_600_000L) / 60_000L;
        long secs = (elapsed % 60_000L) / 1_000L;

        int x = 8, y = 28, w = 260, h = 96;
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(255, 255, 255, 220));
        g.drawRoundRect(x, y, w, h, 8, 8);

        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        int ty = y + 20;
        g.drawString("CorruptedGauntletPro", x + 10, ty); ty += 18;
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Runtime: " + hrs + "h " + mins + "m " + secs + "s", x + 10, ty); ty += 16;
        g.drawString("Status: " + stats.status, x + 10, ty); ty += 16;
        g.drawString("Runs: " + stats.runsCompleted, x + 10, ty); ty += 16;
        g.drawString("Profit: " + formatGp(stats.totalProfitGp) + " (" + formatGp(stats.profitPerHour()) + "/h)", x + 10, ty);
    }

    private String formatGp(long gp) {
        if (gp >= 1_000_000_000L) return (gp / 1_000_000_000L) + "b";
        if (gp >= 1_000_000L) return (gp / 1_000_000L) + "m";
        if (gp >= 1_000L) return (gp / 1_000L) + "k";
        return String.valueOf(gp);
    }
}

