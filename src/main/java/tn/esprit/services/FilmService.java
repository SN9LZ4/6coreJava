package tn.esprit.services;

import tn.esprit.entities.Film;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmService implements IService<Film> {
    private final Connection cnx;
    private String sql;

    public FilmService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Film film) throws SQLException {
        sql = "INSERT INTO film (titre, directeur, note, genre, description, date_debut, date_fin, duree, image_film, nbusers) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, film.getTitre());
            pst.setString(2, film.getDirecteur());
            pst.setFloat(3, film.getNote());
            pst.setString(4, film.getGenre());
            pst.setString(5, film.getDescription());
            pst.setDate(6, film.getDateDebut());
            pst.setDate(7, film.getDateFin());
            pst.setInt(8, film.getDuree());
            pst.setString(9, film.getImage());
            pst.setInt(10, film.getNbusers());

            pst.executeUpdate();
            System.out.println("✅ Film ajouté avec succès !");
        }
    }

    @Override
    public void modifier(int id, String nom) throws SQLException {
        // Optionnel : méthode héritée de l'interface, peut être ignorée si inutilisée
    }

    public void edit(Film film) throws SQLException {
        if (film.getDateDebut() == null || film.getDateFin() == null) {
            throw new SQLException("Les dates ne peuvent pas être nulles.");
        }

        String sql = "UPDATE film SET titre=?, directeur=?, note=?, genre=?, description=?, date_debut=?, date_fin=?, duree=?, image_film=? WHERE id=?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, film.getTitre());
            pst.setString(2, film.getDirecteur());
            pst.setFloat(3, film.getNote());
            pst.setString(4, film.getGenre());
            pst.setString(5, film.getDescription());
            pst.setDate(6, film.getDateDebut());
            pst.setDate(7, film.getDateFin());
            pst.setInt(8, film.getDuree());
            pst.setString(9, film.getImage());
            pst.setInt(10, film.getId());

            int rows = pst.executeUpdate();
            if (rows <= 0) {
                throw new SQLException("❌ Aucune mise à jour effectuée. Film introuvable.");
            }

            System.out.println("✅ Film mis à jour avec succès.");
        }
    }

    @Override
    public void supprimer(Film film) throws SQLException {
        remove(film);
    }

    public void remove(Film film) throws SQLException {
        String sql = "DELETE FROM film WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, film.getId());
            int rows = pst.executeUpdate();
            if (rows <= 0) {
                throw new SQLException("❌ Suppression échouée, film introuvable.");
            }
            System.out.println("✅ Film supprimé avec succès.");
        }
    }

    public List<Film> recuperer() throws SQLException {
        List<Film> films = new ArrayList<>();
        String query = "SELECT * FROM film";

        try (PreparedStatement pst = cnx.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                films.add(mapFilm(rs));
            }
        }

        return films;
    }

    public List<Film> rechercher(String titre, String directeur, String genre, Double noteMin) {
        List<Film> films = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM film WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (titre != null && !titre.isEmpty()) {
            query.append(" AND titre LIKE ?");
            params.add("%" + titre + "%");
        }

        if (directeur != null && !directeur.isEmpty()) {
            query.append(" AND directeur LIKE ?");
            params.add("%" + directeur + "%");
        }

        if (genre != null && !genre.isEmpty()) {
            query.append(" AND genre = ?");
            params.add(genre);
        }

        if (noteMin != null && noteMin > 0) {
            query.append(" AND note >= ?");
            params.add(noteMin);
        }

        try (PreparedStatement pst = cnx.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    films.add(mapFilm(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche films : " + e.getMessage());
        }

        return films;
    }

    // 🔧 Utilitaire pour transformer une ligne SQL en Film
    private Film mapFilm(ResultSet rs) throws SQLException {
        Film film = new Film();

        film.setId(rs.getInt("id"));
        film.setTitre(rs.getString("titre"));
        film.setDirecteur(rs.getString("directeur"));

        float note = rs.getFloat("note");
        film.setNote(rs.wasNull() ? 0 : note);

        film.setGenre(rs.getString("genre"));
        film.setDescription(rs.getString("description"));

        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) film.setDateDebut(dateDebut);

        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) film.setDateFin(dateFin);

        int duree = rs.getInt("duree");
        film.setDuree(rs.wasNull() ? 0 : duree);

        film.setImage(rs.getString("image_film"));

        return film;
    }
}
