package router.graph;

import java.util.Collection;
import java.util.HashSet;

import router.Edge;
import Model.Charger;

public class RamGraph implements Graph {

	private Collection<Charger> nodes;
	private Collection<Edge> edges;
	
	public RamGraph(){
		nodes = new HashSet<Charger>();
		edges = new HashSet<Edge>();
	}
	
	public void addNode(Charger charger){
		nodes.add(charger);
	}
	
	public void addNodes(Collection<Charger> chargers){
		for(Charger c : chargers){
			nodes.add(c);
		}
	}
	
	//only add edges if their start and end points are nodes in the graph
	public void addEdge(Edge edge){
		Charger startPoint = edge.getStartPoint();
		Charger endPoint = edge.getEndPoint();
		if(nodes.contains(startPoint) && nodes.contains(endPoint)){
			edges.add(edge);
		}
	}
	
	public void addEdges(Collection<Edge> edges){
		for(Edge e : edges){
			addEdge(e);
		}
	}
	
	public Collection<Charger> getNodes(){
		return nodes;
	}
	
	public Collection<Edge> getEdges(){
		return edges;
	}
	
	public Collection<Edge> getEdgesFrom(Charger edgeStart){
		HashSet<Edge> desired = new HashSet<Edge>();
		for(Edge e : edges){
			if(e.getStartPoint().equals(edgeStart)){
				desired.add(e);
			}
		}
		return desired;
	}
}
