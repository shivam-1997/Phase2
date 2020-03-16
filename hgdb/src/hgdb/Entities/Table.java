package hgdb.Entities;

import java.util.*;

public class Table {

	String name;
	public void setName(String name) {
		this.name = name;
	}
    public String getName() {
    	return name;
    }
	
    
//    int attrCount;
//    public void setAttrCount(int count) {
//		attrCount = count;
//	}
//    public int getAttrCount() {
//    	return attrCount;
//    }
	
	HashMap<String, String> attributes;
	public void setAttributes(HashMap<String, String> data) {
		this.attributes = data;
	}
    public HashMap<String, String> getData() {
    	return attributes;
    }
    
    String key;
    public void setKey(String key) {
		this.key = key;
	}
    public String getKey() {
    	return key;
    }    

    String foreignKey;
    public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}
    public String getForeignKey() {
    	return foreignKey;
    }    

    String referredTable;
    public void setReferredTable(String name) {
		this.referredTable = name;
	}
    public String getReferredTable() {
    	return referredTable;
    }    
    
    String referredAttr;
    public void setReferredAttr(String name) {
		this.referredAttr = name;
	}
    public String getReferredAttr() {
    	return referredAttr;
    }
    
//     constructors 
    public Table() {
    	//nullary constructor
	}
	
	public Table(String name, 
				HashMap<String, String> attributes,
				String key,
				String foreignKey,
				String referredTable,
				String referredAttr) {
		setName(name);
		setAttributes(attributes);
		setKey(key);
		setForeignKey(foreignKey);
		setReferredTable(referredTable);
		setReferredAttr(referredAttr);
		
	}
    
}
