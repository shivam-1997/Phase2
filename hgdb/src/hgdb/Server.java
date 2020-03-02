package hgdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Server {
	
	private final String url = "jdbc:postgresql://localhost/mtp";
	private final String user = "postgres";
	private final String password = "";
	
	public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 
        return conn;
    }
	
	public static void main(String args[]) {
		Server server = new Server();
		server.connect();
	}
}
