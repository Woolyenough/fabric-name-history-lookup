package net.woolyenough.namehistory;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

/*
import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
*/

@Environment(EnvType.CLIENT)
public final class ClientCommands implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
/*
        // CLIENT COMMAND

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("test_client_command").executes(context -> {
            context.getSource().sendFeedback(new LiteralText("Working"));

            return 0;
        }));
*/
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("namemc")
                .then(ClientCommandManager.argument("Player Name", StringArgumentType.string())
                        .executes(context -> {

                            String name = String.valueOf(StringArgumentType.getString(context, "Player Name"));

                            context.getSource().sendFeedback(new LiteralText("[Click] " + name + "'s NameMC").formatted(Formatting.BOLD).styled(

                                    style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + name))
                                            .withHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT, new LiteralText("View on NameMC!"))
                                            )));

                            return 0;
                        })));
/*
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("lookup")
                .then(ClientCommandManager.argument("Player Name", StringArgumentType.string())
                        .executes(context -> {
                            String name = String.valueOf(StringArgumentType.getString(context, "Player Name"));

                            context.getSource().sendFeedback(new LiteralText("The person is not in your game"));
                            String resp = null;

                            try {

                                resp = getRequest("https://api.mojang.com/users/profiles/minecraft/" + name);

                            } catch (IOException e) {
                                context.getSource().sendFeedback(new LiteralText("An error occurred when trying " +
                                        "to connect to https://api.mojang.com/users/profiles/minecraft/" + name));
                            }
                            String jsonString = resp;
                            JSONArray obj = new JSONArray(jsonString);
                            try {
                                String accountName = obj.getJSONObject(0).getString("name");
                                String accountUUID = obj.getJSONObject(0).getString("id");
                                context.getSource().sendFeedback(new LiteralText("UUID: " + accountUUID + "\nName: " + accountName+"\nNameMC: https://namemc.com/profile/"+accountUUID));
                            }catch(Exception e){context.getSource().sendError(new LiteralText("That user does not exist!"));}

                            return 0;
                        })));


    }
    private static String getRequest(String urlToRead) throws IOException {
        URL url = new URL(urlToRead);
        URLConnection connection = url.openConnection();

        ArrayList<String> stuff = new ArrayList<String>();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream())))
        {
            String line;
            while ((line = in.readLine()) != null) {
                stuff.add(line);
            }
        }
        return stuff.toString();
    }
*/
    }
}