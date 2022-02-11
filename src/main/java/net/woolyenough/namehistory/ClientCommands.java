package net.woolyenough.namehistory;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.LiteralText;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Environment(EnvType.CLIENT)
public final class ClientCommands implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("lookup")
                .then(ClientCommandManager.argument("Player Name", StringArgumentType.string())
                        .executes(context -> {
                            String name = String.valueOf(StringArgumentType.getString(context, "Player Name"));
                            context.getSource().sendFeedback(new LiteralText(get_name_history(name)));
                            return 0;
                        })));
    }

    private String get_name_history(String name) {
        String uuid = get_api_request("https://api.mojang.com/users/profiles/minecraft/" + name).split("\"")[7];
        List<String> nameHistory = new ArrayList<String>(Arrays.asList(get_api_request("https://api.mojang.com/user/profiles/" + uuid + "/names").split("\\[|\\]|\\{|\\}|\"|\\,")));
        nameHistory.removeAll(Arrays.asList("", null, " ", ":"));
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < nameHistory.size(); i++) {
            switch (nameHistory.get(i)) {
                case "name": {
                    i++;
                    output.append("\n§r§b");
                    if (Objects.equals(nameHistory.get(i), name)) output.append("§a");
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

            //Getting the response code
            int response = conn.getResponseCode();

            if (response != 200) {
                throw new RuntimeException("HttpResponseCode: " + response);
            } else {
                StringBuilder inline = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }

                //Close the scanner
                scanner.close();
                output = inline.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
