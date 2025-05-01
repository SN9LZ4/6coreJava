package tn.esprit.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class User {
    private int User_id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String image;
    private String[] roles;
    private int num_telephone;
    private Date DateNaissance;
    private String localisation;
    private int banned;
    private int deleted;
    private String verification_code;
    private String confirmpassword;
    private int banned_at;
    private int nblogin;

    public User() {}

    public User(int User_id, String nom, String prenom, String email, String image, int num_telephone) {
        this.User_id = User_id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.image = image;
        this.num_telephone = num_telephone;
    }

    public User(String nom, String prenom, String email, int num_telephone, Date dateNaissance, String localisation, String image, String password, String confirmpassword) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.num_telephone = num_telephone;
        this.DateNaissance = dateNaissance;
        this.localisation = localisation;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.banned = 0;
        this.deleted = 0;
        this.nblogin = 0;
        this.image = image;
        this.roles = new String[]{"ROLE_USER"};
    }

    public User(int userId, String nom, String prenom, boolean isAdmin) {
        this.User_id = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.roles = new String[] { isAdmin ? "ROLE_ADMIN" : "ROLE_USER" };
        this.banned = 0;
        this.deleted = 0;
        this.nblogin = 0;
    }


    // Getters
    public int getUser_id() { return User_id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getImage() { return image; }
    public String[] getRoles() { return roles; }
    public int getNum_telephone() { return num_telephone; }
    public Date getDateNaissance() { return DateNaissance; }
    public String getLocalisation() { return localisation; }
    public int getBanned() { return banned; }
    public int getDeleted() { return deleted; }
    public String getVerification_code() { return verification_code; }
    public String getConfirmpassword() { return confirmpassword; }
    public int getBanned_at() { return banned_at; }
    public int getNblogin() { return nblogin; }

    // Setters
    public void setUser_id(int user_id) { this.User_id = user_id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setImage(String image) { this.image = image; }
    public void setRoles(String[] roles) { this.roles = roles; }
    public void setNum_telephone(int num_telephone) { this.num_telephone = num_telephone; }
    public void setDateNaissance(Date dateNaissance) { this.DateNaissance = dateNaissance; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public void setBanned(int banned) { this.banned = banned; }
    public void setDeleted(int deleted) { this.deleted = deleted; }
    public void setVerification_code(String verification_code) { this.verification_code = verification_code; }
    public void setConfirmpassword(String confirmpassword) { this.confirmpassword = confirmpassword; }
    public void setBanned_at(int banned_at) { this.banned_at = banned_at; }
    public void setNblogin(int nblogin) { this.nblogin = nblogin; }
    public List<commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public boolean isAdmin() {
        if (this.roles != null) {
            for (String role : this.roles) {
                if (role.equals("ROLE_ADMIN")) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String toString() {
        return "User{" +
                "User_id=" + User_id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", num_telephone=" + num_telephone +
                ", DateNaissance=" + DateNaissance +
                ", localisation='" + localisation + '\'' +
                ", banned=" + banned +
                ", deleted=" + deleted +
                ", verification_code='" + verification_code + '\'' +
                ", confirmpassword='" + confirmpassword + '\'' +
                ", banned_at=" + banned_at +
                ", nblogin=" + nblogin +
                '}';
    }




    public void setUserId(int i) {
    }

    public Object getUserId() {
        return null;
    }

    public void setAdmin(boolean isAdmin) {
        if (isAdmin) {
            this.roles = new String[]{"ROLE_ADMIN"};
        } else {
            this.roles = new String[]{"ROLE_USER"};
        }
    }
    private List<commentaire> commentaires = new ArrayList<>();


}
