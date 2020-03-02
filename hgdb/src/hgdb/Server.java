package hgdb;

import java.sql.*;
import hgdb.Node;

public class Server {
//	 there is a question mark in url variable
//	 it is used so that we can get the list of all databases
//	private final String url = "jdbc:postgresql://localhost:9432/?";
	private final String url = "jdbc:postgresql://localhost:9432/";
	private final String user = "shivam";
	private final String password = "";
	
	public Connection connect(String db_name) {
        Connection conn = null;
        try {
        	String finalURL = url + db_name;
            conn = DriverManager.getConnection(finalURL, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 
        return conn;
    }
	
	public void listAllDBs(Connection conn) {
		System.out.println("Databases are: ");
		try {
			String sql = "SELECT datname from pg_database WHERE datistemplate=false;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				System.out.println(result.getString(1));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	public void createGraph(Connection conn) {
		try {
			Statement stmnt = (Statement) conn.createStatement();
			ResultSet tables = stmnt.executeQuery("SELECT * from pg_catalog.pg_tables "
												+ "WHERE schemaname != 'pg_catalog' "
												+ "AND schemaname != 'information_schema' ;"
												);
			System.out.println("Foreign kys relations are:");
			String sql = "select kcu.table_schema || '.' ||kcu.table_name as foreign_table,\n" + 
					"       '>-' as rel,\n" + 
					"       rel_tco.table_schema || '.' || rel_tco.table_name as primary_table,\n" + 
					"       string_agg(kcu.column_name, ', ') as fk_columns,\n" + 
					"       kcu.constraint_name\n" + 
					"from information_schema.table_constraints tco\n" + 
					"join information_schema.key_column_usage kcu\n" + 
					"          on tco.constraint_schema = kcu.constraint_schema\n" + 
					"          and tco.constraint_name = kcu.constraint_name\n" + 
					"join information_schema.referential_constraints rco\n" + 
					"          on tco.constraint_schema = rco.constraint_schema\n" + 
					"          and tco.constraint_name = rco.constraint_name\n" + 
					"join information_schema.table_constraints rel_tco\n" + 
					"          on rco.unique_constraint_schema = rel_tco.constraint_schema\n" + 
					"          and rco.unique_constraint_name = rel_tco.constraint_name\n" + 
					"where tco.constraint_type = 'FOREIGN KEY'\n" + 
					"group by kcu.table_schema,\n" + 
					"         kcu.table_name,\n" + 
					"         rel_tco.table_name,\n" + 
					"         rel_tco.table_schema,\n" + 
					"         kcu.constraint_name\n" + 
					"order by kcu.table_schema,\n" + 
					"         kcu.table_name;" ;
			stmnt = (Statement) conn.createStatement();
			ResultSet forKey = stmnt.executeQuery(sql);
			while( forKey.next()) {
				System.out.println(forKey.getString(3) );
				
			}
			System.out.println("Tables in current database are: ");
			while(tables.next()){
				
				String table = tables.getString(2);
				System.out.println(table);
				Statement stmnt2 = (Statement) conn.createStatement();
				ResultSet tuples = stmnt2.executeQuery("SELECT * from " + table + " ;");
				while(tuples.next()) {
					System.out.println("\t" + tuples.getString(1));
					Node nd = new Node();
					// add this object to the graph
//					for() {
//						
//					}
//					graph.add(Nd)
				}
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
	
		Server server = new Server();
//		Connection conn = server.connect("?");
		Connection conn = server.connect("mtp");
		
		server.listAllDBs(conn);
		// now lets create graph from the tables in the given database
		// traverse through all tables in the given database
		server.createGraph(conn);
	}
}
