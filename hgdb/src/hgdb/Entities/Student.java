package hgdb.Entities;
//Java Bean standard: 
//	this simply means that your class 
//	must provide a null-argument-constructor and 
//	each property must have a getter and setter.

public class Student {
    String name;
    String roll;
    char gender;
    float cpi;
    public Student() {
    	int a;
    	System.out.println("st");
    }  // nullary-constructor

    public Student(String name, String roll, char gender) {
    	setName(name);
    	setRoll(roll);
    	setGender(gender);
//    	setCpi(cpi);
//    	System.out.println("st");
    }   
    public Student(String name, String roll, char gender, float cpi) {
    	setName(name);
    	setRoll(roll);
    	setGender(gender);
    	setCpi(cpi);
    }

    public String getName() {return name; }
    public void setName(String name) {this.name = name;}
    
    public float getCpi() {return cpi; }
    public void setCpi(float cpi) {this.cpi = cpi;}
    
    public String getRoll() {return roll; }
    public void setRoll(String roll) {this.roll = roll;}
    
    public char getGender() {return gender; }
    public void setGender(char gender) {this.gender = gender;}
    
}