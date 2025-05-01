package tn.esprit.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class article {
    private int id;
    private String titre;
    private Date date_publication;
    private String contenu;
    private String image;
    private String categorie;
    private double total_rating;
    private Integer rating_count;
    private int likes;
    private int dislikes;
    private List<commentaire> commentaires = new ArrayList<>();

    // Constructors
    public article(int id, String titre, Date date_publication, String contenu, String image,
                   double total_rating, String categorie, Integer rating_count, int likes, int dislikes) {
        this.id = id;
        this.titre = titre;
        this.date_publication = date_publication;
        this.contenu = contenu;
        this.image = image;
        this.total_rating = total_rating;
        this.categorie = categorie;
        this.rating_count = rating_count;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public article(String titre, Date date_publication, String contenu, String image,
                   double total_rating, String categorie, Integer rating_count, int likes, int dislikes) {
        this(0, titre, date_publication, contenu, image, total_rating, categorie, rating_count, likes, dislikes);
    }

    public article(int articleId) {
    }

    public article(int id, String titre, Date datePublication, String contenu, String image, float totalRating, String movieReview, int ratingCount, int likes, User adminUser) {
    }

    public article() {

    }

    public article(String s, String catégorieA, LocalDate now, String s1, String s2) {
    }

    // Getters and Setters
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

    public Date getDate_publication() {
        return date_publication;
    }

    public void setDate_publication(Date date_publication) {
        this.date_publication = date_publication;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public List<commentaire> getCommentaires() { return commentaires; }
    public void setCommentaires(List<commentaire> commentaires) { this.commentaires = commentaires; }
    public void addCommentaire(commentaire com) {
        this.commentaires.add(com);
        com.setArticle(this);
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public float getTotal_rating() {
        return (float) total_rating;
    }

    public void setTotal_rating(double total_rating) {
        this.total_rating = total_rating;
    }

    public Integer getRating_count() {
        return rating_count;
    }

    public void setRating_count(Integer rating_count) {
        this.rating_count = rating_count;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    // equals and hashCode (based on ID)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof article)) return false;
        article article = (article) o;
        return id == article.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString for debugging and logging
    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", date_publication=" + date_publication +
                ", contenu='" + contenu + '\'' +
                ", image='" + image + '\'' +
                ", categorie='" + categorie + '\'' +
                ", total_rating=" + total_rating +
                ", rating_count=" + rating_count +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
}
