package tn.esprit.services;

import tn.esprit.Tools.MyData;
import tn.esprit.entities.User;
import tn.esprit.entities.article;
import tn.esprit.entities.commentaire;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class commentaireService {
    private final Connection cnx;

    public commentaireService() {
        cnx = MyData.getInstance().getCnx();
    }

    public void add(commentaire c) throws ServiceException {
        String sql = "INSERT INTO commentaire (contenu_com, date, article_id, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, c.getContenuCom());
            pst.setTimestamp(2, new java.sql.Timestamp(c.getDate().getTime()));
            pst.setInt(3, c.getArticle().getId());
            pst.setInt(4, c.getUser().getUser_id());
            
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new ServiceException("La création du commentaire a échoué.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    c.setId(generatedKeys.getInt(1));
                } else {
                    throw new ServiceException("La création du commentaire a échoué, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de l'ajout du commentaire: " + e.getMessage(), e);
        }
    }

    public void edit(commentaire c, User user) throws ServiceException {
        if (c.getUser().getUser_id() != user.getUser_id()) {
            throw new ServiceException("Vous ne pouvez modifier que vos propres commentaires.");
        }
        String sql = "UPDATE commentaire SET contenu_com = ?, date = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, c.getContenuCom());
            pst.setDate(2, new java.sql.Date(c.getDate().getTime()));
            pst.setInt(3, c.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de la modification du commentaire", e);
        }
    }

    public void delete(commentaire c, User user) throws ServiceException {
        if (c.getUser().getUser_id() != user.getUser_id() && !user.isAdmin()) {
            throw new ServiceException("Seul l'auteur ou un administrateur peut supprimer ce commentaire.");
        }
        String sql = "DELETE FROM commentaire WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, c.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de la suppression du commentaire", e);
        }
    }

    public List<commentaire> getCommentairesByArticle(article a) throws ServiceException {
        List<commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT c.*, u.nom, u.prenom FROM commentaire c " +
                "JOIN user u ON c.user_id = u.id WHERE c.article_id = ? ORDER BY c.date DESC";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, a.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                commentaire c = new commentaire();
                c.setId(rs.getInt("id"));
                c.setContenuCom(rs.getString("contenu_com"));
                c.setDate(rs.getDate("date"));
                c.setArticle(a);
                a.addCommentaire(c); // 🔗 importante pour la relation bidirectionnelle en mémoire

                User u = new User();
                u.setUser_id(rs.getInt("user_id"));
                u.setNom(rs.getString("nom")); // 👈 important
                u.setPrenom(rs.getString("prenom")); // 👈 important
                c.setUser(u);

                commentaires.add(c);
            }
        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de la récupération des commentaires", e);
        }

        return commentaires;
    }

}