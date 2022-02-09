package net.woolyenough.namehistory.util;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.woolyenough.namehistory.command.NameMC;

public class ModCommandRegister {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(NameMC::register);
    }
}
