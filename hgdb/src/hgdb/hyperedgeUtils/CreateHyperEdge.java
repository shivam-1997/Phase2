package hgdb.hyperedgeUtils;


import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGValueLink;
import org.hypergraphdb.HyperGraph;
import org.json.simple.JSONObject;

import hgdb.Entities.HyperEdge;
import hgdb.Entities.NestedHyperEdge;

import org.hypergraphdb.HGQuery.hg;


public class CreateHyperEdge {
	public static void print(String msg) {
		System.out.println(msg);
	}
	/*
	 * searches the graph if there already exists a hyperedge with given id
	 * returns true if hyperedge with given id is found, else return false.
	 */
	public static boolean isHEPresent(HyperGraph graph, String id) {
		HGHandle existingHandle = hg.findOne(graph, 
				hg.and(	hg.type(HyperEdge.class),
				hg.eq("id", id.trim()))
				);
		if(existingHandle != null) return true;
		return false;
		
	}
	
	public static int createHyperEdge(HyperGraph graph, String id, 
			String[] sids_string_array, 
			String destination_type, 
			String attribute, 
			String operator, 
			String value) 
	{										
		
		if(isHEPresent(graph, id)) {
			print("Hyperedge with given id already exists.");
			return -1;
		}
		
		HyperEdge he = new HyperEdge();
		JSONObject data = new JSONObject();
		
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

		System.out.println("CREATING HYPEREDGE from STRING");
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

	public static int createHyperEdge(HyperGraph graph, JSONObject json) {
		print("*** creating hyperedge from json ***");
		try {
			
			if(isHEPresent(graph, (String)json.get("id"))) {
				print("Hyperedge with given id already exists.");
				return -1;
			}

			HyperEdge he = new HyperEdge();
			he.setId( (String)json.get("id") ); 
			he.setSource_id( (String)json.get("source_id") );
			he.setDestination_type( (String)json.get("destination_type"));
			he.setData(json);
			
//			adding the he as a node
			HGHandle heHandle = graph.add(he);
//			query to find the handle of the HYPEREDGE type node
			HGHandle rootHandle = graph.findOne( hg.type(HyperEdge.class) );
//			adding a link from root node to he
			graph.add(new HGValueLink("hyperedge_node", rootHandle, heHandle));
			
			print("*** Hyperedge created ***");
			
//			triggering the he
			he.trigger_function(graph);
			print("\nresult: " +he.getRes());
			print("count: " +  he.getCount());
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}

	public static int createNestedHyperEdge(HyperGraph graph, JSONObject json) {
		print("*** creating nested-hyperedge from json ***");
		try {
			
			if(isHEPresent(graph, (String)json.get("id"))) {
				print("Nested Hyperedge with given id already exists.");
				return -1;
			}

			NestedHyperEdge he = new NestedHyperEdge();
			he.setId( (String)json.get("id") ); 
			
			he.setData(json);

//			adding the he as a node
			HGHandle heHandle = graph.add(he);
//			query to find the handle of the HYPEREDGE type node
			HGHandle rootHandle = graph.findOne( hg.type(HyperEdge.class) );
//			adding a link from root node to he
			graph.add(new HGValueLink("hyperedge_node", rootHandle, heHandle));
			
			print("*** Hyperedge created ***");
			
//			triggering the he
			he.trigger_function(graph);
			print("\nresult: " +he.getRes());
			print("count: " +  he.getCount());
			return 0;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}

}
