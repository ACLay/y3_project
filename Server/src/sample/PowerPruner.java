package sample;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.quantity.Power;
import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.graph.FolderGraph;
import Model.Charger;
import Model.connectors.Connector;
import XML.XMLChargers;
import XML.XMLcharger;
import XML.XMLedge;
import XML.XMLedges;

public class PowerPruner {

	public static void main(String[] args){

		Amount<Power> minOutput = Amount.valueOf(40, SI.KILO(SI.WATT));
		//load the original graph
		FolderGraph g = new FolderGraph("./xml/edges/", "./xml/edges/edited_registry.xml");
		//build a set of chargers and fast connectors
		Set<Charger> validChargers = new HashSet<Charger>();
		List<XMLcharger> xmlChargerList = new ArrayList<XMLcharger>();

		for(Charger c : g.getNodes()){
			Set<Connector> highPowerConnectors = new HashSet<Connector>();
			for(Connector con : c.getConnectors()){
				if(con.getPower().isGreaterThan(minOutput)){
					highPowerConnectors.add(con);
				}
			}
			if(!highPowerConnectors.isEmpty()){
				Charger charger = new Charger(c.getID(), c.getReference(), c.getName(), c.getCoordinates(), c.getLocationLong(), c.getLocationLong(), highPowerConnectors);
				validChargers.add(charger);
				xmlChargerList.add(new XMLcharger(c));
				System.out.println("charger added " + charger.getConnectors().size());
			}
		}
		System.out.println(validChargers.size() + " chargers");
		Map<Charger,Set<Edge>> edgeSets = new HashMap<Charger,Set<Edge>>();
		//for each node in that set
		for(Charger c : validChargers){
			Set<Edge> validEdges = new HashSet<Edge>();
			//build a reduced set of edges from it
			Collection<Edge> edges = g.getEdgesFrom(c);
			
			for(Edge e : edges){
				for(Charger c2 : validChargers){
					if(e.getEndPoint().getID().equals(c2.getID())){
						validEdges.add(e);
					}
				}
			}
			if(!validEdges.isEmpty()){
				edgeSets.put(c, validEdges);
				System.out.println(validEdges.size() + " edges found");
			}
		}
		System.out.println(edgeSets.keySet().size());
		//save chargers
		XMLChargers xmlChargers = new XMLChargers();
		
		//save edges to a new file
		String dir = "./xml/powerCut2/";
		String xml = ".xml";

		for(Charger c : edgeSets.keySet()){
			
			String id = c.getID();
			File f = new File(dir+id+xml);

			XMLedges xmlEdges = new XMLedges();
			
			List<XMLedge> xEdgeList = new ArrayList<XMLedge>();
			Set<Edge> edges = edgeSets.get(c);
			for(Edge e : edges){
				xEdgeList.add(new XMLedge(e));
			}
			
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
		
		xmlChargers.setChargers(xmlChargerList);
		File f = new File("./xml/powerCut2/nodes.xml");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLChargers.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(xmlChargers,f);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
