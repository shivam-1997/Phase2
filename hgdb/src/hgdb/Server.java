package hgdb;

import java.sql.*;
import hgdb.Node;
import java.net.*; 
import java.io.*;

import org.hypergraphdb.HGConfiguration;
import org.hypergraphdb.HGEnvironment;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.handle.SequentialUUIDHandleFactory;
import org.hypergraphdb.storage.bje.BJEConfig;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class Server {

	private HyperGraph graph;
	public void addNodes() {
		try {	
			Statement stmnt = (Statement) conn.createStatement();
			ResultSet tables = stmnt.executeQuery("SELECT * from pg_catalog.pg_tables "
												+ "WHERE schemaname != 'pg_catalog' "
												+ "AND schemaname != 'information_schema' ;"
												);
			System.out.println("Foreign key relations are:");
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
	//				for() {
	//					
	//				}
					graph.add(nd);
				}
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	public void createGraph() {
		try {
			String databaseLocation = "databases/University/";
			File folder = new File(databaseLocation);
			if(folder.exists())		Utils.deleteFolder(folder);
//			HGConfiguration config = setConfig();
//			graph = HGEnvironment.get(databaseLocation, config);
		    graph = HGEnvironment.get(databaseLocation); 
		    
//			Importing data-set
			addNodes();		    
//			addRelations();

//		    for(Student s: sts)
//				System.out.println(s.getName()+" "+s.getCpi());

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//	 initialize socket and input stream 
	 private Socket          socket   = null; 
	 private ServerSocket    server   = null; 
	 private DataInputStream in       =  null; 
	 String xml = "<?xml version=\"1.0\"?>"
		 		+ "<msg>"
		 			+ "<command>CREATE</command>"
		 			+ "<id>123</id>"
		 			+ "<sources>"
		 				+ "<source>1</source>"
		 			+ "</sources>"
		 			+ "<dest>student</dest>"
		 			+ "<attr>"
		 				+ "<name>gender</name>"
		 				+ "<operand>=</operand>"
		 				+ "<value>m</value>"
		 			+ "</attr>"
		 		+ "</msg>";
	 public int performOperation(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(xml);
			ByteArrayInputStream input = new ByteArrayInputStream(
			xmlStringBuilder.toString().getBytes("UTF-8"));
			Document doc = builder.parse(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return 1;
	 }
	 public void connectToClient(int port) 
	 { 
	     // starts server and waits for a connection 
	     try
	     { 
	    	 InetAddress inetAddress=InetAddress.getByName("localhost");  
	         server = new ServerSocket();
	         server.setReuseAddress(true);
	         //Binding the SocketAddress with inetAddress and port  
	         SocketAddress endPoint=new InetSocketAddress(inetAddress, port);  
	         
	         //bind() method  the ServerSocket to the specified socket address  
	         server.bind(endPoint);  
	         
	         System.out.println("Server started\n "
	         		+ "Waiting for a client ..."); 

	         socket = server.accept(); 
	         System.out.println("Client accepted"); 

	         // takes input from the client socket 
	         InputStream istream = socket.getInputStream();
	         in = new DataInputStream( new BufferedInputStream(socket.getInputStream())); 
	         BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
	 	    
	         OutputStream ostream = socket.getOutputStream(); 
	         PrintWriter pwrite = new PrintWriter(ostream, true);
	    
	                                 // receiving from server ( receiveRead  object)
	         
	         String receiveMessage, sendMessage;               
	         while(true)
	         {
	        	 int status=0;
	        	 if((receiveMessage = receiveRead.readLine()) != null)  
	        	 {
	        		 System.out.println(receiveMessage);
	        		 status = performOperation(xml);
	        	 } 
	        	 if(status == 1)
	        		 sendMessage = "SUCCESS";
	        	 else sendMessage = "FAIL";
	           pwrite.println(sendMessage);             
	           pwrite.flush();
	         }    

	     } catch(IOException i) { 
	    	 i.printStackTrace();
	     } 
	 } 

	 

//	there is a question mark in url variable
//	it is used so that we can get the list of all databases
//	private final String url = "jdbc:postgresql://localhost:9432/?";
	private final String url = "jdbc:postgresql://localhost:9432/";
	private final String user = "shivam";
	private final String password = "";
	private Connection conn=null;
	
	public void connectToDB(String db_name) {
        try {
        	String finalURL = url + db_name;
            conn = DriverManager.getConnection(finalURL, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	public void listAllDBs() {
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
	
	
	public void closeAll() {
		try {
			socket.close();
		    in.close();
		    conn.close(); 
		    graph.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
	}
	
	public static void main(String args[]) {
	
		Server obj = new Server();
//		obj.connectToDB("?");
//		obj.connectToDB("mtp");
		
//		obj.listAllDBs();
// 		creates graph from the tables in the given database
//		obj.createGraph();
		
//		creating Dummy Graph
//		connecting to Postgres Client
//		obj.connectToClient(8433);
//		close connection to Client
//		obj.closeAll();
  }
	
	private HGConfiguration setConfig() {
		// setting the configurations
		HGConfiguration config = new HGConfiguration();
		SequentialUUIDHandleFactory handleFactory = new SequentialUUIDHandleFactory(System.currentTimeMillis(), 0);
		config.setHandleFactory(handleFactory);
		config.setTransactional(false);
		BJEConfig storeConfig = (BJEConfig) config.getStoreImplementation().getConfiguration();
//		storeConfig.getEnvironmentConfig().setCacheSize(1024 * 1024 * 1000);
		return config;		
	}
}
