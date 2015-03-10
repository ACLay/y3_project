package router.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import router.Edge;
import Model.Charger;
import OSRM.GraphBuilder;

public class CustomPointFolderGraph extends FolderGraph {

	private Map<String, Edge> edgesToEnd;
	private Set<Edge> edgesFromStart;
	
	private Charger startNode;
	private Charger endNode;
	
	private GraphBuilder gb;
	
	public CustomPointFolderGraph(String edgeDirectory, String nodeFile, Charger startPoint, Charger endPoint, GraphBuilder gb) {
		super(edgeDirectory, nodeFile);
		this.gb = gb;
		this.startNode = startPoint;
		this.endNode = endPoint;
		//generate a set of edges from the start node (where new)
		edgesFromStart = new HashSet<Edge>();
		if(!super.containsNode(startPoint)){
			
			for(Charger c : getNodes()){
				Edge edge = makeEdge(startPoint, c);
				if(edge != null){
					edgesFromStart.add(edge);
				}
			}
			
		}
		//generate a set of edges to the finish node (where new)
		edgesToEnd = new HashMap<String, Edge>();
		if(!super.containsNode(endPoint)){
			
			for(Charger c : getNodes()){
				Edge edge = makeEdge(c, endPoint);
				if(edge != null){
					edgesToEnd.put(c.getID(),edge);
				}
			}
			
			if(!super.containsNode(startPoint)){
				Edge edge = makeEdge(startPoint, endPoint);
				if(edge != null){
					edgesToEnd.put(startPoint.getID(), edge);
				}
			}
		}
	}
	
	private Edge makeEdge(Charger startPoint, Charger endPoint){
		Edge edge;
		try {
			edge = gb.makeEdge(startPoint, endPoint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(edge == null){
			return edge;
		}
		if(edge.getDistance().isGreaterThan(gb.getMaxDistance())){
			return null;
		}
		return edge;
	}
	
	//when getting nodes from a point, include freshly generated ones
	public Collection<Edge> getEdgesFrom(Charger edgeStart) {
		//if it's a pre-existing node, include the new edge to the finish node
		if(super.containsNode(edgeStart)){
			Collection<Edge> edges = super.getEdgesFrom(edgeStart);
			Edge toEnd = edgesToEnd.get(edgeStart.getID());
			if(toEnd != null){
				edges.add(toEnd);
			}
			return edges;
		} else if(edgeStart.equals(startNode)){
			return edgesFromStart;
		} else {
			return null;
		}
	}
	
	public boolean containsNode(Charger charger){
		if(startNode.equals(charger)){
			return true;
		} else if (endNode.equals(charger)){
			return true;
		} else {
			return super.containsNode(charger);
		}
	}
	
	public boolean containsEdge(Edge edge){
		if(super.containsEdge(edge)){
			return true;
		} else if(edge.getStartPoint().equals(startNode)){
			return edgesFromStart.contains(edge);
		} else if(edge.getEndPoint().equals(endNode)){
			return edgesToEnd.values().contains(edge);
		} else {
			return false;
		}
	}
}
