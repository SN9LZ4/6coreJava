package com.cinema.model;

/**
 * Salle entity representing a cinema room
 */
public class Salle {
    private int id;
    private String nomSalle;
    private int nbrPlaces;
    private String typeSalle;
    private String etatSalle;
    private String imageSalle;
    
    public Salle(int id, String nomSalle) {
        this.id = id;
        this.nomSalle = nomSalle;
    }
    
    public Salle(int id, String nomSalle, int nbrPlaces, String typeSalle, String etatSalle, String imageSalle) {
        this.id = id;
        this.nomSalle = nomSalle;
        this.nbrPlaces = nbrPlaces;
        this.typeSalle = typeSalle;
        this.etatSalle = etatSalle;
        this.imageSalle = imageSalle;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNomSalle() {
        return nomSalle;
    }
    
    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }
    
    public int getNbrPlaces() {
        return nbrPlaces;
    }
    
    public void setNbrPlaces(int nbrPlaces) {
        this.nbrPlaces = nbrPlaces;
    }
    
    public String getTypeSalle() {
        return typeSalle;
    }
    
    public void setTypeSalle(String typeSalle) {
        this.typeSalle = typeSalle;
    }
    
    public String getEtatSalle() {
        return etatSalle;
    }
    
    public void setEtatSalle(String etatSalle) {
        this.etatSalle = etatSalle;
    }
    
    public String getImageSalle() {
        return imageSalle;
    }
    
    public void setImageSalle(String imageSalle) {
        this.imageSalle = imageSalle;
    }
    
    @Override
    public String toString() {
        return nomSalle;
    }
}
