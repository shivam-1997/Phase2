package hgdb.Entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.algorithms.HGDepthFirstTraversal;
import org.hypergraphdb.algorithms.HGTraversal;
import org.hypergraphdb.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sleepycat.asm.Type;

/*
 * this class implements a hyperedge 
 * for
 * only one source id
 * only one destination type
 * comparison of ONLY one attribute value
 * comparison of ONLY one connected type or id
 * comparison of count of connected components of the destination
 * comparison of count of distance of connected components of the destination
 */

public class HyperEdge{
	
	String id;
	String source_id;
	String destination_type;
	JSONObject data;
	
	int count=0;
	String res="";
	
	public HyperEdge() {
		// nullary constructor
	}
    
    public String getType() {
    	return (String) data.get("type");
    }

   	public int trigger_function_simplest(HyperGraph graph) {
		count = 0;
		res = "";
		Set<String> finalSet = new HashSet<String>(); 
		long n_sources = Long.parseLong((String) data.get("n_sources"));
		
		for(long i=0; i< n_sources; i++) {
			print("source id"+ i + ": "+data.get("source"+ i));
	    	String source = (String) data.get("source"+ i);
			
			HGHandle sourceHandle = hg.findOne(graph, 
									hg.and(
											hg.type(Node.class),
											hg.eq("id", source.trim())
											)
									);
			
			if(sourceHandle == null) {
				print("No such source <"+ source+"> exists");
				res = "";
				count=0;
				return -1;
			}
			
//			Object sourceObject = graph.get(sourceHandle);
//	        Node sourceNode = (Node)sourceObject;
//		    print("The source is: "+ sourceNode.getId());
			Set<String> resSet = new HashSet<String>(); 
	
		
			try {
				
				HGALGenerator adjGen = new DefaultALGenerator(graph, null, null , true, true, false, false);
				
	//			Prof->Proj->Student
				
				HGTraversal trav= new HGDepthFirstTraversal(sourceHandle, adjGen);
				String operator = (String) data.get("operator");
		    	String attribute = (String) data.get("attribute");
		    	String value = (String) data.get("value");
		    	
	//	    	print("destination->" + data.get("destination")+".");
	//          print("attribute->" + attribute);
	//	    	print("value->"+value);

				while(trav.hasNext()){
			        Pair<HGHandle, HGHandle> pair = trav.next();
			        Object nextElement = graph.get(pair.getSecond());
		            Node next = (Node)nextElement;
//		            print(next.getData().get("type")+".");
		            
		            if(next.getData().get("type").equalsIgnoreCase((String) data.get("destination"))) {
		            	
		            	print(attribute + " of "+ data.get("destination") + " "+ next.getId()+": "+next.getData().get(attribute));
		            	boolean found = false;
				    	if(operator.equalsIgnoreCase(">")) {
				    		if(Double.valueOf(next.getData().get(attribute)) > 
				    		Double.valueOf(value)) {
//				    			res += next.getData().get("name") +"\n";
				    			found = true;
				    		}
				    	}
				    	else if(operator.equalsIgnoreCase(">=")) {
				    		if(Double.valueOf(next.getData().get(attribute)) >= Double.valueOf(value)) {
//				    			res += next.getData().get("name") +"\n";
				    			found = true;
				    		}
				    	}
				    	else if(operator.equalsIgnoreCase("<")) {
				    		if(Double.valueOf(next.getData().get(attribute)) <= Double.valueOf(value)) {
//				    			res += next.getData().get("name") +"\n";
				    			found = true;
				    		}
				    	}
				    	else if(operator.equalsIgnoreCase("<=")) {
				    		if(Double.valueOf(next.getData().get(attribute)) <= Double.valueOf(value)) {
//				    			res += next.getData().get("name") +"\n";
				    			found = true;
				    		}
				    	}
				    	else if(operator.equalsIgnoreCase("!=") || operator.equalsIgnoreCase("<>")) {
				    		if( next.getData().get(attribute) != value) {
//				    			res += next.getData().get("name") +"\n";
				    			found = true;
				    		}
				    	}
				    	else if(operator.equalsIgnoreCase("=")) {
				    		if( next.getData().get(attribute) == value) {
//				    			res += next.getData().get("name") +"\n";
				    			found = true;
				    		}
				    	}
				    	else {
				    		print("Invalid operator");
				    		return -1;
				    	}
				    	if(found ) {
				    		resSet.add(next.getData().get("name"));
				    	}
				    }
			            
			    }
				if(i==0) {
					finalSet.addAll(resSet);
				}
				else {
					finalSet.retainAll(resSet);
				}
			    
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		count = finalSet.size();
		res = finalSet.toString();
	    
		print("done");
		
		return 0;
	}
	
	public int trigger_function(HyperGraph graph) {
		
		count = 0;
		res = "";
		
		String source_id = "";
		HGHandle sourceHandle;
		String destination_type = (String)data.get("destination_type");		

/*
 * finding the source handle
 */
		if(data.containsKey("source_id") == true) {
			// checking, whether the given source id exists or not
			source_id = (String)data.get("source_id");
			sourceHandle = hg.findOne( graph, 
										hg.and( hg.type(Node.class), hg.eq("id", source_id))
									);
			if(sourceHandle == null) {
				print("No such source <"+ source_id +"> exists");
				res = "";
				count=0;
				return -1;
			}

		}
		else {
			/*
			 * no source_id specified in JSON
			 * this he is a purely functional node
			 * therefore, the source will be the destination type node 
			 */
			source_id = destination_type;
			sourceHandle = hg.findOne( graph, hg.and( hg.type(Type.class), hg.eq("id", destination_type)) );
		}
		
//		start traversing	
//		Prof->Proj->Student
		
		Set<String> resSet = new HashSet<String>();
		try {
			HGALGenerator adjGen = new DefaultALGenerator(graph, /*linkPredicate*/null, null , false, true, false, false);
			HGALGenerator adjGen_fromDestination = new DefaultALGenerator( graph, null, null, true, true, false, true);
			HGTraversal trav= new HGDepthFirstTraversal(sourceHandle, adjGen);		
				
			while(trav.hasNext()){
				boolean havingClause= false;
				
			    Pair<HGHandle, HGHandle> pair = trav.next();
			    HGHandle destinationHandle = pair.getSecond();
			    Object nextElement = graph.get(destinationHandle);
			    
			    if(nextElement.getClass()!=Node.class) {
			    	continue;
			    }
		        Node next = (Node)nextElement;
	//	        print(next.getData().get("type")+".");
		            
		        if(next.getData().get("type").equalsIgnoreCase(destination_type)) {		            	
		        	//found the node matching the destination type
/*
 * performing the comparison on the attributes of the node
 */
		            if(data.containsKey("having")== false) {
	//		           	having field not present in the JSON
	//		           	therefore, no comparison has to be made
	//		           	on the attributes of the node
		            	havingClause = true;
		            }
		            else {
		            	JSONArray havingArray = (JSONArray)data.get("having");
		            	// havingArray = ["attribute", "operator", "value"]		            	
		            	Iterator havingArray_itr = havingArray.iterator();
		                String attribute = (String)havingArray_itr.next();
		                String operator = (String)havingArray_itr.next();
		                String value = (String)havingArray_itr.next();
		            	boolean havingComparisonResult = 
			            			performComparison(	next, attribute, operator, value);
			            	
		            	if(havingComparisonResult)	havingClause = true;
		            	else havingClause = false;
		            }
			            
		            if (havingClause==false) continue;
		            print("*** having clause satisfied ***");
/*
 * comparison on the nodes connected to the destination node
 */
		            if(data.containsKey("connected")==false) {
		            	print("*** no connected clause ***");
	            		resSet.add(next.getData().get("name"));

		            	continue;
		            }
		            
		            HGTraversal travFromDestination = null;

//	            	travFromDestination = new HGBreadthFirstTraversal(destinationHandle, adjGen);
		            if(data.containsKey("distance")) {
		            	Iterator distance_itr  = ((JSONArray)data.get("distance")).iterator();
		            	String distance_operator = (String)distance_itr.next();
		            	int distance = Integer.parseInt((String)distance_itr.next());
		            	travFromDestination = new HGBreadthFirstTraversal(destinationHandle, adjGen_fromDestination, distance);
			        }
		            else {
		            	travFromDestination = new HGBreadthFirstTraversal(destinationHandle, adjGen_fromDestination);
		            }
			        
		            Double countNodesFromDestination = 0.0;
		            Map<String, String> connectedMap = (Map<String, String>)data.get("connected");
		            String idOrType = connectedMap.get("idOrType");
		        	String idOrType_name = connectedMap.get("name");
//		        	print("idOrType = " + connectedMap.get("idOrType"));
//		        	print("idOrType_name =" + connectedMap.get("name"));
		            print("BFS traversal started");
		            int BFScount =0;
			        while(travFromDestination.hasNext()) {
			        	/*
						 * [TEMPORARY]
						 * lets display the BFS traversal
						 */
			        	
			        	Pair<HGHandle, HGHandle> pairFromDestination = travFromDestination.next();
			        	HGHandle fromDestinationHandle = pairFromDestination.getSecond();
			        	Object nextAtomFromDetination = graph.get(fromDestinationHandle);

			        	BFScount++;
			        	print("BFS Count:" + BFScount + " - " + nextAtomFromDetination.getClass());
			        	if(nextAtomFromDetination.getClass()!=Node.class) {
			        		continue;
			        	}
			        	Node nextNodeFromDestination = (Node)nextAtomFromDetination;
					    print(nextNodeFromDestination.getData().get("type")+".");
					    if(	idOrType.equalsIgnoreCase("type") 
						   &&
						 nextNodeFromDestination.getType()
									 .equalsIgnoreCase(idOrType_name)
						) {
					        	
					       countNodesFromDestination += 1.0;
						}
						else if( nextNodeFromDestination.getId()
									 .equalsIgnoreCase(idOrType_name)
						) {	
						   countNodesFromDestination += 1.0;
						 }
			        }
			        print("BFS traversal completed");
			            
			        
/*
 * Comparison of the number of nodes connected to destination node
 */
			        boolean countClause = false;
			        if(data.containsKey("count")) {
			        	Iterator countArray_itr = ((JSONArray)data.get("count")).iterator();
			        	String operator = (String)countArray_itr.next();
			        	Double value = Double.valueOf((String)countArray_itr.next());
			        	countClause = performComparison( countNodesFromDestination, operator, value);
			        }
			        else {
			        	countClause = performComparison( countNodesFromDestination, ">=", 1.0);
			        }
		            if(countClause) {
	            		resSet.add(next.getData().get("name"));
		            }
				}
			}
		}
		catch (Exception e) {
			res = "";
			count = 0;
			e.printStackTrace();
		}
		
		res = resSet.toString();
		count = resSet.size();
		
		return 1;
	}
	
	public static void print(String message) {
		System.out.println(message);
	}
/*
 * Setters and getters
 */
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}
	public String getSourceId() {
		return source_id;
	}
	public void setDestination_type(String type) {
		destination_type = type;
	}
	public String getDestination_type() {
		return destination_type;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
    public JSONObject getData() {
    	return data;
    }
    public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public boolean performComparison(Node node, String attribute, String operator, String value) {
    	
//	    	print("destination->" + data.get("destination")+".");
//          print("attribute->" + attribute);
//	    	print("value->"+value);


    	
    	print(attribute + " of "+ data.get("destination") + " "+ node.getId()+": "+node.getData().get(attribute));
    	boolean found = false;
    	if(operator.equalsIgnoreCase(">")) {
    		if(Double.valueOf(node.getData().get(attribute)) > 
    		Double.valueOf(value)) {
//    			res += next.getData().get("name") +"\n";
    			found = true;
    		}
    	}
    	else if(operator.equalsIgnoreCase(">=")) {
    		if(Double.valueOf(node.getData().get(attribute)) >= Double.valueOf(value)) {
//    			res += next.getData().get("name") +"\n";
    			found = true;
    		}
    	}
    	else if(operator.equalsIgnoreCase("<")) {
    		if(Double.valueOf(node.getData().get(attribute)) < Double.valueOf(value)) {
//    			res += next.getData().get("name") +"\n";
    			found = true;
    		}
    	}
    	else if(operator.equalsIgnoreCase("<=")) {
    		if(Double.valueOf(node.getData().get(attribute)) <= Double.valueOf(value)) {
//    			res += next.getData().get("name") +"\n";
    			found = true;
    		}
    	}
    	else if(operator.equalsIgnoreCase("!=") || operator.equalsIgnoreCase("<>")) {
    		if( node.getData().get(attribute) != value) {
//    			res += next.getData().get("name") +"\n";
    			found = true;
    		}
    	}
    	else if(operator.equalsIgnoreCase("=")) {
    		if( node.getData().get(attribute) == value) {
//    			res += next.getData().get("name") +"\n";
    			found = true;
    		}
    	}
    	else {
    		print("Invalid operator");
    		return false;
    	}
    	
    	return found;
	}
	
	public boolean performComparison(Double a, String operator, Double b) {
    	print("count:" + a + operator + b);
		boolean found = false;
		if(operator.equalsIgnoreCase(">") &&   a > b) {
				found = true;
		}
		else if(operator.equalsIgnoreCase(">=") && a >= b) {
				found = true;
		}
		else if(operator.equalsIgnoreCase("<") && a < b) {
				found = true;
		}
		else if(operator.equalsIgnoreCase("<=") && a <= b) {
				found = true;
		}
		else if( (operator.equalsIgnoreCase("!=") || operator.equalsIgnoreCase("<>")) &&  a != b) {
				found = true;
		}
		else if(operator.equalsIgnoreCase("=") &&  a == b) {
				found = true;
		}
		return found;
	}
}