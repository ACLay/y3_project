package router.graph;

import java.util.Collection;
import java.util.HashSet;

import router.Edge;
import Model.Node;

public class RamGraph implements Graph {

	private Collection<Node> nodes;
	private Collection<Edge> edges;
	
	public RamGraph(){
		nodes = new HashSet<Node>();
		edges = new HashSet<Edge>();
	}
	
	public void addNode(Node charger){
		nodes.add(charger);
	}
	
	public void addNodes(Collection<Node> chargers){
		for(Node c : chargers){
			nodes.add(c);
		}
	}
	
	//only add edges if their start and end points are nodes in the graph
	public void addEdge(Edge edge){
		Node startPoint = edge.getStartPoint();
		Node endPoint = edge.getEndPoint();
		if(nodes.contains(startPoint) && nodes.contains(endPoint)){
			edges.add(edge);
		}
	}
	
	public void addEdges(Collection<Edge> edges){
		for(Edge e : edges){
			addEdge(e);
		}
	}
	
	public Collection<Node> getNodes(){
		return nodes;
	}
	
	public Collection<Edge> getEdges(){
		return edges;
	}
	
	public Collection<Edge> getEdgesFrom(Node edgeStart){
		HashSet<Edge> desired = new HashSet<Edge>();
		for(Edge e : edges){
			if(e.getStartPoint().equals(edgeStart)){
				desired.add(e);
			}
		}
		return desired;
	}

	@Override
	public boolean containsNode(Node charger) {
		return nodes.contains(charger);
	}

	@Override
	public boolean containsEdge(Edge edge) {
		return edges.contains(edge);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RamGraph other = (RamGraph) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

	


	
}
