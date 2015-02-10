package XML;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.graph.RamGraph;
import Model.Charger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Graph")
public class XMLgraph {

	@XmlElement(name = "ChargeDevices")
	XMLChargers chargers;
	@XmlElement(name = "Edges")
	XMLedges edges;
	
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
		Map<String,Charger> chargerIdMap = new HashMap<String,Charger>();
		for(XMLcharger charger : chargers.getChargers()){
			chargerIdMap.put(charger.getChargeDeviceID(), charger.makeCharger());
		}
		Set<Edge> graphEdges = new HashSet<Edge>();
		for(XMLedge edge : edges.getEdges()){
			Charger startPoint = chargerIdMap.get(edge.getStartPointId());
			Charger endPoint = chargerIdMap.get(edge.getEndPointId());
			graphEdges.add(new Edge(startPoint, endPoint,
					Amount.valueOf(edge.getTravelDistance(), SI.METER),
					Amount.valueOf(edge.getTravelTime(), SI.SECOND)));
		}
		graph.addNodes(chargerIdMap.values());
		graph.addEdges(graphEdges);
		
		return graph;
	}
	
}
