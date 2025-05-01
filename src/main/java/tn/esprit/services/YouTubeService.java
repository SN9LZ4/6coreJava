package tn.esprit.services;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YouTubeService {

    private static final String API_KEY = "AIzaSyCYoGyiBrshp3ZoflEIaUv4ljMe9vF4jFA"; // Remplace par ta clé

    public static String getTrailerVideoId(String filmTitle) {
        try {
            String searchQuery = filmTitle + " trailer";
            String urlStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&type=video&q="
                    + java.net.URLEncoder.encode(searchQuery, "UTF-8")
                    + "&key=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");

            if (items.size() > 0) {
                JsonObject video = items.get(0).getAsJsonObject();
                JsonObject id = video.getAsJsonObject("id");
                return id.get("videoId").getAsString(); // ID de la vidéo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
