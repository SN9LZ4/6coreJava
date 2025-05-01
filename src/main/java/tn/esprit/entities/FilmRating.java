package tn.esprit.entities;

import java.util.ArrayList;
import java.util.List;

public class FilmRating {
    private static List<Integer> ratings = new ArrayList<>();
    private static double average = 0.0;

    public static void addRating(int rating) {
        ratings.add(rating);
        calculateAverage();
    }

    private static void calculateAverage() {
        average = ratings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    public static double getAverage() {
        return average;
    }

    public static int getRatingCount() {
        return ratings.size();
    }
}