package router;

import java.util.ArrayList;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.graph.RamGraph;
import Model.Car;
import Model.Charger;
import Model.connectors.Connector;

public class ManhattenScenario extends Scenario {
	
	
	public static void main(String[] args) {

		Car car = new Car("Nissan Leaf", Amount.valueOf(900, SI.METER), Amount.valueOf(2000, SI.JOULE));
		
		ManhattenScenario rm = new ManhattenScenario(100,100,1,0,0,99,99,car);

		car.addCompatibleConnectors(Connector.getNewCollection(1, false));

		System.out.println("Graph built");

		System.out.println("Routing");

		Router[] routers = new Router[]{/*new QueueRouter(),new TimePrunedQueueRouter(),new ChargePrunedQueueRouter(),*/new DualPrunedQueueRouter(), new QueuePrunedQueueRouter(), new QueuePrunedQueueRouter2()};

		for(Router router : routers){

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
				row.add(new Charger(id, id, id, null, id, id, Connector.getNewCollection(chargerP, true)));
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
