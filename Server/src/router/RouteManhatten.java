package router;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.physics.amount.Amount;

import router.graph.Graph;
import router.graph.RamGraph;
import Model.Car;
import Model.Charger;
import Model.connectors.Connector;
import Model.connectors.Type;

public class RouteManhatten {

	private Graph graph;
	private Charger start;
	private Charger end;

	private Random rnd;

	private static final Integer[] ampages = {7,10,13};


	public static void main(String[] args) {

		RouteManhatten rm = new RouteManhatten();
		rm.initialize(5,5,1,0,0,4,4);

		System.out.println("Graph built");

		Car car = new Car("Nissan Leaf", Amount.valueOf(900, SI.METER), Amount.valueOf(2000, SI.JOULE));
		car.addCompatibleConnectors(rm.assembleConnectors(1, false));

		System.out.println("Routing");

		Router[] routers = new Router[]{new QueueRouter(),new PrunedQueueRouter()};

		for(Router router : routers){

			Long startTime = System.currentTimeMillis();
			State state = rm.route(router, car);
			Long endTime = System.currentTimeMillis();
			state.printRoute();

			System.out.println("Routing time:" + Long.toString(endTime - startTime) + "ms");
			System.out.println("States created:" + router.getCreated());
			System.out.println("States explored:" + router.getExplored());

		}
		System.out.println("10 by 10");
		Router router = new PrunedQueueRouter();
		rm.initialize(10, 10, 0.5, 1, 1, 9, 9);
		Long startTime = System.currentTimeMillis();
		rm.route(router, car);
		Long endTime = System.currentTimeMillis();
		//state.printRoute();

		System.out.println("Routing time:" + Long.toString(endTime - startTime) + "ms");
		System.out.println("States created:" + router.getCreated());
		System.out.println("States explored:" + router.getExplored());
		
		System.out.println("100 by 100");
		rm.initialize(100, 100, 0.5, 1, 1, 9, 9);
		startTime = System.currentTimeMillis();
		rm.route(router, car);
		endTime = System.currentTimeMillis();
		//state.printRoute();

		System.out.println("Routing time:" + Long.toString(endTime - startTime) + "ms");
		System.out.println("States created:" + router.getCreated());
		System.out.println("States explored:" + router.getExplored());
		
		System.out.println("1000 by 1000");
		rm.initialize(1000, 1000, 0.5, 1, 1, 9, 9);
		startTime = System.currentTimeMillis();
		rm.route(router, car);
		endTime = System.currentTimeMillis();
		//state.printRoute();

		System.out.println("Routing time:" + Long.toString(endTime - startTime) + "ms");
		System.out.println("States created:" + router.getCreated());
		System.out.println("States explored:" + router.getExplored());
	}

	public RouteManhatten(){
		rnd = new Random();
	}

	public State route(Router r, Car vehicle){

		Scenario s = new Scenario(graph,start,end,vehicle);
		return r.route(s);
	}

	public void initialize(int width, int length, double chargerP, int startI, int startJ, int endI, int endJ){
		graph = new RamGraph();

		ArrayList<ArrayList<Charger>> chargers = new ArrayList<ArrayList<Charger>>();
		//Build the nodes
		for(int i=0; i< width; i++){
			ArrayList<Charger> row = new ArrayList<Charger>();
			for(int j=0; j<length; j++){
				String id = i + "," + j;
				row.add(new Charger(id, id, id, LatLong.valueOf(i, j, SI.RADIAN), id, id, assembleConnectors(chargerP, true)));
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
		end = chargers.get(endI).get(endJ);

	}

	public Collection<Connector> assembleConnectors(double p, boolean canBeEmpty){

		ArrayList<Connector> connectors = new ArrayList<Connector>();

		if(canBeEmpty){
			for(Type t : Type.values()){
				if(rnd.nextDouble() < p){
					connectors.add(buildConnector(t));
				}
			}
		} else {
			int n = rnd.nextInt(Type.values().length);
			for(Type t : Type.values()){
				if(t.equals(Type.values()[n])){
					connectors.add(buildConnector(t));
				} else if(rnd.nextDouble() < p){
					connectors.add(buildConnector(t));
				}
			}
		}

		return connectors;
	}

	private Connector buildConnector(Type t){
		Integer amps = ampages[rnd.nextInt(ampages.length)];
		Amount<ElectricPotential> voltage = Amount.valueOf(250, SI.VOLT);
		Amount<ElectricCurrent> current = Amount.valueOf(amps,SI.AMPERE);
		Amount<Power> output = Amount.valueOf(250 * amps, SI.WATT);
		Connector c = new Connector(t, output, voltage, current);
		return c;
	}

}
