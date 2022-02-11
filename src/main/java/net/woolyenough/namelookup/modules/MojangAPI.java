package net.woolyenough.namelookup.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MojangAPI {
    public static String get_raw_response(String api_url) {
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


    public static String[] get_player_name_and_uuid(String name){
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


    public static String get_player_name_history(String name, String uuid) {

        List<String> playerNameHistory = new ArrayList<>(Arrays.asList(MojangAPI.get_raw_response("user/profiles/" + uuid + "/names").split("[\\[\\]{}\",:]")));
        playerNameHistory.removeAll(Arrays.asList("", null, " ", ":"));
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < playerNameHistory.size(); i++) {
            switch (playerNameHistory.get(i)) {
                case "name": {
                    i++;

                    if (playerNameHistory.get(i).equals(name)) {
                        if (i == 1) output.append("§r§a§l");
                        else output.append("\n§r§a§l");
                    }
                    else {
                        if (i == 1) output.append("§r§b");
                        else output.append("\n§r§b");
                    }

                    output.append(playerNameHistory.get(i));
                    break;
                }
                case "changedToAt": {
                    i++;
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy (HH:mm)");
                    Date result = new Date(Long.parseLong(playerNameHistory.get(i)));
                    output.append("  §r§7").append(simple.format(result));
                    break;
                }
            }
        }
        return output.toString();
    }
}
