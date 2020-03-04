package hgdb;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.algorithms.*;
import org.hypergraphdb.util.Pair;


import hgdb.Entities.Student;

public interface FunctionalNode {
	void trigger_function(HyperGraph graph, HGHandle handle);
	int getRes();
}

class CountMaleStudents implements FunctionalNode{

	int res=0;

	public CountMaleStudents() {}

	@Override
	public int getRes() {	return res;	}
	public void setRes(int res) {	this.res=res;	}

	@Override
	public void trigger_function(HyperGraph graph, HGHandle handle) {
		// TODO Auto-generated method stub
		Object obj = graph.get(handle);
		if(obj.getClass()==Student.class)	{
			Student studentObj = (Student)obj;
			if(studentObj.getGender() == 'm') {
				res++;
			}
		}
//		System.out.print("male done\n");
	}
	
}

class CountFemaleStudents implements FunctionalNode{

	int res=0;

	public CountFemaleStudents() {}

	@Override
	public int getRes() {	return res;	}
	public void setRes(int res) {	this.res=res;	}

	@Override
	public void trigger_function(HyperGraph graph, HGHandle handle) {
		// TODO Auto-generated method stub
		Object obj = graph.get(handle);
		if(obj.getClass()==Student.class)	{
			Student studentObj = (Student)obj;
			if(studentObj.getGender() == 'f') {
				res++;
			}
		}
//		System.out.print("female done\n");
	}
	
}
class CountStudents implements FunctionalNode{

	int res=0;

	public CountStudents() {}

	@Override
	public int getRes() {	return res;	}
	public void setRes(int res) {	this.res=res;	}

	@Override
	public void trigger_function(HyperGraph graph, HGHandle handle) {
		
//		public HGBreadthFirstTraversal(HGHandle startAtom,
//                HGALGenerator adjListGenerator,
//                int maxDistance);
		HGALGenerator adjGen = new DefaultALGenerator(graph, null, null , false, true, false);
		HGTraversal trav= new HGBreadthFirstTraversal(handle, adjGen,1);
		res=0;
		while(trav.hasNext()){
	        Pair<HGHandle, HGHandle> pair = trav.next();
	        Object nextElement = graph.get(pair.getSecond());
	        if(nextElement instanceof FunctionalNode) {
	        	int n =((FunctionalNode)nextElement).getRes(); 
	        	res += n;
	        }
//	        System.out.print(nextElement.getClass()+"\n");
		}
//		System.out.print("total done\n");
	}
}