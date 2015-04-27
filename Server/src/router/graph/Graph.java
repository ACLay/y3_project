package router.graph;

import java.util.Collection;

import router.Edge;
import Model.Node;

public interface Graph {
	
	public void addNode(Node charger);
	
	public void addNodes(Collection<Node> chargers);
	
	//only add edges if their start and end points are nodes in the graph
	public void addEdge(Edge edge);
	
	public void addEdges(Collection<Edge> edges);
	
	public Collection<Node> getNodes();
	
	public Collection<Edge> getEdges();
	
	public Collection<Edge> getEdgesFrom(Node edgeStart);
	
	public boolean containsNode(Node charger);
	
	public boolean containsEdge(Edge edge);
}
