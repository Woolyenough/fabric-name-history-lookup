package net.woolyenough.namehistory.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static net.minecraft.server.command.CommandManager.argument;

public class NameMC {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("namemc")

                .then(argument("username", StringArgumentType.string())

                        .executes(context -> {

                            String name = String.valueOf(StringArgumentType.getString(context, "username"));

                            context.getSource().sendFeedback(new LiteralText("[Click] "+name+"'s NameMC").formatted(Formatting.BOLD).styled(

                                    style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/"+ name))
                                            .withHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT, new LiteralText("View on NameMC!"))
                                            )), false);

                            return 0;
                        })));
    }
}
