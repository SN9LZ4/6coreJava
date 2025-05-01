package tn.esprit.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import tn.esprit.entities.Film;

import java.util.ArrayList;
import java.util.List;

public class TMDBService {
    private static final String API_KEY = "e0918722985fec5eba58312c34144a07";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String DEFAULT_POSTER = "https://via.placeholder.com/500x750?text=No+Poster";
    private final OkHttpClient client = new OkHttpClient();

    public List<Film> getSimilarMovies(String movieTitle) {
        List<Film> similarMovies = new ArrayList<>();

        try {
            // 1. Rechercher l'ID du film
            String searchUrl = BASE_URL + "/search/movie?api_key=" + API_KEY + "&query=" + movieTitle.replace(" ", "%20");
            Request searchRequest = new Request.Builder().url(searchUrl).build();

            try (Response searchResponse = client.newCall(searchRequest).execute()) {
                if (!searchResponse.isSuccessful()) {
                    return similarMovies;
                }

                JSONObject searchResult = new JSONObject(searchResponse.body().string());
                JSONArray results = searchResult.getJSONArray("results");

                if (results.length() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    int movieId = firstResult.optInt("id", -1);

                    if (movieId == -1) {
                        return similarMovies;
                    }

                    // 2. Obtenir les films similaires
                    String similarUrl = BASE_URL + "/movie/" + movieId + "/similar?api_key=" + API_KEY;
                    Request similarRequest = new Request.Builder().url(similarUrl).build();

                    try (Response similarResponse = client.newCall(similarRequest).execute()) {
                        if (!similarResponse.isSuccessful()) {
                            return similarMovies;
                        }

                        JSONObject similarResult = new JSONObject(similarResponse.body().string());
                        JSONArray similarMoviesJson = similarResult.optJSONArray("results");

                        if (similarMoviesJson == null) {
                            return similarMovies;
                        }

                        // 3. Convertir en objets Film (limit à 5 films)
                        for (int i = 0; i < Math.min(similarMoviesJson.length(), 5); i++) {
                            JSONObject movieJson = similarMoviesJson.getJSONObject(i);
                            Film film = new Film();

                            // Gestion des champs potentiellement manquants
                            film.setTitre(movieJson.optString("title", "Titre inconnu"));
                            film.setGenre(getGenresFromIds(movieJson.optJSONArray("genre_ids")));

                            // Gestion du poster_path
                            String posterPath = movieJson.optString("poster_path", "");
                            film.setImage(posterPath.isEmpty() ? DEFAULT_POSTER : "https://image.tmdb.org/t/p/w500" + posterPath);

                            // Gestion de la note
                            film.setNote((float) movieJson.optDouble("vote_average", 0.0));

                            similarMovies.add(film);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return similarMovies;
    }

    private String getGenresFromIds(JSONArray genreIds) {
        if (genreIds == null || genreIds.length() == 0) {
            return "Divers";
        }


        return "Divers"; // Temporaire
    }
}