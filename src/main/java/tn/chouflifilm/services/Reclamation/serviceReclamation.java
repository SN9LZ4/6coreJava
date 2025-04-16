package tn.chouflifilm.services.Reclamation;

import tn.chouflifilm.entities.Reclamation;
import tn.chouflifilm.entities.User;
import tn.chouflifilm.services.userService;
import tn.chouflifilm.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class serviceReclamation implements IReclamationService{
Connection cnx;
    public serviceReclamation() {
        cnx= MyDataBase.getDataBase().getConnection();
    }




    @Override
    public List<Reclamation> getListReclamations() throws SQLException {
     String sql = "Select * from Reclamation";
     Statement stmt = cnx.createStatement();
     ResultSet rs = stmt.executeQuery(sql);
        List<Reclamation> listReclamation = new ArrayList<>();
     while (rs.next()){
         int id = rs.getInt("id");
         int user_id = rs.getInt("user_id");
         String image = rs.getString("image");
         String status = rs.getString("status");
         String type = rs.getString("type");
         String description = rs.getString("description");
         Date created_at = rs.getDate("created_at");
         String priority = rs.getString("priority");
         userService userService = new userService();
         User user =    userService.recherparid(user_id);

         Reclamation reclamation = new Reclamation(id,user, image,type ,status, description, created_at, priority);
         reclamation.setUser(user);
         listReclamation.add(reclamation);
     }
return listReclamation;

    }


    @Override
    public void ajouterReclamation(Reclamation reclamation) throws SQLException {
        String sql = "INSERT INTO reclamation ( user_id, image, type, status, description, created_at, priority) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = cnx.prepareStatement(sql);
        stmt.setInt(1, 35);
        stmt.setString(2, reclamation.getImage());
        stmt.setString(3, reclamation.getType());
        stmt.setString(4, reclamation.getStatus());
        stmt.setString(5, reclamation.getDescription());
        stmt.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));
        stmt.setString(7,"medium");
        stmt.executeUpdate();
        System.out.println("Reclamation Ajouté avec succés");


    }

    @Override
    public void supprimerReclamation(int id) throws SQLException {
        String sql = "delete from reclamation where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1,id);
        ps.executeUpdate();
        System.out.println("Réclamation");
    }
}
