package sample;

import java.util.Collection;
import java.util.Iterator;

import javax.measure.quantity.Power;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import registry.ChargerLoader;
import router.Scenario;
import router.State;
import router.comparator.DistanceStoringAStarComparator;
import router.graph.FolderGraph;
import router.router.ListPrunedQueueRouter;
import router.router.Router;
import Model.Car;
import Model.Node;
import Model.connectors.Connector;

public class FolderGraphTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FolderGraph g = new FolderGraph("./xml/edges/", "./xml/edges/edited_registry.xml");
		Collection<Node> chargers = ChargerLoader.loadFromFile("./xml/edges/edited_registry.xml");
		
		Iterator<Node> it = chargers.iterator();
		Node startPoint = it.next();
		for(int i=0;i<10;i++){
			it.next();
		}
		Node endPoint = it.next();
		
		Amount<Power> mostPowerful = Amount.valueOf(0, SI.WATT);
		for(Node c : chargers){
			for (Connector con : c.getConnectors()){
				if(con.getPower().isGreaterThan(mostPowerful)){
					mostPowerful = con.getPower();
				}
			}
		}

		System.out.println("Startpoint: " + startPoint.getLocationLong() + " :: " + startPoint.getLocationShort());
		System.out.println("Endpoint:   " + endPoint.getLocationLong() + " :: " + endPoint.getLocationShort());
		
		Router r = new ListPrunedQueueRouter(new DistanceStoringAStarComparator(endPoint, Amount.valueOf(70, NonSI.MILES_PER_HOUR), mostPowerful));
		Car tesla = new Car("Tesla Model S 85", Amount.valueOf(426, SI.KILOMETER), Amount.valueOf(85 *60*60, SI.KILO(SI.JOULE)));
		long startTime = System.currentTimeMillis();
		State s = r.route(new Scenario(g, startPoint, endPoint, tesla));
		long endTime = System.currentTimeMillis();
		System.out.println("Routing time: " + (endTime - startTime) + "ms");
		s.printStats();
		r.printStats();
		
		System.out.println("Done");
	}

}
