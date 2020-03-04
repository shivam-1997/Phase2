package hgdb.Entities;

public class Project {
	String name;
	public String getName(){
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	Project(){
		
	}
	public Project(String name){
		setName(name);
	}
}
