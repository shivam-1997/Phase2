package hgdb;

import java.util.*;

public class Node {
	Dictionary data;
	public Node() {
		data = new Hashtable();
	}
	
	
	public void setData(Dictionary data) {
		this.data = data;
	}
    public Dictionary getData() {
    	return data;
    }
    
    public String getType() {
    	return data["type"];
    }
    
}
