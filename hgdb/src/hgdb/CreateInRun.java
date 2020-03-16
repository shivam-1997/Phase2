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
		static String databaseLocation = "databases/University/";
		static File folder = new File(databaseLocation);
		
		
		
 		public static void main(String[] args) {
			
 			if(folder.exists())	Utils.deleteFolder(folder);
 			
 			HyperGraph graph = HGEnvironment.get(databaseLocation);
 			
			CLI cli = new CLI();
			
			cli.createGraph(graph);
 			ShowGraph.showGraph(graph);
 			
 			cli.startCLI(graph);
 			
 			graph.close();
		}
		
}
