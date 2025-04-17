package com.cinema.model;

import java.time.LocalDate;

/**
 * Film entity representing movie information
 */
public class Film {
    private int id;
    private String titre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    
    public Film(int id, String titre) {
        this.id = id;
        this.titre = titre;
    }
    
    public Film(int id, String titre, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    @Override
    public String toString() {
        return titre;
    }
}
