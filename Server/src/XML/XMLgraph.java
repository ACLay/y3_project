package XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.graph.Graph;
import router.graph.RamGraph;
import Model.Node;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Graph")
public class XMLgraph {

	@XmlElement(name = "ChargeDevices")
	XMLChargers chargers;
	@XmlElement(name = "Edges")
	XMLedges edges;
	
	public XMLgraph(){}
	public XMLgraph(Graph g){
		List<XMLcharger> xmlChargers = new ArrayList<XMLcharger>();
		for(Node c : g.getNodes()){
			xmlChargers.add(new XMLcharger(c));
		}
		chargers = new XMLChargers();
		chargers.setChargers(xmlChargers);
		
		List<XMLedge> xmlEdges = new ArrayList<XMLedge>();
		for(Edge e : g.getEdges()){
			xmlEdges.add(new XMLedge(e));
		}
		edges = new XMLedges();
		edges.setEdges(xmlEdges);
	}
	
	public void setChargers(XMLChargers chargers){
		this.chargers = chargers;
	}
	public void setEdges(XMLedges edges){
		this.edges = edges;
	}
	
	public XMLChargers getChargers(){
		return chargers;
	}
	public XMLedges getEdges(){
		return edges;
	}
	
	public RamGraph makeGraph(){
		RamGraph graph = new RamGraph();
		Map<String,Node> chargerIdMap = new HashMap<String,Node>();
		for(XMLcharger charger : chargers.getChargers()){
			chargerIdMap.put(charger.getChargeDeviceID(), charger.makeCharger());
		}
		Set<Edge> graphEdges = new HashSet<Edge>();
		for(XMLedge edge : edges.getEdges()){
			Node startPoint = chargerIdMap.get(edge.getStartPointId());
			Node endPoint = chargerIdMap.get(edge.getEndPointId());
			graphEdges.add(new Edge(startPoint, endPoint,
					Amount.valueOf(edge.getTravelDistance(), SI.METER),
					Amount.valueOf(edge.getTravelTime(), SI.SECOND)));
		}
		graph.addNodes(chargerIdMap.values());
		graph.addEdges(graphEdges);
		
		return graph;
	}
	
}
