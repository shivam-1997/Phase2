package benchmark;
import java.io.*;
import java.util.*;
import org.hypergraphdb.*;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.algorithms.*;
import org.hypergraphdb.handle.SequentialUUIDHandleFactory;
import org.hypergraphdb.indexing.ByPartIndexer;
import org.hypergraphdb.storage.bje.BJEConfig;
import org.hypergraphdb.util.Pair;
import benchmark.Entities.Professor;
import benchmark.Entities.Project;
import benchmark.Entities.Student;

public class CreateInRun {
		
		public static Map<String, HGHandle> studentHandles = new HashMap<String, HGHandle>(25000);
		public static Map<String, HGHandle> profHandles = new HashMap<String, HGHandle>(1000);
		public static Map<String, HGHandle> projHandles = new HashMap<String, HGHandle>(1000);
		
		public static void addStudents(String fileName,  HyperGraph graph)
		{ 
			
		    // loading the file in the form of list of strings
			List<String> lines = Utils.getAllLines(fileName); 

		    // creating a list of Student objects
			
		    List<Student> students = new ArrayList<Student>();
		    Iterator<String> itr = lines.iterator();
		    while (itr.hasNext()) {
		    	String nextLine = itr.next();
		    	String[] arrOfStr = nextLine.split("\t", 2); 
		    	students.add(new Student(arrOfStr[0], arrOfStr[0], arrOfStr[1].charAt(0)));
		    }
		    
		    // importing the objects in database
		    Iterator<Student> students_itr = students.iterator();
		    int i=0;
		    long start_time = System.nanoTime();
		    while (students_itr.hasNext()) {
		    	if(i%1000 == 0)	System.out.print(i+"...");
	    		Student obj = students_itr.next();
	    		studentHandles.put(obj.getName(), graph.add(obj));
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
			
		    List<Professor> profs = new ArrayList<Professor>();
		    Iterator<String> itr = lines.iterator();
		    while (itr.hasNext()) {
		    	String nextLine = itr.next();
		    	profs.add(new Professor(nextLine, nextLine, 'm'));
		    }
		    
		    // importing the objects in database
		    Iterator<Professor> profs_itr = profs.iterator();
		    int i=0;
		    long start_time = System.nanoTime();
		    while (profs_itr.hasNext()) {
		    	if(i%1000 == 0)	System.out.print(i+"...");
	    		Professor obj = profs_itr.next();
	    		profHandles.put(obj.getName(), graph.add(obj));
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
			
		    List<Project> projs = new ArrayList<Project>();
		    Iterator<String> itr = lines.iterator();
		    while (itr.hasNext()) {
		    	String nextLine = itr.next();
		    	projs.add(new Project(nextLine));
		    }
		    
		    // importing the objects in database
		    Iterator<Project> projs_itr = projs.iterator();
		    int i=0;
		    long start_time = System.nanoTime();
		    while (projs_itr.hasNext()) {
		    	if(i%1000 == 0)	System.out.print(i+"...");
	    		Project obj = projs_itr.next();
	    		projHandles.put(obj.getName(), graph.add(obj));
		    	i+=1;
		    }
		    long end_time = System.nanoTime();
		    long time_taken = (long) ((end_time- start_time)/1000000.0);
			System.out.println(i+" projects imported");
			System.out.println("import of nodes took "+ time_taken + "ms.");
		}
		
		public static void addRelations(String fileName, HyperGraph graph)
		{
			// loading the file in the form of list of strings
			List<String> lines = Utils.getAllLines(fileName);
			
			// importing the relations
			Iterator<String> itr = lines.iterator();
			int i=0;
			HGHandle handle1, handle2;
			long start_time = System.nanoTime();
		    while(itr.hasNext()) {
				String line = itr.next();
				String[] arrOfStr = line.split("\t", 2); 
				handle1 = studentHandles.get("st_"+arrOfStr[0]);
				handle2 = studentHandles.get("st_"+arrOfStr[1]);
				graph.add(new HGValueLink("called", handle1, handle2));
				i++;
			}
			long end_time = System.nanoTime();
		    long time_taken = (end_time- start_time)/1000000000;
		    
//			
////			HGHandle duplicateLink = graph.add(new HGPlainLink(handle1, handle2));
//	        List<HGHandle> dupsList = hg.findAll(graph, hg.link(handle1, handle2));
//	        System.out.println("querying for link returned that duplicate Link? :" + dupsList.contains(duplicateLink));
			System.out.println(i+" Relations imported");
			System.out.println("import of relations took "+ time_taken + "s.");
		    
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
	 			
		public static void aQuery(HyperGraph graph) {

			System.out.println("\nDepth first traversing started");
			HGALGenerator adjGen = new DefaultALGenerator(graph);
	        
			// applying DFS
			
			HGTraversal trav= new HGDepthFirstTraversal(studentHandles.get("st_0"), adjGen);
	        while(trav.hasNext()){
	            Pair<HGHandle, HGHandle> pair = trav.next();
	            Object nextElement = graph.get(pair.getSecond());
	            Student next = (Student)nextElement;
	            System.out.print("->" + next.getName());
	        }    
			System.out.println("\nTraversing done");
			
			
			// applying BFS
			System.out.println("BFS started");
			HGTraversal BFS_trav = new HGBreadthFirstTraversal(studentHandles.get("st_0"), adjGen,1);
			while(BFS_trav.hasNext()) {
				Pair<HGHandle, HGHandle> pair = BFS_trav.next();
	            Object nextElement = graph.get(pair.getSecond());
	            Student next = (Student)nextElement;
	            
//	            Object firstElement = graph.get(pair.getFirst());
//	            Student first = (Student)firstElement;
	            System.out.println("->" + next.getName());
			}
			System.out.println("Done");
		}
		
		public static void main(String[] args) {
			
			String databaseLocation = "databases/University/";
			File folder = new File(databaseLocation);
			if(folder.exists())		Utils.deleteFolder(folder);
			HGConfiguration config = Utils.setConfig();
			HyperGraph graph = HGEnvironment.get(databaseLocation, config);
//		    HyperGraph graph = HGEnvironment.get(databaseLocation); 
		    
//			Importing data-set
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
//		    findStudentsUnderProf(graph);
		    
			Scanner sc = new Scanner(System.in);
			String command = new String("exit");
			do{
				System.out.print("> ");
				command = sc.nextLine();
				String[] arr = command.split(" ");
				if(arr[0].equalsIgnoreCase("CREATE")) {
					// creation of a new hyperedge
					System.out.println("CREATING HYPEREDGE");
					int i=0;
//					CREATE HYPEREDGE(
					for(; command.charAt(i)!='('; i++);
//					id, 
					for(; command.charAt)
				}
			}while(!command.equalsIgnoreCase("exit"));
			
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