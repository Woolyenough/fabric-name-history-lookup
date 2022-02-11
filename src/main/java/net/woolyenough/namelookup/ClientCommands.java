package net.woolyenough.namelookup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Environment(EnvType.CLIENT)
public final class ClientCommands implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("namemc")
                .then(ClientCommandManager.argument("Player Name", StringArgumentType.string())
                        .executes(context -> {

                            String name = String.valueOf(StringArgumentType.getString(context, "Player Name"));
                            String[] name_and_uuid = get_name_and_uuid(name);

                            String username = name_and_uuid[0];
                            String uuid = name_and_uuid[1];

                            if (username == "None"){
                                context.getSource().sendFeedback(new LiteralText("§7§l[Click] §oSearch "+name+" on NameMC §c§o(account doesn't exist)")
                                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + name))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(name + "\n\n§7§oClick to open NameMC")))));
                                return 0;
                            }

                            context.getSource().sendFeedback(new LiteralText("§7§l[Click] §oLook at "+username+"'s NameMC page")
                                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + username))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(username + "\n\n§7§oClick to open NameMC")))));

                            return 0;
                        })));

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("lookup")
                .then(ClientCommandManager.argument("Player Name", StringArgumentType.string())
                        .executes(context -> {

                            String name = String.valueOf(StringArgumentType.getString(context, "Player Name"));

                            String[] name_and_uuid = get_name_and_uuid(name);

                            String username = name_and_uuid[0];
                            String uuid = name_and_uuid[1];

                            if (username == "None"){
                                context.getSource().sendError(new LiteralText("No account exists by that name .-."));
                                return 0;
                            }

                            context.getSource().sendFeedback(new LiteralText("§7§l[Hover] §oView name history of "+username).formatted(Formatting.BOLD).styled(

                                    style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + username))
                                            .withHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT, new LiteralText(get_name_history(username, uuid) + "\n\n§rClick to open NameMC!"))
                                            )));
                            return 0;
                        })));
    }

    private String[] get_name_and_uuid(String name){
        String[] name_and_uuid = new String[2];

        String nameAndUUIDJson = get_api_request("https://api.mojang.com/users/profiles/minecraft/" + name);

        JsonParser parser = new JsonParser();

        try{JsonElement jsonTree = parser.parse(nameAndUUIDJson);JsonObject jsonObject = jsonTree.getAsJsonObject();} //Check if name is valid
        catch(IllegalStateException e){name_and_uuid[0]="None";return name_and_uuid;}

        JsonElement jsonTree = parser.parse(nameAndUUIDJson);JsonObject jsonObject = jsonTree.getAsJsonObject();

        name_and_uuid[0] = jsonObject.get("name").getAsString();
        name_and_uuid[1] = jsonObject.get("id").getAsString();
        return name_and_uuid;
    }

    private String get_name_history(String name, String uuid) {
        List<String> nameHistory = new ArrayList<String>(Arrays.asList(get_api_request("https://api.mojang.com/user/profiles/" + uuid + "/names").split("\\[|\\]|\\{|\\}|\"|\\,")));
        nameHistory.removeAll(Arrays.asList("", null, " ", ":"));
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < nameHistory.size(); i++) {
            switch (nameHistory.get(i)) {
                case "name": {
                    i++;

                    if (nameHistory.get(i).equals(name)){
                        if (i == 1)
                            output.append("§r§a§l");
                        else
                            output.append("\n§r§a§l");
                    }
                    else{
                        if (i == 1)
                            output.append("§r§b");
                        else
                            output.append("\n§r§b");
                    }

                    output.append(nameHistory.get(i));
                    break;
                }
                case "changedToAt": {
                    i++;
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy (HH:mm)");
                    Date result = new Date(Long.parseLong(nameHistory.get(i).split(":")[1]));
                    output.append("  §r§7").append(simple.format(result));
                    break;
                }
            }
        }
        return output.toString();
    }

    private String get_api_request(String api_url) {
        String output = "";
        try {
            URL url = new URL(api_url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int response = conn.getResponseCode();

            if (response != 200) {;} else {
                StringBuilder inline = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }

                scanner.close();
                output = inline.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}