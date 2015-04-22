package router.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jscience.physics.amount.Amount;

import registry.ChargerLoader;
import router.Edge;
import Model.Charger;
import XML.XMLedge;
import XML.XMLedges;

public class FolderGraph implements Graph{
	
	private String dir;
	private HashMap<String,Charger> chargers;//Chargers, indexed by ID
	
	public FolderGraph(String edgeDirectory, String nodeFile){
		dir = edgeDirectory;
		
		chargers = new HashMap<String, Charger>();
		Collection<Charger> chargePoints = ChargerLoader.loadFromFile(nodeFile);
		for(Charger c : chargePoints){
			chargers.put(c.getID(), c);
		}
	}

	@Override
	public void addNode(Charger charger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addNodes(Collection<Charger> chargers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEdge(Edge edge) {
		List<Edge> edges = loadEdges(edge.getStartPoint().getID());
		edges.add(edge);
		saveEdges(edges);
	}

	@Override
	public void addEdges(Collection<Edge> edges) {
		for(Edge e : edges){
			addEdge(e);
		}
	}

	@Override
	public Collection<Charger> getNodes() {
		return chargers.values();
	}

	@Override
	public Collection<Edge> getEdges() {
		Collection<Edge> allEdges = new HashSet<Edge>();
		for(Charger charger : chargers.values()){
			allEdges.addAll(getEdgesFrom(charger));
		}
		return allEdges;
	}

	@Override
	public Collection<Edge> getEdgesFrom(Charger edgeStart) {
		return loadEdges(edgeStart.getID());
	}

	@Override
	public boolean containsNode(Charger charger) {
		return chargers.containsValue(charger);
	}

	@Override
	public boolean containsEdge(Edge edge) {
		List<Edge> candidates = loadEdges(edge.getStartPoint().getID());
		return candidates.contains(edge);
	}
	
	private List<Edge> loadEdges(String startID){
		XMLedges xmlEdges = null;
		try {
			
			//The p: at the start of the ChargeDevices tags need removing for this to work
			File file = new File(dir + startID + ".xml");

			if(!file.exists()){
				System.err.println(startID + " does not exist");
				return new ArrayList<Edge>();
			}
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLedges.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			xmlEdges = (XMLedges) jaxbUnmarshaller.unmarshal(file);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Edge> edges = new ArrayList<Edge>();
		for(XMLedge xe : xmlEdges.getEdges()){
			Charger startPoint = chargers.get(xe.getStartPointId());
			Charger endPoint = chargers.get(xe.getEndPointId());
			Edge e = new Edge(startPoint,
					endPoint,
					Amount.valueOf(xe.getTravelDistance(), SI.METER),
					Amount.valueOf(xe.getTravelTime(),SI.SECOND));
			edges.add(e);
		}
		
		return edges;
	}
	
	private void saveEdges(List<Edge> edges){
		String startID = edges.get(0).getStartPoint().getID();
		File f = new File(dir + startID + ".xml");
		
		List<XMLedge> xEdgeList = new ArrayList<XMLedge>();
		for(Edge e : edges){
			xEdgeList.add(new XMLedge(e));
		}
		
		XMLedges xmlEdges = new XMLedges();
		xmlEdges.setEdges(xEdgeList);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLedges.class);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			jaxbMarshaller.marshal(xmlEdges,f);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
