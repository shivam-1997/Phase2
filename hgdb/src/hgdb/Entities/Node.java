package hgdb.Entities;

import java.util.*;

public class Node {
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
    
    public String getType() {
    	return data.get("type");
    }
    
}
