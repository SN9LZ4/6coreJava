package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ProfanityFilter {
    // Updated API URL with correct endpoint
    private static final String API_URL = "https://api.api-ninjas.com/v1/profanity?text=";
    private static final String API_KEY = "oN5qLFZWorONG8yoP6INww==11t1Y7fTndklciMK";

    public static String filterText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        try {
            String fullUrl = API_URL + java.net.URLEncoder.encode(input, "UTF-8");
            URL url = new URL(fullUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("X-Api-Key", API_KEY);
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();
            System.out.println("HTTP Status: " + status);

            if (status != 200) {
                System.err.println("Erreur API Ninjas: HTTP " + status);
                // Fall back to local filtering instead of returning null
                return censorBadWords(input);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            System.out.println("Réponse brute : " + content);

            JSONObject json = new JSONObject(content.toString());
            boolean hasProfanity = !json.getJSONArray("profanity").isEmpty();

            if (hasProfanity) {
                System.out.println("⚠️ Propos inappropriés détectés.");
                return censorBadWords(input);
            } else {
                return input;
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur d'appel à l'API Ninjas: " + e.getMessage());
            // Fall back to local filtering instead of returning null
            return censorBadWords(input);
        }
    }

    private static final String[] BAD_WORDS = {
            "merde", "con", "connard", "putain", "salope", "enculé", "chiant", "salopard", "abruti",
            // Add more words as needed
            "pute", "bite", "couille", "merdique", "foutre", "cul", "bordel", "connerie",
            "enculer", "niquer", "salaud", "crétin", "débile", "imbécile"
    };

    private static String censorBadWords(String text) {
        if (text == null) return null;

        String censored = text;
        for (String word : BAD_WORDS) {
            // Case-insensitive replacement with word boundaries
            censored = censored.replaceAll("(?i)\\b" + word + "\\b", "*".repeat(word.length()));
        }
        return censored;
    }
}