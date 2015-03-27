package router;

import java.util.ArrayList;

import javax.measure.quantity.Power;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.physics.amount.Amount;

import router.graph.RamGraph;
import Model.Car;
import Model.Charger;
import Model.connectors.Connector;

public class ManhattenScenario extends Scenario {
	
	
	public static void main(String[] args) {

		Car car = new Car("Nissan Leaf", Amount.valueOf(900, SI.METER), Amount.valueOf(2000, SI.JOULE));
		
		ManhattenScenario rm = new ManhattenScenario(100,100,1,25,25,75,75,car);

		car.addCompatibleConnectors(Connector.getNewCollection(1, false));

		System.out.println("Graph built");

		System.out.println("Routing");

		StateTimeComparator tComp = new StateTimeComparator();
		
		Amount<Power> fastest = Amount.valueOf(Double.MIN_VALUE, SI.WATT);
		for(Charger charger : rm.getGraph().getNodes()){
			Amount<Power> chargerBest = charger.maxChargeOutput(car);
			if(chargerBest.isGreaterThan(fastest)){
				fastest = chargerBest;
			}
		}
		
		AStarStateTimeComparator heurComp = new AStarStateTimeComparator(rm.getFinish(), Amount.valueOf(30, NonSI.MILES_PER_HOUR), fastest);
		DistanceStoringAStarComparator dsComp = new DistanceStoringAStarComparator(rm.getFinish(), Amount.valueOf(30, NonSI.MILES_PER_HOUR), fastest);
		
		Router[] routers = new Router[]{/*new DualPrunedQueueRouter(tComp), new QueuePrunedQueueRouter(tComp), new ListPrunedQueueRouter(tComp),*/ new ListPrunedQueueRouter(heurComp),/* new ListPrunedQueueRouter(dsComp),*/ new UberPrunedRouter(heurComp) };

		for(Router router : routers){
			
			System.gc();
			
			Long startTime = System.currentTimeMillis();
			State state = router.route(rm);
			Long endTime = System.currentTimeMillis();
			//state.printRoute();
			state.printStats();
			System.out.println("Routing time:" + Long.toString(endTime - startTime) + "ms");
			router.printStats();
			System.out.println();
		}

	}

	public ManhattenScenario(int width, int length, double chargerP, int startI, int startJ, int endI, int endJ, Car vehicle){
		
		super(new RamGraph(), null, null, vehicle);

		ArrayList<ArrayList<Charger>> chargers = new ArrayList<ArrayList<Charger>>();
		//Build the nodes
		for(int i=0; i< width; i++){
			ArrayList<Charger> row = new ArrayList<Charger>();
			for(int j=0; j<length; j++){
				String id = i + "," + j;
				double lat = (0.5*i)/6353;
				double lon = (0.5*j)/6353;
				LatLong coordinates = LatLong.valueOf(lat, lon, SI.RADIAN);
				row.add(new Charger(id, id, id, coordinates, id, id, Connector.getNewCollection(chargerP, true)));
			}
			chargers.add(row);
			graph.addNodes(row);
		}
		//Build the edges
		for(int i=0; i<width; i++){
			for(int j=0; j<length; j++){
				//add the edge in i+
				if(i != width-1){
					//500m at 30mph in seconds
					graph.addEdge(new Edge(chargers.get(i).get(j), chargers.get(i+1).get(j), Amount.valueOf(500, SI.METER), Amount.valueOf(37.3, SI.SECOND)));
				}
				//add the edge in i-
				if(i != 0){
					graph.addEdge(new Edge(chargers.get(i).get(j), chargers.get(i-1).get(j), Amount.valueOf(500, SI.METER), Amount.valueOf(37.3, SI.SECOND)));
				}
				//add the edge in j+
				if(j != length-1){
					graph.addEdge(new Edge(chargers.get(i).get(j), chargers.get(i).get(j+1), Amount.valueOf(500, SI.METER), Amount.valueOf(37.3, SI.SECOND)));
				}
				//add the edge in j-
				if(j != 0){
					graph.addEdge(new Edge(chargers.get(i).get(j), chargers.get(i).get(j-1), Amount.valueOf(500, SI.METER), Amount.valueOf(37.3, SI.SECOND)));
				}
			}
		}

		start = chargers.get(startI).get(startJ);
		finish = chargers.get(endI).get(endJ);
		
	}

}
