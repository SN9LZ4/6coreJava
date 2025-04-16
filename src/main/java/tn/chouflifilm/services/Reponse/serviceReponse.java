package tn.chouflifilm.services.Reponse;

import tn.chouflifilm.entities.Reclamation;
import tn.chouflifilm.entities.Reponse;

public class serviceReponse implements IServiceReponse<Reclamation> {
    @Override
    public Reponse getReponse(Reclamation reclamation) {
String sql ="SELECT * FROM Reponse where reclamation_id = ?";
return null;
    }
}
