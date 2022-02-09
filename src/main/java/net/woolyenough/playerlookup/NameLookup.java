package net.woolyenough.playerlookup;

import net.woolyenough.playerlookup.util.ModCommandRegister;
import net.fabricmc.api.ModInitializer;

public class NameLookup implements ModInitializer{
    @Override
    public void onInitialize() {
        ModCommandRegister.registerCommands();
    }
}
