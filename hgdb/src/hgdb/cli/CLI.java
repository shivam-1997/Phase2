package hgdb.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.algorithms.HGTraversal;
import org.hypergraphdb.util.Pair;
import org.hypergraphdb.HGQuery.hg;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.hypergraphdb.HGValueLink;

import hgdb.Utils;
import hgdb.Entities.HyperEdge;
import hgdb.Entities.Node;
import hgdb.Entities.Root;
import hgdb.Entities.Table;
import hgdb.hyperedgeUtils.CreateHyperEdge;
import hgdb.ut.ShowGraph;

@SuppressWarnings("rawtypes")
public class CLI {
	HyperGraph graph;
	public static void print(String message) {
		System.out.println(message);
	}
	public int create_type(JSONObject json) {
		try {
			
			String name =  (String)json.get("name");
			// creating a hashmap of attributes
			HashMap<String, String> attributes = new HashMap<String, String>();
			JSONArray attributesArray = (JSONArray)json.get("attributes");
			Iterator attrListItr = attributesArray.iterator();
			int attrCount=0;
			while(attrListItr.hasNext()) {
				Iterator attrItr =((Map)attrListItr.next()).entrySet().iterator(); 
				while (attrItr.hasNext()) { 
					Map.Entry pair = (Entry) attrItr.next(); 
	                attributes.put((String)pair.getKey(), (String)pair.getValue()); 
	            } 
				attrCount++;
			}
			String key = (String)json.get("key");
			String foreignKey = (String)json.get("foreign key");
			String referredTable = (String)json.get("referred table");
			String referredAttr = (String)json.get("referred attr");
			
//			creating an object of Table class using the Json message
			Table tableNode = new Table( name, attributes, key, foreignKey, referredTable, referredAttr);
//			adding the type node to graph
			HGHandle typeHandle = graph.add(tableNode);
//			query to find the handle of the root node
			HGHandle rootHandle = graph.findOne( hg.type(Root.class) );
//			adding a link from root node to type node
			graph.add(new HGValueLink("root_type", rootHandle, typeHandle));
//			if everything succeeds return 0
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}
	public int drop_table(String tableName) {
		try {
			// first delete all the rows for the given table
			if(truncate_table(tableName)<0) {
				return -1;
			}
			// now delete the table node itself
			HGHandle typeHandle = graph.findOne( hg.and( hg.type(Table.class), hg.eq("name", tableName) ) );
			graph.remove(typeHandle, false);
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}
	
	
	/*
	 * to be completed
	 * all the nodes under concerned table should reflect the new columns
	 */
	public int alter_table(JSONObject json) {
		
		try {
			String tableName = (String)json.get("name");
			
			HashMap<String, String> attributes = new HashMap<String, String>();
			
			String type = (String)json.get("type");
			
			graph.getOne(hg.and( hg.type(Table.class), hg.eq("name", tableName)) );
			
			if(type.equalsIgnoreCase("add")) {
//				add new columns
				JSONArray colsArray = (JSONArray)json.get("columns");
				Iterator attrListItr = colsArray.iterator();
				while(attrListItr.hasNext()) {
					Iterator attrItr =((Map)attrListItr.next()).entrySet().iterator(); 
					while (attrItr.hasNext()) { 
		                Map.Entry pair = (Entry) attrItr.next(); 
		                attributes.put((String)pair.getKey(), (String)pair.getValue()); 
		            } 
				}
			}
			else {
//				modify existing columns
			}
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}

	public int truncate_table(String tableName) {
		try {
			HGHandle typeHandle = graph.findOne( hg.and( hg.type(Table.class), hg.eq("name", tableName)));
			HGALGenerator alGen = new DefaultALGenerator(graph,	//  HyperGraph hg
														 hg.type(HGValueLink.class), // HGAtomPredicate linkPredicate
														 null, //  hg.type(String.class), // HGAtomPredicate siblingPredicate
														 false, // boolean returnPreceeding
														 true,	// boolean returnSucceeding
														 false	// boolean reverseOrder
														 );
			HGTraversal trav= new HGBreadthFirstTraversal(typeHandle, alGen, 2);
			while(trav.hasNext()){
				Pair<HGHandle, HGHandle> pair = trav.next();
				
				System.out.println("\nTraversing. Current word: " + graph.get(pair.getSecond()).getClass());
				if(graph.get(pair.getSecond()).getClass() == Node.class) {
					Node node = (Node)graph.get(pair.getSecond());
					if(node.getType() == tableName) {
						graph.remove(pair.getSecond(), false);						
						print("\nRemoved: " + node.getId());
					}
				}
			}
			
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}
	
	public int insert(JSONObject json) {
		try {
			String tableName = (String) json.get("type");
			
			HashMap<String, String>
			data = 	((HashMap<String, String>) json.get("values"));
			
			HGHandle tableHandle = graph.findOne(	
					hg.and( 
							hg.type(Table.class),
							hg.eq("name", tableName)
							)
				);
			Table table = (Table)graph.get( tableHandle );
			
			Node node = new Node(tableName, data.get(table.getKey()), data);
			HGHandle nodeHandle = graph.add(node);

//			linking the node with the concerned parent table
			graph.add(new HGValueLink("table_row", tableHandle, nodeHandle));
			
//			linking the node with the refrenced node( if any)
			if(table.getForeignKey() == null) {
				return 0;
			}
			// finding the referred table
			HGHandle referredTableHandle = graph.findOne(	
													hg.and( 
															hg.type(Table.class),
															hg.eq("name", table.getReferredTable())
															)
													);
			// finding the node to which the newly added node should refer to
			HGALGenerator alGen = new DefaultALGenerator(
														graph,	//  HyperGraph hg
														hg.type(HGValueLink.class), // HGAtomPredicate linkPredicate
														null, //  hg.type(String.class), // HGAtomPredicate siblingPredicate
														false, // boolean returnPreceeding
														true,	// boolean returnSucceeding
														false	// boolean reverseOrder
														);
			HGTraversal trav= new HGBreadthFirstTraversal(referredTableHandle, alGen, 2);
			HGHandle referredNodeHandle = null;
			while(trav.hasNext()){
				Pair<HGHandle, HGHandle> pair = trav.next();
			
				System.out.println("\nTraversing. Current word: " + graph.get(pair.getSecond()).getClass());
				Object referredNodeObj = graph.get(pair.getSecond());
				
				if( referredNodeObj.getClass() == Node.class) {
					Node referredNode = (Node)graph.get(pair.getSecond());
					
					if(referredNode.getType() == table.getReferredTable() 
							&& 
						referredNode
						.getData()
						.get(table.getReferredAttr())
						.equalsIgnoreCase( data.get(table.getForeignKey()) )) 
					{
						referredNodeHandle = pair.getSecond();				
						print("\nFound: " + node.getId());
						break;
					}
				}
			}

			graph.add( new HGValueLink("foreign", referredNodeHandle, nodeHandle));
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int processCommand( String command, JSONObject json) {
//		creating table/type
		if(command.equalsIgnoreCase("create_table")) {
			return create_type(json);
		}
//		deleting table/type
		else if(command.equalsIgnoreCase("drop_table")) {
			return drop_table( (String)json.get("name") );
		}
//		altering table
		else if(command.equalsIgnoreCase("alter_table")) {
			return alter_table(json);
		}
//		truncating table
		else if(command.equalsIgnoreCase("truncate_table")) {
			return truncate_table( (String)json.get("name") );
		}
//		inserting data into a table
//		i.e, creating a node
		else if(command.equalsIgnoreCase("insert")) {
			return insert(json);
		}
//		updating the values of node(s)
		else if(command.equalsIgnoreCase("update")) {
			
		}
//		deleting node(s)
		else if(command.equalsIgnoreCase("delete")) {
			
		}
//		creating hyperedge
		else if(command.equalsIgnoreCase("CREATE_HYPEREDGE")) {
//			 creation of a new hyperedge
//				command = "CREATE HYPEREDGE (123, (prof_2), student, cpi > 9);";
//				CREATE HYPEREDGE (121, (prof_0, prof_2), student, cpi > 9);
				if(CreateHyperEdge.createHyperEdge(graph, json)>=0) {
				System.out.println("Successfully created the hyperedge");
			}
			else {
				System.out.println("Error in hyperedge creation");
			}	
		}

		return 1;
		
	}
	
	public void startCLI(HyperGraph graph) {
		
		this.graph = graph;
 		print("\n Enter the commands\n");
	    Scanner sc = new Scanner(System.in);
		String sqlStmt = "";
		try {

			JSONParser parser = new JSONParser(); 
			
			while(true){
				System.out.print("> ");
				sqlStmt += sc.nextLine().trim();
				if(sqlStmt.isBlank()) {
					continue;
				}
				if(sqlStmt.charAt(sqlStmt.length()-1)!=';') {
					continue;
				}
				else {
					sqlStmt = sqlStmt.replace(';', ' ');
					sqlStmt = sqlStmt.trim();
				}
				
				print("Statement: " + sqlStmt);
				if(sqlStmt.equalsIgnoreCase("exit")) {
					break;
				}
				else if(sqlStmt.equalsIgnoreCase("show")) {
					ShowGraph.showGraph(graph);
					sqlStmt = "";
					continue;
				}
				print("json: "+ sqlStmt);
				JSONObject json = null;
				try {					
					json = (JSONObject) parser.parse(sqlStmt);
					/*
					String[] arr = sqlStmt.split(" ");
					String command = arr[0].trim();

					if(command.equalsIgnoreCase("exit")) {
						break;
					}
					processCommand(command, sqlStmt);
					command="";
				*/
					String command = (String) json.get("command");
					
					processCommand(command, json);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					sqlStmt = "";
				}
			
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}
	
	

	public static Map<String, HGHandle> studentHandles = new HashMap<String, HGHandle>(25000);
	public static Map<String, HGHandle> profHandles = new HashMap<String, HGHandle>(1000);
	public static Map<String, HGHandle> projHandles = new HashMap<String, HGHandle>(1000);
	
	public static void addStudents(String fileName,  HyperGraph graph)
	{
//		creating an object of Table class using the Json message
		Table tableNode = new Table( "Student", null, null, null, null, null);
//		adding the type node to graph
		HGHandle typeHandle = graph.add(tableNode);
//		query to find the handle of the root node
		HGHandle rootHandle = graph.findOne( hg.type(Root.class) );
//		adding a link from root node to type node
		graph.add(new HGValueLink("root_type", rootHandle, typeHandle));
		
	    // loading the file in the form of list of strings
		List<String> lines = Utils.getAllLines(fileName); 

	    // creating a list of Student objects
	
	    List<Node> students = new ArrayList<Node>();
	    Iterator<String> itr = lines.iterator();
	    while (itr.hasNext()) {
	    	String nextLine = itr.next();
	    	String[] arrOfStr = nextLine.split("\t", 3);
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("type", "student");
	    	data.put("roll", arrOfStr[0].trim());
	    	data.put("name", arrOfStr[0].trim());
	    	data.put("gender", arrOfStr[1].trim());
	    	data.put("cpi", arrOfStr[2].trim());
	    	Node student = new Node();
	    	student.setData(data);
	    	student.setId(arrOfStr[0].trim());
	    	students.add(student);
	    }
	    
	    // importing the student objects in the graph
	    Iterator<Node> students_itr = students.iterator();
	    int i=0;
	    long start_time = System.nanoTime();
	    
	    while (students_itr.hasNext()) {
	    	if(i%1000 == 0)	System.out.print(i+"...");
    		Node obj = students_itr.next();
    		
    		HGHandle nodeHandle = graph.add(obj);
    		graph.add(new HGValueLink("type_node", typeHandle, nodeHandle));

    		studentHandles.put(obj.getData().get("name"), nodeHandle);
    		
	    	i+=1;
	    }
	    long end_time = System.nanoTime();
	    long time_taken = (long) ((end_time- start_time)/1000000000.00);
		System.out.println(i+" students imported");
		System.out.println("import of nodes took "+ time_taken + "s.");
	    
	 } 

	public static void addProfs(String fileName,  HyperGraph graph)
	{ 
//		creating an object of Table class using the Json message
		Table tableNode = new Table( "Professor", null, null, null, null, null);
//		adding the type node to graph
		HGHandle typeHandle = graph.add(tableNode);
//		query to find the handle of the root node
		HGHandle rootHandle = graph.findOne( hg.type(Root.class) );
//		adding a link from root node to type node
		graph.add(new HGValueLink("root_type", rootHandle, typeHandle));

	    // loading the file in the form of list of strings
		List<String> lines = Utils.getAllLines(fileName); 

	    // creating a list of Student objects
		
	    List<Node> profs = new ArrayList<Node>();
	    Iterator<String> itr = lines.iterator();
	    while (itr.hasNext()) {
	    	String nextLine = itr.next();
	    	String[] arrOfStr = nextLine.split("\t", 2);
	    	
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("type", "professor");
	    	data.put("roll", arrOfStr[0].trim());
	    	data.put("name", arrOfStr[0].trim());
	    	data.put("gender", arrOfStr[1].trim());
	    	
	    	Node prof = new Node();
	    	prof.setData(data);
	    	prof.setId(arrOfStr[0].trim());
	    	profs.add(prof);
	    }
	    
	    // importing the objects in database
	    Iterator<Node> profs_itr = profs.iterator();
	    int i=0;
	    long start_time = System.nanoTime();
	    while (profs_itr.hasNext()) {
	    	if(i%1000 == 0)	System.out.print(i+"...");
    		Node obj = profs_itr.next();

    		HGHandle nodeHandle = graph.add(obj);
    		graph.add(new HGValueLink("type_node", typeHandle, nodeHandle));

    		profHandles.put(obj.getData().get("name"), nodeHandle);
	    	i+=1;
	    }
	    long end_time = System.nanoTime();
	    long time_taken = (long) ((end_time- start_time)/1000000.0);
		System.out.println(i+" profs imported");
		System.out.println("import of nodes took "+ time_taken + "ms.");
	}
	
	public static void addProjs(String fileName,  HyperGraph graph)
	{ 
//		creating an object of Table class using the Json message
		Table tableNode = new Table( "Project", null, null, null, null, null);
//		adding the type node to graph
		HGHandle typeHandle = graph.add(tableNode);
//		query to find the handle of the root node
		HGHandle rootHandle = graph.findOne( hg.type(Root.class) );
//		adding a link from root node to type node
		graph.add(new HGValueLink("root_type", rootHandle, typeHandle));

	    // loading the file in the form of list of strings
		List<String> lines = Utils.getAllLines(fileName); 

	    // creating a list of Project objects
		
	    List<Node> projs = new ArrayList<Node>();
	    Iterator<String> itr = lines.iterator();
	    while (itr.hasNext()) {
	    	String nextLine = itr.next();
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("type", "project");
	    	data.put("name", nextLine.trim());
	    	
	    	Node proj = new Node();
	    	proj.setData(data);
	    	proj.setId(nextLine.trim());
	    	projs.add(proj);
	    }
	    
	    // importing the objects in database
	    Iterator<Node> projs_itr = projs.iterator();
	    int i=0;
	    long start_time = System.nanoTime();
	    while (projs_itr.hasNext()) {
	    	if(i%1000 == 0)	System.out.print(i+"...");
    		Node obj = projs_itr.next();
    		
       		HGHandle nodeHandle = graph.add(obj);
    		graph.add(new HGValueLink("type_node", typeHandle, nodeHandle));
    		
    		projHandles.put(obj.getData().get("name"), nodeHandle);
	    	i+=1;
	    }
	    long end_time = System.nanoTime();
	    long time_taken = (long) ((end_time- start_time)/1000000.0);
		System.out.println(i+" projects imported");
		System.out.println("import of nodes took "+ time_taken + "ms.");
	}
 	
	public static void addRelations(HyperGraph graph)
	{
		// loading the file in the form of list of strings
		List<String> st_proj_lines = Utils.getAllLines("dataset/student_proj_relations.txt");
		List<String> prof_proj_lines = Utils.getAllLines("dataset/prof_proj_relations.txt");
		System.out.println("importing relations");
		// importing the relations
		Iterator<String> st_proj_itr = st_proj_lines.iterator();
		Iterator<String> prof_proj_itr = prof_proj_lines.iterator();
		int i=0;
		HGHandle handle1, handle2;

		long start_time = System.nanoTime();
	    
		while(st_proj_itr.hasNext()) {
			String line = st_proj_itr.next();
			String[] arrOfStr = line.split("\t", 2); 
			handle1 = studentHandles.get(arrOfStr[0]); // student
			handle2 = projHandles.get(arrOfStr[1]);	// project
//			System.out.println(handle1 + " " + handle2);
			graph.add(new HGValueLink("student_proj", handle2, handle1));
			i++;
		}
		while(prof_proj_itr.hasNext()) {
			String line = prof_proj_itr.next();
			String[] arrOfStr = line.split("\t", 2); 
			handle1 = profHandles.get(arrOfStr[0]);
			handle2 = projHandles.get(arrOfStr[1]);
			graph.add(new HGValueLink("prof_proj", handle1, handle2));
			i++;
		}
		
		
		long end_time = System.nanoTime();
	    long time_taken = (long) ((end_time- start_time)/1000000000.00);
	    
//		
////		HGHandle duplicateLink = graph.add(new HGPlainLink(handle1, handle2));
//        List<HGHandle> dupsList = hg.findAll(graph, hg.link(handle1, handle2));
//        System.out.println("querying for link returned that duplicate Link? :" + dupsList.contains(duplicateLink));
		System.out.println(i+" Relations imported");
		System.out.println("import of relations took "+ time_taken + "s.");
	    
	}
 			
	public void createGraph(HyperGraph graph) {
		
		graph.add(new Root());
		addStudents("dataset/students.txt", graph);
		addProfs("dataset/profs.txt", graph);
		addProjs("dataset/projects.txt", graph);
			    
		addRelations(graph);
		System.out.println("Graph created");
	}

}
