package hgdb.Entities;
//Java Bean standard: 
//	this simply means that your class 
//	must provide a null-argument-constructor and 
//	each property must have a getter and setter.

public class Professor {
    String name;
    String roll;
    char gender;
    
    public Professor() {}  // nullary-constructor
    public Professor(String name, String roll, char gender) {
    	setName(name);
    	setRoll(roll);
    	setGender(gender);
    }

    public String getName() {return name; }
    public void setName(String name) {this.name = name;}
    
    public String getRoll() {return roll; }
    public void setRoll(String roll) {this.roll = roll;}
    
    public char getGender() {return gender; }
    public void setGender(char gender) {this.gender = gender;}
    
}