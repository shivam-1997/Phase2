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
 * this class implements a nested - hyperedge 
 * for
 * only one source id
 * only one destination type
 * comparison of ONLY one attribute value
 * comparison of ONLY one connected type or id
 * comparison of count of connected components of the destination
 * comparison of count of distance of connected components of the destination
 */

public class NestedHyperEdge{
	
	String id;
//	String type;
	JSONObject data;
	
	int count=0;
	String res="";
	
	public NestedHyperEdge() {
		// nullary constructor
	}
    
    public String getType() {
    	return (String) data.get("type");
    }

	public int trigger_function(HyperGraph graph) {
		
		count = 0;
		res = "";
		Set<String> resSet = new HashSet<String>(); 
		
		String operation = (String)data.get("operation");
		JSONArray base_ids_array = (JSONArray)data.get("base_ids");
		
		Iterator base_ids_array_itr = base_ids_array.iterator();
		while(base_ids_array_itr.hasNext()) {
			String base_id = (String) base_ids_array_itr.next();
			
			HGHandle baseHandle = hg.findOne( graph, hg.and(
															hg.or(hg.type(Node.class),
																	hg.type(HyperEdge.class),
																			hg.type(NestedHyperEdge.class)),
																	hg.eq("id", base_id)));
			if(baseHandle == null) {
				print("No base with <"+ base_id +"> exists");
				res = "";
				count=0;
				return -1;
			}
			Object baseObject = graph.get(baseHandle);
			if(baseObject.getClass() == HyperEdge.class) {
				// in case of he,
				// first get its res string,
				// then convert it into a set and
				// the perform operation
				HyperEdge he = (HyperEdge)baseObject;
				String he_res = he.getRes();
				Set<String> he_resSet = new HashSet<String>();

		        String[] he_res_array = he_res.split(","); 
		  
		        for (String str : he_res_array)
		        	he_resSet.add(str.trim());
		        
				if(operation.equalsIgnoreCase("union")) {
					resSet.addAll(he_resSet);
				}
				else if(operation.equalsIgnoreCase("intersection")) {
					resSet.retainAll(he_resSet);
				}
			}
			else if(baseObject.getClass() == Node.class) {
				// if the base is a node, simply use its id
				Node node = (Node)baseObject;
				if(operation.equalsIgnoreCase("union")) {
					resSet.add(node.getId());
				}
				else if (operation.equalsIgnoreCase("intersection")) {
					Set<String> tempSet = new HashSet<String>();
					tempSet.add(node.getId());
					resSet.retainAll(tempSet);
				}
			}
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