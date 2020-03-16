package hgdb;

import java.io.*;
import java.util.*;


import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;

import hgdb.Entities.*;
import hgdb.cli.CLI;
import hgdb.ut.ShowGraph;

public class CreateInRun {
		public static void print(String message) {
			System.out.println(message);
		}
		public static Map<String, HGHandle> studentHandles = new HashMap<String, HGHandle>(25000);
		public static Map<String, HGHandle> profHandles = new HashMap<String, HGHandle>(1000);
		public static Map<String, HGHandle> projHandles = new HashMap<String, HGHandle>(1000);
		static String databaseLocation = "databases/University/";
		static File folder = new File(databaseLocation);
		
		public static void addStudents(String fileName,  HyperGraph graph)
		{ 
			
		    // loading the file in the form of list of strings
			List<String> lines = Utils.getAllLines(fileName); 

		    // creating a list of Student objects
			
		    List<Node> students = new ArrayList<Node>();
		    Iterator<String> itr = lines.iterator();
		    while (itr.hasNext()) {
		    	String nextLine = itr.next();
		    	String[] arrOfStr = nextLine.split("\t", 3);
		    	HashMap<String, String> data = new HashMap<String, String>();
		    	data.put("type", "student");
		    	data.put("roll", arrOfStr[0].trim());
		    	data.put("name", arrOfStr[0].trim());
		    	data.put("gender", arrOfStr[1].trim());
		    	data.put("cpi", arrOfStr[2].trim());
		    	Node student = new Node();
		    	student.setData(data);
		    	student.setId(arrOfStr[0].trim());
		    	students.add(student);
		    }
		    
		    // importing the objects in database
		    Iterator<Node> students_itr = students.iterator();
		    int i=0;
		    long start_time = System.nanoTime();
		    while (students_itr.hasNext()) {
		    	if(i%1000 == 0)	System.out.print(i+"...");
	    		Node obj = students_itr.next();
	    		studentHandles.put(obj.getData().get("name"), graph.add(obj));
		    	i+=1;
		    }
		    long end_time = System.nanoTime();
		    long time_taken = (long) ((end_time- start_time)/1000000000.00);
			System.out.println(i+" students imported");
			System.out.println("import of nodes took "+ time_taken + "s.");
		    
		 } 

		public static void addProfs(String fileName,  HyperGraph graph)
		{ 
			
		    // loading the file in the form of list of strings
			List<String> lines = Utils.getAllLines(fileName); 

		    // creating a list of Student objects
			
		    List<Node> profs = new ArrayList<Node>();
		    Iterator<String> itr = lines.iterator();
		    while (itr.hasNext()) {
		    	String nextLine = itr.next();
		    	String[] arrOfStr = nextLine.split("\t", 2);
		    	
		    	HashMap<String, String> data = new HashMap<String, String>();
		    	data.put("type", "professor");
		    	data.put("roll", arrOfStr[0].trim());
		    	data.put("name", arrOfStr[0].trim());
		    	data.put("gender", arrOfStr[1].trim());
		    	
		    	Node prof = new Node();
		    	prof.setData(data);
		    	prof.setId(arrOfStr[0].trim());
		    	profs.add(prof);
		    }
		    
		    // importing the objects in database
		    Iterator<Node> profs_itr = profs.iterator();
		    int i=0;
		    long start_time = System.nanoTime();
		    while (profs_itr.hasNext()) {
		    	if(i%1000 == 0)	System.out.print(i+"...");
	    		Node obj = profs_itr.next();
	    		profHandles.put(obj.getData().get("name"), graph.add(obj));
		    	i+=1;
		    }
		    long end_time = System.nanoTime();
		    long time_taken = (long) ((end_time- start_time)/1000000.0);
			System.out.println(i+" profs imported");
			System.out.println("import of nodes took "+ time_taken + "ms.");
		}
		
		public static void addProjs(String fileName,  HyperGraph graph)
		{ 
			
		    // loading the file in the form of list of strings
			List<String> lines = Utils.getAllLines(fileName); 

		    // creating a list of Project objects
			
		    List<Node> projs = new ArrayList<Node>();
		    Iterator<String> itr = lines.iterator();
		    while (itr.hasNext()) {
		    	String nextLine = itr.next();
		    	HashMap<String, String> data = new HashMap<String, String>();
		    	data.put("type", "project");
		    	data.put("name", nextLine.trim());
		    	
		    	Node proj = new Node();
		    	proj.setData(data);
		    	proj.setId(nextLine.trim());
		    	projs.add(proj);
		    }
		    
		    // importing the objects in database
		    Iterator<Node> projs_itr = projs.iterator();
		    int i=0;
		    long start_time = System.nanoTime();
		    while (projs_itr.hasNext()) {
		    	if(i%1000 == 0)	System.out.print(i+"...");
	    		Node obj = projs_itr.next();
	    		
	    		projHandles.put(obj.getData().get("name"), graph.add(obj));
		    	i+=1;
		    }
		    long end_time = System.nanoTime();
		    long time_taken = (long) ((end_time- start_time)/1000000.0);
			System.out.println(i+" projects imported");
			System.out.println("import of nodes took "+ time_taken + "ms.");
		}
	 	
		public static void addRelations(HyperGraph graph)
		{
			// loading the file in the form of list of strings
			List<String> st_proj_lines = Utils.getAllLines("dataset/student_proj_relations.txt");
			List<String> prof_proj_lines = Utils.getAllLines("dataset/prof_proj_relations.txt");
			System.out.println("importing relations");
			// importing the relations
			Iterator<String> st_proj_itr = st_proj_lines.iterator();
			Iterator<String> prof_proj_itr = prof_proj_lines.iterator();
			int i=0;
			HGHandle handle1, handle2;

			long start_time = System.nanoTime();
		    
			while(st_proj_itr.hasNext()) {
				String line = st_proj_itr.next();
				String[] arrOfStr = line.split("\t", 2); 
				handle1 = studentHandles.get(arrOfStr[0]); // student
				handle2 = projHandles.get(arrOfStr[1]);	// project
//				System.out.println(handle1 + " " + handle2);
				graph.add(new HGValueLink("student_proj", handle2, handle1));
				i++;
			}
			while(prof_proj_itr.hasNext()) {
				String line = prof_proj_itr.next();
				String[] arrOfStr = line.split("\t", 2); 
				handle1 = profHandles.get(arrOfStr[0]);
				handle2 = projHandles.get(arrOfStr[1]);
				graph.add(new HGValueLink("prof_proj", handle1, handle2));
				i++;
			}
			
			
			long end_time = System.nanoTime();
		    long time_taken = (long) ((end_time- start_time)/1000000000.00);
		    
//			
////			HGHandle duplicateLink = graph.add(new HGPlainLink(handle1, handle2));
//	        List<HGHandle> dupsList = hg.findAll(graph, hg.link(handle1, handle2));
//	        System.out.println("querying for link returned that duplicate Link? :" + dupsList.contains(duplicateLink));
			System.out.println(i+" Relations imported");
			System.out.println("import of relations took "+ time_taken + "s.");
		    
		}
	 			
		public static void createGraph(HyperGraph graph) {
			
			
			addStudents("dataset/students.txt", graph);
			addProfs("dataset/profs.txt", graph);
			addProjs("dataset/projects.txt", graph);
			
//		    to get handles use findOne or findALL
//		    for (Object s : hg.getAll(graph, hg.type(Student.class))) {
//		    for (Object s : hg.findAll(graph, hg.type(Student.class))) {
//		    	// s is actually a HGHandle
//		    	Student st = graph.get((HGHandle) s);
//		    	System.out.println( st.getName() + " " + s);
//		    }
		    
			addRelations(graph);
			System.out.println("Graph created");
		}

		
 		public static void main(String[] args) {
			
 			if(folder.exists())	Utils.deleteFolder(folder);
 			
 			HyperGraph graph = HGEnvironment.get(databaseLocation);
 			
 			Node root = new Node("root", "root", null);
			HGHandle rootHandle = graph.add(root);
 			createGraph(graph);
 			
 			ShowGraph.showGraph(graph);
 			
 			CLI cli = new CLI();
 			
 			cli.startCLI(graph);
		    
 			graph.close();
		}
		
}
