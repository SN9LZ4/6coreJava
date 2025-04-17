package tn.esprit.Tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyData {
    private static Connection connection;
    final String url="jdbc:mysql://localhost:3306/6core_cinema";
   final String user="root";
    final String password="";
   Connection con;
   static MyData instance;
    private MyData(){
        try {
            con=DriverManager.getConnection(url, user, password);
            System.out.println("Connection Established");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static MyData getInstance(){
        if(instance==null){
            instance=new MyData();}
        return instance;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        MyData.connection = connection;
    }

    public Connection getCnx() {
        return con;
    }



}
