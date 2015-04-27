package router.router;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import Model.Node;

public class FastChargeRouter extends Router{

	Graph graph;
	Map<Node,State> bestStates;
	Set<Node> expanded;
	StateTimeComparator comparator;

	public FastChargeRouter(StateTimeComparator comparator){
		this.comparator = comparator;
	}

	@Override
	public State route(Scenario scenario) {
		// TODO Auto-generated method stub
		graph = scenario.getGraph();
		
		bestStates = new HashMap<Node, State>(graph.getNodes().size());
		expanded = new HashSet<Node>(graph.getNodes().size());

		Node startpoint = scenario.getStart();
		Node endpoint = scenario.getFinish();
		Car vehicle = scenario.getCar();

		created = 0;
		stored = 0;
		explored = 0;

		if(!(graph.containsNode(startpoint) && graph.containsNode(endpoint))){
			return null;
		}
		//Assume starting fully charged
		State state1 = new State(startpoint, Amount.valueOf(0, SI.SECOND), vehicle.getCapacity(), null, Amount.valueOf(0, SI.METER), vehicle);

		addState(state1);

		while(!bestStates.isEmpty()){

			State n = getState();
			if(n.getLocation().equals(endpoint)){
				return n;
			}

			//System.out.println("Exploring from: " + n.getLocation().getLocationLong() + ". States in queue: " + q.size());

			for(Edge e : graph.getEdgesFrom(n.getLocation())){
				if((e.getEndPoint().equals(endpoint) || e.getEndPoint().canCharge(vehicle)) && !expanded.contains(e.getEndPoint())){//
					Amount<Energy> chargeNeeded = vehicle.chargeNeededToTravel(e.getDistance());
					if(chargeNeeded.isGreaterThan(n.getEnergy())){
						continue;
					}
					//ensure the vehicle can travel along this edge
					Amount<Duration> time;
					Amount<Energy> charge;
					Amount<Length> distance;

					distance = n.getDistance().plus(e.getDistance());
					time = n.getTime().plus(e.getTravelTime());
					charge = n.getEnergy().minus(chargeNeeded);

					State low = new State(e.getEndPoint(),time,charge,n,distance,vehicle);
					low = n.moveTo(e.getEndPoint(), e.getTravelTime(), e.getDistance());
					if(e.getEndPoint().equals(endpoint)){
						addState(low);
					} else {
						addState(low.charge(vehicle.getCapacity()));
					}
					if(bestStates.size() % 50 == 0){
						System.out.println(bestStates.size());
					}
				}
			}
			expanded.add(n.getLocation());
		}

		return null;
	}

	protected void addState(State s){
		created++;
		
		Node location = s.getLocation();
		if(bestStates.containsKey(location)){
			State localBest = bestStates.get(location);
			if(!s.getTime().isLessThan(localBest.getTime())){
				return;
			}
		}
		
		bestStates.put(location, s);
		stored++;
	}

	protected State getState(){
		explored++;
		Iterator<State> it = bestStates.values().iterator();
		if(!it.hasNext()){
			return null;
		}
		State bestState = it.next();
		while(it.hasNext()){
			State candidate = it.next();
			//negative if 1st is less than 2nd
			if(comparator.compare(candidate, bestState) < 0){
				bestState = candidate;
			}
		}
		bestStates.remove(bestState.getLocation());
		return bestState;
	}


}
