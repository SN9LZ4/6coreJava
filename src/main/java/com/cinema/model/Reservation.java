package com.cinema.model;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int userId;
    private Integer filmId;
    private Integer salleId;
    private Integer associationId;
    private LocalDate dateReservation;
    private String typePlace;
    private int nombrePlaces;
    private String status;
    private String titre;
    private String selectedSeats;
    private String salleNumero;
    private double prix;

    public Reservation() {}

    public Reservation(int userId, Integer filmId, Integer salleId, Integer associationId, 
                      LocalDate dateReservation, String typePlace, int nombrePlaces, 
                      String status, String titre, String selectedSeats) {
        this.userId = userId;
        this.filmId = filmId;
        this.salleId = salleId;
        this.associationId = associationId;
        this.dateReservation = dateReservation;
        this.typePlace = typePlace;
        this.nombrePlaces = nombrePlaces;
        this.status = status;
        this.titre = titre;
        this.selectedSeats = selectedSeats;
        this.prix = ("VIP".equals(typePlace) ? 25.0 : 15.0) * nombrePlaces;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getFilmId() { return filmId; }
    public void setFilmId(Integer filmId) { this.filmId = filmId; }

    public Integer getSalleId() { return salleId; }
    public void setSalleId(Integer salleId) { this.salleId = salleId; }

    public Integer getAssociationId() { return associationId; }
    public void setAssociationId(Integer associationId) { this.associationId = associationId; }

    public LocalDate getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDate dateReservation) { this.dateReservation = dateReservation; }

    public String getTypePlace() { return typePlace; }
    public void setTypePlace(String typePlace) { 
        this.typePlace = typePlace;
        // Update price when type changes
        if (this.nombrePlaces > 0) {
            this.prix = ("VIP".equals(typePlace) ? 25.0 : 15.0) * this.nombrePlaces;
        }
    }

    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { 
        this.nombrePlaces = nombrePlaces;
        // Update price when number of places changes
        if (this.typePlace != null) {
            this.prix = ("VIP".equals(this.typePlace) ? 25.0 : 15.0) * nombrePlaces;
        }
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(String selectedSeats) { this.selectedSeats = selectedSeats; }

    public String getSalleNumero() { return salleNumero; }
    public void setSalleNumero(String salleNumero) { this.salleNumero = salleNumero; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
}
