package router.graph;

import java.util.Collection;

import router.Edge;
import Model.Charger;

public interface Graph {
	
	public void addNode(Charger charger);
	
	public void addNodes(Collection<Charger> chargers);
	
	//only add edges if their start and end points are nodes in the graph
	public void addEdge(Edge edge);
	
	public void addEdges(Collection<Edge> edges);
	
	public Collection<Charger> getNodes();
	
	public Collection<Edge> getEdges();
	
	public Collection<Edge> getEdgesFrom(Charger edgeStart);
}
