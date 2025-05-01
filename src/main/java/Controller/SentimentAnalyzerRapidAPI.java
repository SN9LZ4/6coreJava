package Controller;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SentimentAnalyzerRapidAPI {

    private static final String API_URL = "https://text-analysis12.p.rapidapi.com/sentiment-analysis/api/v1.1";
    private static final String RAPID_API_KEY = "bf5872a568mshb097282e0c9051dp1a8e6ejsndc5ccf45a9c7";
    private static final String RAPID_API_HOST = "text-analysis12.p.rapidapi.com";
    
    // Mots positifs en français
    private static final String[] POSITIVE_WORDS = {
        "excellent", "super", "génial", "heureux", "content", "bravo", "merci",
        "bien", "parfait", "aime", "formidable", "extraordinaire", "magnifique",
        "agréable", "plaisir", "joie", "réussite", "succès", "adore", "fantastique",
        "impressionnant", "incroyable", "satisfait", "félicitations", "positif"
    };
    
    // Mots négatifs en français
    private static final String[] NEGATIVE_WORDS = {
        "mauvais", "nul", "horrible", "déteste", "pire", "problème", "erreur",
        "bug", "défaut", "mal", "catastrophe", "terrible", "déçu", "décevant",
        "triste", "colère", "fâché", "ennuyeux", "difficile", "échec", "pénible",
        "insupportable", "médiocre", "insuffisant", "négatif", "déplorable"
    };

    public static String analyzeSentiment(String text) {
        try {
            // Essayer d'abord l'API RapidAPI
            return analyzeWithRapidAPI(text);
        } catch (Exception e) {
            System.err.println("Erreur avec RapidAPI: " + e.getMessage());
            // Fallback sur l'analyse locale si l'API échoue
            return analyzeLocally(text);
        }
    }
    
    private static String analyzeWithRapidAPI(String text) throws Exception {
        // Préparer les données pour l'API
        String jsonInputString = String.format("{\"language\":\"french\",\"text\":\"%s\"}",
                text.replace("\"", "\\\""));

        // Créer la connexion
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // Ajouter les en-têtes requis
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-RapidAPI-Key", RAPID_API_KEY);
        conn.setRequestProperty("X-RapidAPI-Host", RAPID_API_HOST);

        // Activer l'envoi des données
        conn.setDoOutput(true);
        conn.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));

        // Vérifier le code de réponse
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Erreur API: HTTP " + responseCode);
        }

        // Lire la réponse
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        // Parser la réponse JSON
        JSONObject jsonResponse = new JSONObject(response.toString());
        String sentiment = jsonResponse.getJSONObject("sentiment").getString("label");
        double score = jsonResponse.getJSONObject("sentiment").getDouble("score");

        // Convertir le sentiment en format lisible
        return formatSentiment(sentiment, score);
    }
    
    private static String analyzeLocally(String text) {
        if (text == null || text.isEmpty()) {
            return "😐 Neutre";
        }
        
        text = text.toLowerCase();
        int positiveCount = 0;
        int negativeCount = 0;
        
        // Compter les mots positifs
        for (String word : POSITIVE_WORDS) {
            if (text.contains(word)) {
                positiveCount++;
            }
        }
        
        // Compter les mots négatifs
        for (String word : NEGATIVE_WORDS) {
            if (text.contains(word)) {
                negativeCount++;
            }
        }
        
        // Calculer le score
        double totalWords = positiveCount + negativeCount;
        if (totalWords == 0) {
            return "😐 Neutre";
        }
        
        double score = positiveCount / totalWords;
        
        // Déterminer le sentiment
        if (positiveCount > negativeCount) {
            return score > 0.75 ? "😊 Très positif" : "🙂 Positif";
        } else if (negativeCount > positiveCount) {
            return score < 0.25 ? "😠 Très négatif" : "🙁 Négatif";
        } else {
            return "😐 Neutre";
        }
    }

    private static String formatSentiment(String sentiment, double score) {
        switch (sentiment.toLowerCase()) {
            case "positive":
                return score > 0.75 ? "😊 Très positif" : "🙂 Positif";
            case "negative":
                return score > 0.75 ? "😠 Très négatif" : "🙁 Négatif";
            case "neutral":
                return "😐 Neutre";
            default:
                return "😐 Indéterminé";
        }
    }
 
    public static void main(String[] args) {
        String[] testTexts = {
            "Je suis très content de cette application !",
            "C'est vraiment nul, je déteste ça.",
            "C'est un article intéressant.",
            "Excellent travail, bravo à toute l'équipe !",
            "Il y a beaucoup de bugs et de problèmes."
        };
        
        System.out.println("Test d'analyse de sentiment:");
        for (String text : testTexts) {
            System.out.println("\nTexte: " + text);
            System.out.println("Résultat: " + analyzeSentiment(text));
        }
    }
}