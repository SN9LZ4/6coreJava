package com.cinema.model;

/**
 * Association entity representing a relationship between a film and a salle
 */
public class Association {
    private int id;
    private String name;
    private int filmId;
    private int salleId;
    
    public Association(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Association(int id, String name, int filmId, int salleId) {
        this.id = id;
        this.name = name;
        this.filmId = filmId;
        this.salleId = salleId;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getFilmId() {
        return filmId;
    }
    
    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }
    
    public int getSalleId() {
        return salleId;
    }
    
    public void setSalleId(int salleId) {
        this.salleId = salleId;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
