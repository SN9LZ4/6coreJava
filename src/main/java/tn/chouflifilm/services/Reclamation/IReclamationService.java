package tn.chouflifilm.services.Reclamation;

import tn.chouflifilm.entities.Reclamation;

import java.sql.SQLException;
import java.util.List;

public interface IReclamationService {
    public List<Reclamation> getListReclamations() throws SQLException;
    public void ajouterReclamation (Reclamation reclamation) throws SQLException;
    public  void supprimerReclamation (int id) throws SQLException;
}
