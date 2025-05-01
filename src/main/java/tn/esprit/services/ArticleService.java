package tn.esprit.services;

import tn.esprit.entities.article;
import tn.esprit.Tools.MyData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class ArticleService implements IService<article> {
    private  final Connection cnx;
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




        // Rest of your methods remain the same, but remove 'static' from updateRating and updateLikes methods
        public void updateRating(int articleId, int rating) throws ServiceException {
            String sql = "UPDATE article SET total_rating = ((total_rating * rating_count) + ?) / (rating_count + 1), " +
                    "rating_count = rating_count + 1 WHERE id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(sql)) {
                pst.setInt(1, rating);
                pst.setInt(2, articleId);
                pst.executeUpdate();
            } catch (SQLException e) {
                throw new ServiceException("Error updating article rating", e);
            }
        }

        public void updateLikes(int articleId, boolean isLike) throws ServiceException {
            String sql = isLike ?
                    "UPDATE article SET likes = likes + 1 WHERE id = ?" :
                    "UPDATE article SET dislikes = dislikes + 1 WHERE id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(sql)) {
                pst.setInt(1, articleId);
                pst.executeUpdate();
            } catch (SQLException e) {
                throw new ServiceException("Error updating article likes/dislikes", e);
            }
        }

    public article getById(int id) throws ServiceException {
        String sql = "SELECT * FROM article WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(sql))
        {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                article a = new article();
                a.setId(rs.getInt("id"));
                a.setTitre(rs.getString("titre"));
                a.setContenu(rs.getString("contenu"));
                a.setCategorie(rs.getString("categorie"));
                a.setImage(rs.getString("image"));
                a.setDate_publication(rs.getDate("date_publication"));
                a.setTotal_rating(rs.getDouble("total_rating"));
                a.setRating_count(rs.getInt("rating_count"));
                a.setLikes(rs.getInt("likes"));
                a.setDislikes(rs.getInt("dislikes"));
                return a;
            } else {
                throw new ServiceException("Aucun article trouvé avec l'ID : " + id);
            }

        } catch (SQLException e) {
            throw new ServiceException("Erreur lors de la récupération de l'article : " + e.getMessage());
        }
    }


}

