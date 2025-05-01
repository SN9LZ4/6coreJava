package tn.esprit.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
   public final String url="jdbc:mysql://localhost:3306/6core_cinema";
    public final String user="root";
    public final String password="";
    private static MyDataBase myDataBase;
Connection cnx;
    public MyDataBase() {
        try {
            cnx= DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            System.out.println("erreur de connexion");
        }
    }
    public static MyDataBase getInstance(){
        if(myDataBase==null)
            myDataBase=new MyDataBase();
        return myDataBase;
    }

    public Connection getCnx() {
        return cnx;
    }
}
