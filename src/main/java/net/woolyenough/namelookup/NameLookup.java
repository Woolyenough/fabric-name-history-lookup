package net.woolyenough.namelookup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.woolyenough.namelookup.modules.MojangAPI;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class NameLookup {
    private static String get_raw_response(String api_url) {
        try {
            URL url = new URL("https://api.mojang.com/" + api_url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            StringBuilder output = new StringBuilder();
            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    output.append(scanner.nextLine());
                }

                scanner.close();
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } return "";  // Return an empty String if something failed
    }

    public static String[] get_player_name_and_uuid(String name) {
        String[] playerNameAndUUID = new String[2];

        String nameAndUUIDJson = get_raw_response("users/profiles/minecraft/" + name);

        JsonParser parser = new JsonParser();

        try {  //Check if the name is valid
            JsonElement jsonTree = parser.parse(nameAndUUIDJson);
            jsonTree.getAsJsonObject();
        } catch (IllegalStateException e) {
            playerNameAndUUID[0]="None";
            return playerNameAndUUID;
        }

        JsonElement jsonTree = parser.parse(nameAndUUIDJson);
        JsonObject jsonObject = jsonTree.getAsJsonObject();

        playerNameAndUUID[0] = jsonObject.get("name").getAsString();
        playerNameAndUUID[1] = jsonObject.get("id").getAsString();
        return playerNameAndUUID;
    }

    public static LiteralText get_player_name_history(String[] player) {
        List<String> playerNameHistory = new ArrayList<>(Arrays.asList(MojangAPI.get_raw_response("user/profiles/" + player[1] + "/names").split("[\\[\\]{}\",:]")));
        playerNameHistory.removeAll(Arrays.asList("", null, " ", ":"));
        //StringBuilder output = new StringBuilder();
        LiteralText output = new LiteralText("");

        for (int i = 0; i < playerNameHistory.size(); i++) {
            switch (playerNameHistory.get(i)) {
                case "name": {
                    i++;
                    //If the person has used their current name in previous name changes, this prevents them from also turning green
                    if (playerNameHistory.get(i).toLowerCase().equals(player[0].toLowerCase()) && (i >= playerNameHistory.size()-3)) {
                        if (i == 1){output.append(new LiteralText("§r§l"+playerNameHistory.get(i))
                                .styled(style -> style.withColor(TextColor.fromRgb(0x6ED878)))
                                .append(new LiteralText("§r  (Original name)")
                                        .styled(style -> style.withColor(TextColor.fromRgb(0xBFB8D5)))));}

                        else output.append(new LiteralText("\n§r§l"+playerNameHistory.get(i))
                                .styled(style -> style.withColor(TextColor.fromRgb(0x6ED878))));
                    }
                    else {
                        if (i == 1) {//First name in list
                            output.append(new LiteralText("§r"+playerNameHistory.get(i))
                                    .styled(style -> style.withColor(TextColor.fromRgb(0x67AAEC)))
                                    .append(new LiteralText("§r  (Original name)")
                                            .styled(style -> style.withColor(TextColor.fromRgb(0xBFB8D5)))));}

                        else {output.append(new LiteralText("\n§r"+playerNameHistory.get(i)).styled(style -> style.withColor(TextColor.fromRgb(0x67AAEC))));}
                    }
                    break;
                }
                case "changedToAt": {
                    i++;
                    DateFormat simple = new SimpleDateFormat("dd.MM.yyyy • HH:mm");
                    Date result = new Date(Long.parseLong(playerNameHistory.get(i)));
                    output.append(new LiteralText("  §r§o"+simple.format(result))
                            .styled(style -> style.withColor(TextColor.fromRgb(0x4A143F))));
                    break;
                }
            }
        }
        return output;
    }
}
