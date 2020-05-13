package hgdb;

import org.hypergraphdb.*;
import hgdb.cli.CLI;

public class CreateInRun {
		public static void print(String message) {
			System.out.println(message);
		}
		static String databaseLocation = "databases/University/";
		
 		public static void main(String[] args) {
			
 			
 			HyperGraph graph = HGEnvironment.get(databaseLocation);
 			
			CLI cli = new CLI();
	
 			
//			ShowGraph.showGraph(graph);
 			cli.startCLI(graph);
 			print("Closing the graph");
 			graph.close();
		}
		
}

