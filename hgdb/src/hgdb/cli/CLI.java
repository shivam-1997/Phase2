package hgdb.cli;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.hypergraphdb.HGValueLink;

import hgdb.Entities.HyperEdge;
import hgdb.Entities.Node;
import hgdb.Entities.Root;
import hgdb.Entities.Table;
import hgdb.ut.ShowGraph;

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
			String foreignKey = (String)json.get("foreign key");
			String referredTable = (String)json.get("referred table");
			String referredAttr = (String)json.get("referred attr");
			
//			creating an object of Table class using the Json message
			Table tableNode = new Table( name, attributes, foreignKey, referredTable, referredAttr);
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
	
	/*
	 * to be completed
	 */
	public int drop_table(String name) {
		/*
		 * this function deletes the type node 
		 * and all the other nodes linked to it
		 */
		try {
			HGHandle typeHandle = graph.findOne(hg.type(Table.class));
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
	
	/*
	 * to be completed
	 */
	public int truncate_table(String name) {
		/*
		 * this function deletes the type node 
		 * and all the other nodes linked to it
		 */
		try {
			HGHandle typeHandle = graph.findOne(hg.type(Table.class));
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
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
				if(createHyperEdge(graph, command)==1) {
				System.out.println("Successfully created the hyperedge");
			}
			else {
				System.out.println("Error in hyperedge creation");
			}	
		}
//		displaying graph
		else if(command.equalsIgnoreCase("showgraph")) {
			ShowGraph.showGraph(graph);
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
				if(sqlStmt.charAt(sqlStmt.length()-1)!=';') {
					continue;
				}
				else {
					sqlStmt = sqlStmt.replace(';', ' ');
				}
				print("sqlStmt: " + sqlStmt);
				
				JSONObject json = (JSONObject) parser.parse(sqlStmt);
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
				if(command.equalsIgnoreCase("exit")) {
					break;
				}
				processCommand(command, json);

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}
	
	public static int createHyperEdge(HyperGraph graph, String id, 
										String[] sids_string_array, 
										String destination_type, 
										String attribute, 
										String operator, 
										String value) 
	{
		
		HGHandle existingHandle = hg.findOne(graph, 
				hg.and(	hg.type(HyperEdge.class),
						hg.eq("id", id.trim()))
				);

		if(existingHandle != null) {
			print("Hyperedge with given id already exists.");
			return -1;
		}
		
		HyperEdge he = new HyperEdge();
		HashMap<String, String> data = new HashMap<String, String>();
		
		data.put("type", "hyperedge");
    	
		print("id: "+ id);
		he.setId(id);
		
		data.put("n_sources", ""+sids_string_array.length);
		for(int i=0; i<sids_string_array.length; i++) {
			print("source id"+ i + ": "+ sids_string_array[i]);
			data.put("source"+ i, sids_string_array[i]);
	    	
		}
		
		print("Destination: "+ destination_type+".");
		data.put("destination", destination_type);
    	
		print("attribute: "+ attribute+".");
		data.put("attribute", attribute);
    	
		print("operator: "+ operator+".");
		data.put("operator", operator);
    	
		print("value: "+  value+".");
    	data.put("value", value);
    	
		
		
    	he.setData(data);
    	graph.add(he);
		he.trigger_function(graph);
		print("result: " +he.getRes());
		print("count: " +  he.getCount());
		return 1;
	}
	
	public static int createHyperEdge(HyperGraph graph, String command) {

		System.out.println("CREATING HYPEREDGE");
		int i=0;
		
//		command: CREATE HYPEREDGE(
		for(; command.charAt(i)!='('; i++);
//		command: id, 
		String id="";
		for(i+=1; command.charAt(i)!=','; i++) {
			id += command.charAt(i);
		}
//		command: (sid1, sid2, ...)
		String sids_string="";
		for(i+=1; command.charAt(i)!=')'; i++) {
			if(command.charAt(i)=='(' ) {
				continue;
			}
			sids_string += command.charAt(i);
		}
		String[] sids_string_array = sids_string.split(",");
		for(int j=0; j<sids_string_array.length; j++) {
			sids_string_array[j] = sids_string_array[j].trim();
		}

//		command: ,
		for(i+=1; command.charAt(i)!=','; i++);
//		command: destination_type,
		String destination_type="";
		for(i+=1; command.charAt(i)!=','; i++) {
			destination_type += command.charAt(i);
		}
		destination_type = destination_type.trim();
//		command: attribute operator value)
		String condition = "";
		for(i+=1; command.charAt(i)!=')'; i++) {
			condition += command.charAt(i);
		}
		condition = condition.trim();
		String[] conditionStrings = condition.split(" ");
		String attribute = conditionStrings[0].trim();
		String operator = conditionStrings[1].trim();
		String value = conditionStrings[2].trim();
		
		return createHyperEdge(graph, id.trim(), sids_string_array, destination_type.trim(), attribute.trim(), operator.trim(), value.trim());
	
	}


}
