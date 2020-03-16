package hgdb.Entities;

import java.util.*;

public class Node {

	String type;
	public void setType(String type) {
		this.type = type;
	}
    public String getType() {
    	return type;
    }
	
	String id;
	public void setId(String id) {
		this.id = id;
	}
    public String getId() {
    	return id;
    }
    
	HashMap<String, String> data;
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
    public HashMap<String, String> getData() {
    	return data;
    }
    
//     constructors 
    public Node() {
    	//nullary constructor
	}
	
	public Node(String type, String id, HashMap<String, String> data) {
		setType(type);
		setId(id);
		setData(data);
		
	}
    
}
