package tn.esprit.entities;

import java.util.Date;
import java.util.Objects;

public class commentaire {
    private int id;
    private String contenu_com;
    private Date date;
    private article article;
    private User user;

    // Fix the empty constructor
    public commentaire() {
    }

    // Fix the constructor with all parameters
    public commentaire(int id, String contenu_com, Date date, article article, User user) {
        this.id = id;
        this.contenu_com = contenu_com;
        this.date = date;
        this.article = article;
        this.user = user;
    }

    // Add this constructor for new comments
    public commentaire(String contenu_com, User user, article article, Date date) {
        this.contenu_com = contenu_com;
        this.user = user;
        this.article = article;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenuCom() {
        return contenu_com;
    }

    public void setContenuCom(String contenu_com) {
        this.contenu_com = contenu_com;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public article getArticle() {
        return article;
    }

    public void setArticle(article article) {
        this.article = article;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", contenu_com='" + contenu_com + '\'' +
                ", date=" + date +
                ", article=" + (article != null ? article.getId() : "null") +
                ", user=" + (user != null ? user.getUser_id() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof commentaire)) return false;
        commentaire that = (commentaire) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
