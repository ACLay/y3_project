package XML;

import java.io.File;

import javax.measure.quantity.Power;
import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jscience.physics.amount.Amount;

import router.ManhattenScenario;
import router.graph.Graph;
import router.graph.RamGraph;
import Model.Charger;

public class GraphIO {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ManhattenScenario ms = new ManhattenScenario(3,3,1,0,0,2,2,null);
		saveGraph(ms.getGraph(), "./testGraph.xml");
		Graph loaded = loadGraph("./testGraph.xml");
		
		Charger c = ms.getGraph().getNodes().iterator().next();
		for(Charger c2: loaded.getNodes()){
			boolean result = c.equals(c2);
			if(result){
				System.out.println(result);
			}
		}
		
		System.out.println();
		System.out.println(loaded.getNodes().equals(ms.getGraph().getNodes()));
		System.out.println(loaded.equals(ms.getGraph()));
		
		Amount<Power> pow = Amount.valueOf(70,SI.WATT);
		Amount<Power> p2 = Amount.valueOf(0.07,SI.KILO(SI.WATT));
		System.out.println();
		System.out.println(pow.equals(p2));
		System.out.println(pow.approximates(p2));
	}
	
	
	
	public static RamGraph loadGraph(String filename){
		XMLgraph xmlGraph = null;
		try {
			
			//The p: at the start of the ChargeDevices tags need removing for this to work
			File file = new File(filename);


			JAXBContext jaxbContext = JAXBContext.newInstance(XMLgraph.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			xmlGraph = (XMLgraph) jaxbUnmarshaller.unmarshal(file);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlGraph.makeGraph();
	}

	public static void saveGraph(Graph graph, String filename){
		
		XMLgraph xmlGraph = new XMLgraph(graph);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLgraph.class);
			
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			File f = new File(filename);
			
			jaxbMarshaller.marshal(xmlGraph,f);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
