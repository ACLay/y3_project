package registry;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jscience.physics.amount.Amount;

import router.Edge;
import Model.Node;
import OSRM.GraphBuilder;
import OSRM.QueryBuilder;
import XML.XMLChargers;
import XML.XMLcharger;

public class ChargerLoader {

	String url = "http://chargepoints.dft.gov.uk/api/retrieve/registry/";

	public static Collection<Node> loadFromFile(String filename){
		//TODO remove p: automatically
		
		//load chargers
		ArrayList<Node> chargers = new ArrayList<Node>();
		try {

			File registry = new File(filename);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLChargers.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			XMLChargers xmlChargers = (XMLChargers) jaxbUnmarshaller.unmarshal(registry);

			
			int errors = 0;
			for(XMLcharger xmlC : xmlChargers.getChargers()){
				try{
					chargers.add(xmlC.makeCharger());
				} catch (Exception e){
					errors++;
				}
			}
			
			System.out.println(chargers.size());
			System.out.println(errors + " error(s)");
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		//sanity checks
		Iterator<Node> it = chargers.iterator();
		while(it.hasNext()){
			Node c = it.next();
			if(c.getConnectors().isEmpty()){
				it.remove();
				System.err.println("Chargepoint removed");
			}
		}
		return chargers;
	}

	public static void main(String[] args){
		Collection<Node> chargers = loadFromFile("./xml/edited_registry.xml");
		Iterator<Node> it = chargers.iterator();
		for(int i=0; i<100; i++){
			it.next();
		}
		Node c1 = it.next();
		for(int i=0; i<150; i++){
			it.next();
		}
		Node c2 = it.next();
		
		GraphBuilder gb = new GraphBuilder(new QueryBuilder("127.0.0.1", 5000), Amount.valueOf(426, SI.KILOMETER));
		Edge edge;
		try {
			edge = gb.makeEdge(c1, c2);
			System.out.println(c1.getLocationLong() + " :: " + c1.getLocationShort());
			System.out.println(c1.getCoordinates());
			System.out.println(c2.getLocationLong() + " :: " + c2.getLocationShort());
			System.out.println(c2.getCoordinates());
			System.out.println(edge.getDistance());
			System.out.println(edge.getTravelTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
