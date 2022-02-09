package net.woolyenough.namehistory;

import net.woolyenough.namehistory.util.ModCommandRegister;
import net.fabricmc.api.ModInitializer;

public class NameLookup implements ModInitializer{
    @Override
    public void onInitialize() {
        ModCommandRegister.registerCommands();
    }
}
