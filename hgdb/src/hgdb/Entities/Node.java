package hgdb.Entities;

import java.util.*;

public class Node {
	String id;
	HashMap<String, String> data;
	public Node() {
		data = new HashMap<String, String>();
	}
	
	
	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
    public HashMap<String, String> getData() {
    	return data;
    }
    public void setId(String id) {
		this.id = id;
	}
    public String getId() {
    	return id;
    }
    
    public String getType() {
    	return data.get("type");
    }
    
}
