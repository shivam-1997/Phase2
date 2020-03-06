package hgdb.Entities;

import java.awt.List;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HGQuery.hg.*;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGDepthFirstTraversal;
import org.hypergraphdb.algorithms.HGTraversal;
import org.hypergraphdb.util.Pair;

import com.sleepycat.je.rep.impl.node.Feeder.ExitException;

public class HyperEdge{
	
	HashMap<String, String> data;
	String id;
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	int count=0;
	String res="";
	
	public HyperEdge() {
		data = new HashMap<String, String>();
	}
		
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
    public HashMap<String, String> getData() {
    	return data;
    }
    
    public String getType() {
    	return data.get("type");
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

	public int trigger_function(HyperGraph graph) {
		count = 0;
		res = "";
		String source = data.get("source");

		HGHandle sourceHandle = hg.findOne(graph, 
								hg.and(
										hg.type(Node.class),
										hg.eq("id", source.trim())
										)
								);
		
		if(sourceHandle == null) {
			print("No such source exists");
			return -1;
		}
		
		Object sourceObject = graph.get(sourceHandle);
        Node sourceNode = (Node)sourceObject;
	    print("The source is: "+ sourceNode.getId());
		
		try {
			
//			DefaultALGenerator(HyperGraph hg,
//	        HGAtomPredicate linkPredicate,
//	        HGAtomPredicate siblingPredicate,
//	        boolean returnPreceeding,
//	        boolean returnSucceeding,
//	        boolean reverseOrder)
//			hg.type(HGPlainLink.class), hg.type(String.class),
			
			HGALGenerator adjGen = new DefaultALGenerator(graph, null, null , false, true, false);
			
//			Prof->Proj->Student
			
			HGTraversal trav= new HGDepthFirstTraversal(sourceHandle, adjGen);
			String operator = data.get("operator");
	    	String attribute = data.get("attribute");
	    	String value = data.get("value");
	    	
//	    	print("destination->" + data.get("destination")+".");
//          print("attribute->" + attribute);
//	    	print("value->"+value);
		    
			while(trav.hasNext()){
		        Pair<HGHandle, HGHandle> pair = trav.next();
		        Object nextElement = graph.get(pair.getSecond());
	            Node next = (Node)nextElement;
//	            print(next.getData().get("type")+".");
	            
	            if(next.getData().get("type").equalsIgnoreCase(data.get("destination"))) {
	            	
	            	print(attribute + " of "+ data.get("destination") + " "+ next.getId()+": "+next.getData().get(attribute));
	            	
			    	if(operator.equalsIgnoreCase(">")) {
			    		if(Double.valueOf(next.getData().get(attribute)) > 
			    		Double.valueOf(value)) {
			    			count++;
			    			res += next.getData().get("name") +"\n";
			    		}
			    	}
			    	else if(operator.equalsIgnoreCase(">=")) {
			    		if(Double.valueOf(next.getData().get(attribute)) >= Double.valueOf(value)) {
			    			count++;
			    			res += next.getData().get("name") +"\n";
			    		}
			    	}
			    	else if(operator.equalsIgnoreCase("<")) {
			    		if(Double.valueOf(next.getData().get(attribute)) <= Double.valueOf(value)) {
			    			count++;
			    			res += next.getData().get("name") +"\n";
			    		}
			    	}
			    	else if(operator.equalsIgnoreCase("<=")) {
			    		if(Double.valueOf(next.getData().get(attribute)) <= Double.valueOf(value)) {
			    			count++;
			    			res += next.getData().get("name") +"\n";
			    		}
			    	}
			    	else if(operator.equalsIgnoreCase("!=") || operator.equalsIgnoreCase("<>")) {
			    		if( next.getData().get(attribute) != value) {
			    			count++;
			    			res += next.getData().get("name") +"\n";
			    		}
			    	}
			    	else if(operator.equalsIgnoreCase("=")) {
			    		if( next.getData().get(attribute) == value) {
			    			count++;
			    			res += next.getData().get("name") +"\n";
			    		}
			    	}
			    	else {
			    		print("Invalid operator");
			    		return -1;
			    	}
			    }
		            
		    }

			print("done");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static void print(String message) {
		System.out.println(message);
	}
	
}
	