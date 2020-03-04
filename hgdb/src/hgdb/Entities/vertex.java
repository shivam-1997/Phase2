/**
 * 
 */
package hgdb.Entities;

import java.util.ArrayList;
import java.util.List;

import org.hypergraphdb.HGHandle;

/**
 * @author shivam
 *
 */

public class vertex {

	List<HGHandle> listOfHyperEdges = new ArrayList<HGHandle>();
	
	public vertex() {
		// nullary constructor
	}
	
	// onUpdate
	public void onUpdate() {
		// 1. trigger all the hypredges 
		//    of whom current node is source of
		for(HGHandle handles : listOfHyperEdges) {
			
		}
		// 2. send message to all parents 
		
		
	}
	// onAdd
	
	// onDelete
}
