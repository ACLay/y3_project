package router;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.physics.amount.Amount;

import router.comparator.AStarStateTimeComparator;
import router.comparator.ChargeTimeStateComparator;
import router.comparator.DistanceStoringAStarComparator;
import router.comparator.StateTimeComparator;
import router.comparator.TravelTimeStateComparator;
import router.graph.RamGraph;
import router.router.ListPrunedQueueRouter;
import router.router.Router;
import Model.Car;
import Model.Charger;
import Model.connectors.Connector;

public class Manhatten2 extends Scenario {
	
	
	public static void main(String[] args) {

		Car car = new Car("Nissan Leaf", Amount.valueOf(Double.MAX_VALUE, SI.METER), Amount.valueOf(2000, SI.JOULE));
		
		List<Amount<Velocity>> speeds = new ArrayList<Amount<Velocity>>();
		speeds.add(Amount.valueOf(30, NonSI.MILES_PER_HOUR));
		/*speeds.add(Amount.valueOf(40, NonSI.MILES_PER_HOUR));
		speeds.add(Amount.valueOf(50, NonSI.MILES_PER_HOUR));
		speeds.add(Amount.valueOf(60, NonSI.MILES_PER_HOUR));*/
		Manhatten2 rm = new Manhatten2(40,40,1,10,10,30,30,Amount.valueOf(500, SI.METER), Amount.valueOf(505, SI.METER), speeds, car);
		System.out.println(rm.getGraph().getEdges().size() + " edges");
		//car.addCompatibleConnectors(Connector.getNewCollection(1, false));

		System.out.println("Graph built");

		StateTimeComparator tComp = new StateTimeComparator();
		
		Amount<Power> fastest = Amount.valueOf(0, SI.WATT);
		for(Charger charger : rm.getGraph().getNodes()){
			Amount<Power> chargerBest = charger.maxChargeOutput(car);
			if(chargerBest.isGreaterThan(fastest)){
				fastest = chargerBest;
			}
		}
		
		AStarStateTimeComparator heurComp = new AStarStateTimeComparator(rm.getFinish(), Amount.valueOf(30, NonSI.MILES_PER_HOUR), fastest);
		DistanceStoringAStarComparator dsComp = new DistanceStoringAStarComparator(rm.getFinish(), Amount.valueOf(30, NonSI.MILES_PER_HOUR), fastest);
		TravelTimeStateComparator travelComp = new TravelTimeStateComparator(rm.getFinish(), Amount.valueOf(30, NonSI.MILES_PER_HOUR), fastest);
		ChargeTimeStateComparator chargeComp = new ChargeTimeStateComparator(rm.getFinish(), Amount.valueOf(30, NonSI.MILES_PER_HOUR), fastest);
		
		Router[] routers = new Router[]{
				new ListPrunedQueueRouter(tComp),
				new ListPrunedQueueRouter(heurComp),
				/*new DualPrunedQueueRouter(tComp),
				new QueuePrunedQueueRouter(tComp),
				new ListPrunedQueueRouter(tComp),
				new ListPrunedQueueRouter(travelComp),
				new ListPrunedQueueRouter(chargeComp),
				new ListPrunedQueueRouter(heurComp),
				new ListPrunedQueueRouter(dsComp),
				new UberPrunedRouter(heurComp)*/};

		System.out.println("Routing");
		
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

	public Manhatten2(int width, int length, double chargerP, int startI, int startJ, int endI, int endJ, Amount<Length> nodeSeparation, Amount<Length> maxEdgeLength, List<Amount<Velocity>> speeds, Car vehicle){
		
		super(new RamGraph(), null, null, vehicle);

		double radiansPerSeparation = nodeSeparation.doubleValue(SI.KILOMETER) / Geography.earthRadius.doubleValue(SI.KILOMETER);
		Set<Charger> chargerSet = new HashSet<Charger>();
		//Build the nodes
		for(int i=0; i< width; i++){
			for(int j=0; j<length; j++){
				String id = i + "," + j;
				double lat = ((double)i) * radiansPerSeparation;
				double lon = ((double)j) * radiansPerSeparation;
				LatLong coordinates = LatLong.valueOf(lat, lon, SI.RADIAN);
				Charger c = new Charger(id, id, id, coordinates, id, id, Connector.getNewCollection(chargerP, true));
				chargerSet.add(c);
				graph.addNode(c);
				if(i == startI && j == startJ){
					start = c;
				}
				if(i == endI && j == endJ){
					finish = c;
				}
			}
		}
		//Build the edges
		Random r = new Random();
		for(Charger edgeStart : chargerSet){
			for(Charger edgeEnd : chargerSet){
				if(edgeStart == edgeEnd){
					continue;
				}
				Amount<Length> separation = Geography.haversineDistance(edgeStart, edgeEnd);
				if(!separation.isGreaterThan(maxEdgeLength)){
					Amount<Velocity> v = speeds.get(r.nextInt(speeds.size()));
					Amount<Duration> t = separation.divide(v).to(SI.SECOND);
					graph.addEdge(new Edge(edgeStart, edgeEnd, separation, t));
				}
			}
		}
		
	}

	public void setCar(Car c){
		this.car = c;
	}
	
}
