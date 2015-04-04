package router.router;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.Scenario;
import router.State;
import router.comparator.StateTimeComparator;
import router.graph.Graph;
import Model.Car;
import Model.Charger;

public class TimeOnlyRouter extends Router{

	Graph graph;
	PriorityQueue<State> pq;
	Map<Charger,State> bestStates;
	StateTimeComparator comparator;

	public TimeOnlyRouter(StateTimeComparator comparator){
		this.comparator = comparator;
		bestStates = new HashMap<Charger, State>();
	}

	@Override
	public State route(Scenario scenario) {
		// TODO Auto-generated method stub
		graph = scenario.getGraph();

		Charger startpoint = scenario.getStart();
		Charger endpoint = scenario.getFinish();
		Car vehicle = scenario.getCar();

		created = 0;
		stored = 0;
		explored = 0;

		pq = new PriorityQueue<State>(3, comparator);
		if(!(graph.containsNode(startpoint) && graph.containsNode(endpoint))){
			return null;
		}
		//Assume starting fully charged
		State state1 = new State(startpoint, Amount.valueOf(0, SI.SECOND), vehicle.getCapacity(), null, Amount.valueOf(0, SI.METER), vehicle);

		addState(state1);

		while(!pq.isEmpty()){

			State n = getState();
			if(n.getLocation().equals(endpoint)){
				return n;
			}

			//System.out.println("Exploring from: " + n.getLocation().getLocationLong() + ". States in queue: " + q.size());

			for(Edge e : graph.getEdgesFrom(n.getLocation())){
				Amount<Energy> chargeNeeded = vehicle.chargeNeededToTravel(e.getDistance());
				//ensure the vehicle can travel along this edge
				Amount<Duration> time;
				Amount<Energy> charge;
				Amount<Length> distance;

				distance = n.getDistance().plus(e.getDistance());
				time = n.getTime().plus(e.getTravelTime());
				charge = n.getEnergy().minus(chargeNeeded);
				State low = new State(e.getEndPoint(),time,charge,n,distance,vehicle);
				addState(low);

			}

		}

		return null;
	}

	protected void addState(State s){
		created++;
		Charger location = s.getLocation();
		if(bestStates.containsKey(location)){
			State localBest = bestStates.get(location);
			if(s.getTime().isLessThan(localBest.getTime())){
				pq.remove(localBest);
			} else {
				return;
			}
		}
		bestStates.put(location, s);
		pq.add(s);
		stored++;
	}

	protected State getState(){
		explored++;
		State s = pq.poll();
		return s;
	}


}
