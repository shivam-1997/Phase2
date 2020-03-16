package hgdb.ut;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.algorithms.HGDepthFirstTraversal;
import org.hypergraphdb.algorithms.HGTraversal;
import org.hypergraphdb.atom.HGStats;
import org.hypergraphdb.util.Pair;
import org.hypergraphdb.viewer.HGViewer;
import hgdb.Entities.*;

public class ShowGraph {
	
	public static void print(String message) {
		System.out.println(message);
	}
	
	public static void showGraph(HyperGraph graph) {
		
		
		HGALGenerator adjGen = new DefaultALGenerator(graph, null, null , false, true, false);
		
//		HGTraversal trav= new HGDepthFirstTraversal();
		HGHandle rootHandle = graph.findOne( hg.type(Root.class) );
		if(rootHandle == null) {
			print("No root node present");
			return;
		}
		print("GRAPH:");
		HGTraversal trav= new HGBreadthFirstTraversal(rootHandle, adjGen);
 		
 		
// 		JFrame f = new JFrame();
// 		HGHandle h = graph.getTypeSystem().getTypeHandle(HGStats.class);
// 		HGViewer viewer = new HGViewer(graph, h, 2, adjGen);
// 		f.getContentPane().add(viewer);
// 		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
// 		f.addWindowListener(new WindowAdapter()
// 		{
// 		   public void windowClosing(WindowEvent e)
// 		   {
// 		      f.setVisible(false);
// 		      //uncomment if you'd use only one instance  
// 		      graph.close();
// 		   }
// 		});
// 		f.setMinimumSize(new Dimension(600, 400));
// 		f.setVisible(true);
//	
 		
 		int count = 0;
 		print(""+trav.hasNext());
 		while(trav.hasNext()){
 			print(""+count);
 	        Pair<HGHandle, HGHandle> pair = trav.next();
 	        Object nextElement = graph.get(pair.getSecond());
 	        if(nextElement instanceof Node) {
 	        	print("-> " + ((Node)nextElement).getId());
 	        }
 	        System.out.print("-> " + nextElement.getClass());
 		}
 	
 	}
}
