package tn.esprit.entities;
import java.util.HashSet;
import java.util.Set;
public class Salle {
    private int id,nbr_places;
    private String nom_salle,Type_salle,Etat_salle,image_salle;
    private Set<Film> films = new HashSet<>();

    public Salle(int id, int nbr_places, String nom_salle, String type_salle, String etat_salle, String image_salle, Set<Film> films) {
        this.id = id;
        this.nbr_places = nbr_places;
        this.nom_salle = nom_salle;
        Type_salle = type_salle;
        Etat_salle = etat_salle;
        this.image_salle = image_salle;
    }

    public Salle(Set<Film> films, int nbr_places, String nom_salle, String type_salle, String etat_salle, String image_salle) {
        this.films = films;
        this.nbr_places = nbr_places;
        this.nom_salle = nom_salle;
        Type_salle = type_salle;
        Etat_salle = etat_salle;
        this.image_salle = image_salle;
    }

    public Salle() {
    }

    public int getNbr_places() {
        return nbr_places;
    }

    public void setNbr_places(int nbr_places) {
        this.nbr_places = nbr_places;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_salle() {
        return nom_salle;
    }

    public void setNom_salle(String nom_salle) {
        this.nom_salle = nom_salle;
    }

    public String getType_salle() {
        return Type_salle;
    }

    public void setType_salle(String type_salle) {
        Type_salle = type_salle;
    }

    public String getEtat_salle() {
        return Etat_salle;
    }

    public void setEtat_salle(String etat_salle) {
        Etat_salle = etat_salle;
    }

    public String getImage_salle() {
        return image_salle;
    }

    public void setImage_salle(String image_salle) {
        this.image_salle = image_salle;
    }

    public Set<Film> getFilms() {
        return films;
    }

    public void setFilms(Set<Film> films) {
        this.films = films;
    }

    @Override
    public String toString() {
        return "Salle{" +
                "nbr_places=" + nbr_places +
                ", nom_salle='" + nom_salle + '\'' +
                ", Type_salle='" + Type_salle + '\'' +
                ", Etat_salle='" + Etat_salle + '\'' +
                ", image_salle='" + image_salle + '\'' +
                ", films=" + films +
                ", id=" + id +
                '}';
    }
}