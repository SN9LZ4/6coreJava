package tn.esprit.services;

import tn.esprit.entities.article;
import tn.esprit.Tools.MyData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ArticleService implements IService<article> {
    private final Connection cnx;
    public ArticleService() {
        cnx = MyData.getInstance().getCnx();
    }

    @Override
    public void add(article article) throws ServiceException {
        if (article.getDate_publication() == null) {
            throw new ServiceException("date_publication cannot be null");
        }
        String sql = "INSERT INTO article (titre, date_publication, contenu, image, categorie, total_rating, rating_count, likes, dislikes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, article.getTitre());
            pst.setDate(2, new java.sql.Date(article.getDate_publication().getTime()));  // Corrected line
            pst.setString(3, article.getContenu());
            pst.setString(4, article.getImage());
            pst.setString(5, article.getCategorie());
            pst.setDouble(6, article.getTotal_rating());

            if (article.getRating_count() != null) {
                pst.setInt(7, article.getRating_count());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setInt(8, article.getLikes());
            pst.setInt(9, article.getDislikes());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    article.setId(rs.getInt(1));
                    System.out.println("✅ Article ajouté avec ID : " + article.getId());
                }
            } else {
                throw new ServiceException("Failed to add article.");
            }
        } catch (SQLException e) {
            throw new ServiceException("Error adding article", e);
        }
    }

    @Override
    public void edit(article article) throws ServiceException{
        if (article.getDate_publication() == null) {
            throw new ServiceException("date_publication cannot be null");
        }
        String sql = "UPDATE article SET titre=?, date_publication=?, contenu=?, image=?, categorie=?, total_rating=?, rating_count=?, likes=?, dislikes=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, article.getTitre());
            pst.setDate(2, new java.sql.Date(article.getDate_publication().getTime()));
            pst.setString(3, article.getContenu());
            pst.setString(4, article.getImage());
            pst.setString(5, article.getCategorie());
            pst.setDouble(6, article.getTotal_rating());

            if (article.getRating_count() != null) {
                pst.setInt(7, article.getRating_count());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setInt(8, article.getLikes());
            pst.setInt(9, article.getDislikes());
            pst.setInt(10, article.getId());

            int rows = pst.executeUpdate();
            if (rows <= 0) {
                throw new ServiceException("Failed to edit article with ID: " + article.getId());
            }
            System.out.println("✅ Article mis à jour avec succès.");
        } catch (SQLException e) {
            throw new ServiceException("Error editing article", e);
        }
    }

    @Override
    public void remove(article article)  throws ServiceException{
        String sql = "DELETE FROM article WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, article.getId());
            int rows = pst.executeUpdate();
            if (rows <= 0) {
                throw new ServiceException("Failed to delete article with ID: " + article.getId());
            }
            System.out.println("✅ Article supprimé avec succès.");
        } catch (SQLException e) {
            throw new ServiceException("Error deleting article", e);
        }
    }

    @Override
    public List<article> recuperer() throws ServiceException {
        List<article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article";
        try (PreparedStatement pst = cnx.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Date datePublication = rs.getDate("date_publication");
                article art = new article(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        new java.util.Date(datePublication.getTime()),
                        rs.getString("contenu"),
                        rs.getString("image"),
                        rs.getDouble("total_rating"),
                        rs.getString("categorie"),
                        rs.getObject("rating_count") != null ? rs.getInt("rating_count") : null,
                        rs.getInt("likes"),
                        rs.getInt("dislikes")
                );
                articles.add(art);
            }
        } catch (SQLException e) {
            throw new ServiceException("Error retrieving articles", e);
        }
        return articles;
    }
}
