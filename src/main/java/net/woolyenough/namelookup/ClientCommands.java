package net.woolyenough.namelookup;

import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.woolyenough.namelookup.modules.MojangAPI.*;

@Environment(EnvType.CLIENT)
public final class ClientCommands implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("names")
                .then(ClientCommandManager.argument("Username", StringArgumentType.string())
                        .executes(ClientCommands::names)));

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("n") //Alias
                .then(ClientCommandManager.argument("Username", StringArgumentType.string())
                        .executes(ClientCommands::names)));

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("namemc")
                .then(ClientCommandManager.argument("Username", StringArgumentType.string())
                        .executes(ClientCommands::namemc)));
    
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("nm")
        .then(ClientCommandManager.argument("Username", StringArgumentType.string())
                .executes(ClientCommands::namemc)));
    }

    private static int names(CommandContext<FabricClientCommandSource> context) {
        CompletableFuture.runAsync(()->{

            String name = String.valueOf(StringArgumentType.getString(context, "Username"));

            String[] playerNameAndUUID = get_player_name_and_uuid(name);

            String username = playerNameAndUUID[0];
            String uuid = playerNameAndUUID[1];

            if (Objects.equals(username, "None")){
                context.getSource().sendError(new LiteralText("No account exists by that name .-."));

            }else{
                context.getSource().sendFeedback(new LiteralText("§7[Hover] §eView name history of " + username).styled(

                        style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + username))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT, get_player_name_history(username, uuid).append(new LiteralText("\n\n§7§oClick to open in NameMC!")))
                                )));}
        });
        return 0;
    }


    private static int namemc(CommandContext<FabricClientCommandSource> context) {

        CompletableFuture.runAsync(()->{

            String name = String.valueOf(StringArgumentType.getString(context, "Username"));

            String[] playerNameAndUUID = get_player_name_and_uuid(name);

            String username = playerNameAndUUID[0];
            //String uuid = playerNameAndUUID[1];   right now useless ¯\_(ツ)_/¯

            if (Objects.equals(username, "None")){
                context.getSource().sendFeedback(new LiteralText("§7[Click] §eSearch " + name + " on NameMC §c§o(account not found)")
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + name))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(name + "\n\n§7§oClick to open NameMC")))));
            }else{

                context.getSource().sendFeedback(new LiteralText("§7[Click] §eLook at " + username + "'s NameMC page")
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + username))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(username + "\n\n§7§oClick to open NameMC")))));

            }});
        return 0;
    }
}
