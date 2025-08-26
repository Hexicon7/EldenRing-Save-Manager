package com.example.epicbot;

import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.GameType;
import com.epicbot.api.shared.model.Tile;
import com.epicbot.api.shared.script.LoopScript;
import com.epicbot.api.shared.script.ScriptManifest;

@ScriptManifest(name = "MyFirstBot", gameType = GameType.OS)
public class MyFirstBot extends LoopScript {

    @Override
    public boolean onStart(String... args) {
        log("MyFirstBot starting");
        return true;
    }

    @Override
    protected int loop() {
        APIContext ctx = getAPIContext();
        if (!ctx.walking().isWalking()) {
            // Example: walk to Lumbridge courtyard
            ctx.walking().walkTo(new Tile(3222, 3218, 0));
        }
        return 1000; // milliseconds until next loop
    }

    @Override
    public void onStop() {
        log("MyFirstBot stopping");
    }
}
