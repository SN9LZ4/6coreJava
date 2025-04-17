package tn.esprit.entities;

import java.sql.Date;
import java.time.LocalDate;

public class Film {
    private int id;
    private String titre;
    private String directeur;
    private float note;
    private String genre;
private String description;
    private Date dateDebut;
    private Date dateFin;
    private int duree;
    private String image;
    private int nbusers;

    // Constructeur complet
    public Film(int id, String titre, String directeur, float note, String genre, String description, Date dateDebut, Date dateFin, int duree) {
        this.titre = this.titre;
        this.directeur = this.directeur;
        this.note = this.note;
        this.genre = this.genre;
        this.description = this.description;
        this.dateDebut = this.dateDebut;
        this.dateFin = this.dateFin;
        this.duree = Integer.parseInt(String.valueOf(this.duree));
        this.image = image;
    }



    public Film(float note, int nbusers, String titre, String genre, String directeur, String description, Date dateDebut, Date dateFin, int i, String image) {
        this.duree = duree;
        this.nbusers = nbusers;
        this.titre = titre;
        this.genre = genre;
        this.directeur = directeur;
        this.description = description;
    }

    public Film(int id, float note, int nbUsers, String titre, String genre, String directeur, String description, Date dateDebut, Date dateFin, int duree, String image) {

    }

    public Film() {

    }



    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDirecteur() { return directeur; }
    public void setDirecteur(String directeur) { this.directeur = directeur; }

    public float getNote() { return note; }
    public void setNote(float note) { this.note = note; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date  dateFin) { this.dateFin = dateFin; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getNbusers() { return nbusers; }
    public void setNbusers(int nbusers) { this.nbusers = nbusers; }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", directeur='" + directeur + '\'' +
                ", note=" + note +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut='" + dateDebut + '\'' +
                ", dateFin='" + dateFin + '\'' +
                ", duree=" + duree +
                ", image='" + image + '\'' +
                ", nbusers=" + nbusers +
                '}';
    }
}
