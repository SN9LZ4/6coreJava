package tn.esprit.services;

import tn.esprit.entities.Film;
import tn.esprit.entities.Salle;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.*;

public class SalleService {

    private final Connection cnx;

    public SalleService() {
        cnx = MyDataBase.getInstance().getCnx();
    }
    public List<Salle> getSallesParFilm(int filmId) throws SQLException {
        List<Salle> salles = new ArrayList<>();
        String req = """
    SELECT s.* FROM salle s
    JOIN salle_film sf ON s.id = sf.salle_id
    WHERE sf.film_id = ?
""";

        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, filmId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Salle salle = new Salle();
            salle.setNom_salle(rs.getString("nom_salle"));
            salle.setType_salle(rs.getString("type_salle"));
            salle.setEtat_salle(rs.getString("etat_salle"));
            salle.setNbr_places(rs.getInt("nbr_places"));
            salle.setImage_salle(rs.getString("image_salle"));
            // Ajoute d’autres champs si tu en as
            salles.add(salle);
        }
        return salles;
    }
    // ➕ Ajouter une salle avec ses films
    public void ajouter(Salle salle) {
        String sql = "INSERT INTO salle (nom_salle, nbr_places, type_salle, etat_salle, image_salle) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, salle.getNom_salle());
            ps.setInt(2, salle.getNbr_places());
            ps.setString(3, salle.getType_salle());
            ps.setString(4, salle.getEtat_salle());
            ps.setString(5, salle.getImage_salle());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int salleId = rs.getInt(1);
                    salle.setId(salleId);

                    if (salle.getFilms() != null && !salle.getFilms().isEmpty()) {
                        String insertJoinSql = "INSERT INTO salle_film (salle_id, film_id) VALUES (?, ?)";
                        try (PreparedStatement psJoin = cnx.prepareStatement(insertJoinSql)) {
                            for (Film film : salle.getFilms()) {
                                psJoin.setInt(1, salleId);
                                psJoin.setInt(2, film.getId());
                                psJoin.executeUpdate();
                            }
                        }
                    }
                    System.out.println("✅ Salle ajoutée avec films !");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔄 Modifier une salle et ses films
    public void modifier(Salle salle) {
        String sql = "UPDATE salle SET nom_salle=?, nbr_places=?, type_salle=?, etat_salle=?, image_salle=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, salle.getNom_salle());
            ps.setInt(2, salle.getNbr_places());
            ps.setString(3, salle.getType_salle());
            ps.setString(4, salle.getEtat_salle());
            ps.setString(5, salle.getImage_salle());
            ps.setInt(6, salle.getId());
            ps.executeUpdate();

            try (PreparedStatement psDelete = cnx.prepareStatement("DELETE FROM salle_film WHERE salle_id=?")) {
                psDelete.setInt(1, salle.getId());
                psDelete.executeUpdate();
            }

            if (salle.getFilms() != null && !salle.getFilms().isEmpty()) {
                String insertJoinSql = "INSERT INTO salle_film (salle_id, film_id) VALUES (?, ?)";
                try (PreparedStatement psJoin = cnx.prepareStatement(insertJoinSql)) {
                    for (Film film : salle.getFilms()) {
                        psJoin.setInt(1, salle.getId());
                        psJoin.setInt(2, film.getId());
                        psJoin.executeUpdate();
                    }
                }
            }

            System.out.println("✅ Salle modifiée avec films !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ❌ Supprimer une salle et ses associations
    public void supprimer(int id) {
        try {
            try (PreparedStatement psDelete = cnx.prepareStatement("DELETE FROM salle_film WHERE salle_id=?")) {
                psDelete.setInt(1, id);
                psDelete.executeUpdate();
            }

            try (PreparedStatement ps = cnx.prepareStatement("DELETE FROM salle WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            System.out.println("✅ Salle supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 📄 Récupérer toutes les salles avec leurs films
    public List<Salle> recuperer() {
        List<Salle> list = new ArrayList<>();
        String sql = "SELECT * FROM salle";

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Salle s = new Salle();
                s.setId(rs.getInt("id"));
                s.setNom_salle(rs.getString("nom_salle"));
                s.setNbr_places(rs.getInt("nbr_places"));
                s.setType_salle(rs.getString("type_salle"));
                s.setEtat_salle(rs.getString("etat_salle"));
                s.setImage_salle(rs.getString("image_salle"));
                s.setFilms(recupererFilmsParSalle(s.getId()));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🔍 Récupérer les films liés à une salle
    private Set<Film> recupererFilmsParSalle(int salleId) {
        Set<Film> films = new HashSet<>();
        String sql = "SELECT f.* FROM film f JOIN salle_film sf ON f.id = sf.film_id WHERE sf.salle_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.setId(rs.getInt("id"));
                    film.setTitre(rs.getString("titre"));
                    film.setGenre(rs.getString("genre"));
                    film.setDirecteur(rs.getString("directeur"));
                    film.setNote(rs.getFloat("note"));
                    film.setNbusers(rs.getInt("nbusers"));
                    film.setDescription(rs.getString("description"));
                    film.setDateDebut(rs.getDate("date_debut"));
                    film.setDateFin(rs.getDate("date_fin"));
                    film.setDuree(rs.getInt("duree"));
                    film.setImage(rs.getString("image_film"));
                    films.add(film);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return films;
    }

    // 🎯 Récupérer toutes les salles liées à un film donné
    public List<Salle> getSallesByFilm(Film film) {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT s.* FROM salle s JOIN salle_film sf ON s.id = sf.salle_id WHERE sf.film_id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, film.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Salle salle = new Salle();
                salle.setId(rs.getInt("id"));
                salle.setNom_salle(rs.getString("nom_salle"));
                salle.setType_salle(rs.getString("type_salle"));
                salle.setEtat_salle(rs.getString("etat_salle"));
                salle.setNbr_places(rs.getInt("nbr_places"));
                salle.setImage_salle(rs.getString("image_salle"));
                salles.add(salle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salles;
    }

}
