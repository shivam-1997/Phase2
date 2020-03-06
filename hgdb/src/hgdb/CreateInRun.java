package hgdb;

import java.io.*;
import java.util.*;
import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.algorithms.*;
import org.hypergraphdb.handle.SequentialUUIDHandleFactory;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.hypergraphdb.storage.bje.BJEConfig;
import org.hypergraphdb.util.Pair;
import hgdb.Entities.*;

public class CreateInRun {
		public static void print(String message) {
			System.out.println(message);
		}
		public static Map<String, HGHandle> studentHandles = new HashMap<String, HGHandle>(25000);
		public static Map<String, HGHandle> profHandles = new HashMap<String, HGHandle>(1000);
		public static Map<String, HGHandle> projHandles = new HashMap<String, HGHandle>(1000);
		static HyperGraph graph = new HyperGraph();
		
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
		
		public static int createHyperEdge(String id, 
											String[] sids_string_array, 
											String destination_type, 
											String attribute, 
											String operator, 
											String value) 
		{
			print("id: "+ id);
			for(int i=0; i<sids_string_array.length; i++) {
				print("source id"+ (i+1) + ": "+ sids_string_array[i]);
			}
			print("Destination: "+ destination_type+".");
			print("attribute: "+ attribute+".");
			print("operator: "+ operator+".");
			print("value: "+  value+".");

			HGHandle existingHandle = hg.findOne(graph, 
									hg.and(	hg.type(HyperEdge.class),
											hg.eq("id", id.trim()))
									);
			
			if(existingHandle != null) {
				print("Hyperedge with given id already exists.");
				return -1;
			}
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("type", "hyperedge");
	    	data.put("source", sids_string_array[0]);
	    	data.put("destination", destination_type);
	    	data.put("attribute", attribute);
	    	data.put("operator", operator);
	    	data.put("value", value);
	    	HyperEdge he = new HyperEdge();
			he.setData(data);
			he.setId(id);
			graph.add(he);
			he.trigger_function(graph);
			print("result: " +he.getRes());
			print("count: " +  he.getCount());
			return 1;
		}
		
 		public static int createHyperEdge(String command) {

			System.out.println("CREATING HYPEREDGE");
			int i=0;
			
//			command: CREATE HYPEREDGE(
			for(; command.charAt(i)!='('; i++);
//			command: id, 
			String id="";
			for(i+=1; command.charAt(i)!=','; i++) {
				id += command.charAt(i);
			}
//			long id = Long.parseLong( id_string.trim() );
//			command: (sid1, sid2, ...)
			String sids_string="";
			for(i+=1; command.charAt(i)!=')'; i++) {
				if(command.charAt(i)=='(' ) {
					continue;
				}
				sids_string += command.charAt(i);
			}
			String[] sids_string_array = sids_string.split(",");
//			for(int j=0; j<sids_string_array.length)
			
//			command: ,
			for(i+=1; command.charAt(i)!=','; i++);
//			command: destination_type,
			String destination_type="";
			for(i+=1; command.charAt(i)!=','; i++) {
				destination_type += command.charAt(i);
			}
			destination_type = destination_type.trim();
//			command: attribute operator value)
			String condition = "";
			for(i+=1; command.charAt(i)!=')'; i++) {
				condition += command.charAt(i);
			}
			condition = condition.trim();
			String[] conditionStrings = condition.split(" ");
			String attribute = conditionStrings[0].trim();
			String operator = conditionStrings[1].trim();
			String value = conditionStrings[2].trim();
			
			return createHyperEdge(id, sids_string_array, destination_type, attribute, operator, value);
		
		}
		
 		public static void main(String[] args) {
			
			String databaseLocation = "databases/University/";
			File folder = new File(databaseLocation);
			if(folder.exists())		Utils.deleteFolder(folder);
			
			
			try {
				
				HGConfiguration config = Utils.setConfig();
				graph = HGEnvironment.get(databaseLocation, config);
				graph = HGEnvironment.get(databaseLocation); 
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		    createGraph(graph);
			print("\n Enter the commands\n");
//		    findStudentsUnderProf(graph);
		    
			Scanner sc = new Scanner(System.in);
			String command = new String("exit");
			do{
				System.out.print("> ");
				command = sc.nextLine();
//				command = "CREATE HYPEREDGE (123, (prof_2), student, cpi > 9);";
				String[] arr = command.split(" ");
				
				if(arr[0].equalsIgnoreCase("CREATE")) {
					// creation of a new hyperedge
					if(createHyperEdge(command)==1) {
						System.out.println("Successfully created the hyperedge");
					}
					else {
						System.out.println("Error in hyperedge creation");
					}	
				}
//				command="exit";
			}while(!command.equalsIgnoreCase("exit"));
			sc.close();
		    graph.close();
		}
		
		public static void findStudentsUnderProf(HyperGraph graph) {
		try {
				
			FileWriter fw = new FileWriter("reports/studentsUnderProf.txt");
			Utils.print("\nWill find all the students under a professor\n", fw);
//			DefaultALGenerator(HyperGraph hg,
//	        HGAtomPredicate linkPredicate,
//	        HGAtomPredicate siblingPredicate,
//	        boolean returnPreceeding,
//	        boolean returnSucceeding,
//	        boolean reverseOrder)
//			hg.type(HGPlainLink.class), hg.type(String.class),
			long start_time;
			long end_time;
		    long time_taken;
		    
			start_time = System.nanoTime();
			
			HGALGenerator adjGen = new DefaultALGenerator(graph, null, null , false, true, false);
			end_time = System.nanoTime();
			time_taken = (end_time- start_time)/1000;
			Utils.print("Adjacency list took "+ time_taken + "us.\n", fw);
	        
			// applying DFS
			
//			Prof->Proj->Student
			
			int prof_i=0;
			int student_count;
			int project_count;
			long max_time=0, max_time_students=0, max_time_proj=0;
			while(prof_i<1000) {
				student_count=0;
				project_count=0;
				Utils.print("prof_"+prof_i+"\n\t[",fw);

				start_time = System.nanoTime();
				HGTraversal trav= new HGDepthFirstTraversal(profHandles.get("prof_"+prof_i), adjGen);
				while(trav.hasNext()){
			        Pair<HGHandle, HGHandle> pair = trav.next();
			        Object nextElement = graph.get(pair.getSecond());
			//            Student next = (Student)nextElement;
			//            System.out.print("->" + next.getName());
			        if(nextElement.getClass()==Student.class) {
			        	
//			        	System.out.println(student_count + ":"+ ((Student)nextElement).getName());
			        	student_count++;
			        }
			        else if(nextElement.getClass()==Project.class) {
			        	
//			        	System.out.println(student_count + ":"+ ((Student)nextElement).getName());
			        	project_count++;
			        }
			        
			    }
				end_time = System.nanoTime();
				time_taken = (end_time- start_time)/1000000;
				Utils.print(student_count+" students, "+project_count+" projects present] "+ time_taken + "ms.\n", fw);

				if(time_taken>max_time) {
					max_time = time_taken;
					max_time_students = student_count;
					max_time_proj = project_count;
				}
				prof_i++;
			}
			
			Utils.print("done",fw);
			Utils.print("\nMaximum time take was "+max_time+"ms for "+ max_time_proj +" projects and "+ max_time_students+" students.", fw );
			fw.close();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
			
			
		}
		
	}
