package net.woolyenough.playerlookup.util;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.woolyenough.playerlookup.command.NameMCLookup;

public class ModCommandRegister {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(NameMCLookup::register);
    }
}
