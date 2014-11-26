package router;

import java.util.Comparator;
import java.util.PriorityQueue;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.graph.Graph;
import Model.Car;
import Model.Charger;

public class Router {

	Graph graph;
	Charger start;
	Charger finish;
	
	public Router(Graph g) {
		graph = g;
	}
	
	public State route(Charger startpoint, Charger endpoint, Car vehicle){
		
		start = startpoint; finish = endpoint;
		
		PriorityQueue<State> pq = new PriorityQueue<State>(3,new StateTimeComparator());
		if(!(graph.containsNode(startpoint) && graph.containsNode(endpoint))){
			return null;
		}
		//Assume starting fully charged
		State state1 = new State(startpoint, Amount.valueOf(0, SI.SECOND), vehicle.getCapacity(), null, Amount.valueOf(0, SI.METER), vehicle);
		
		pq.add(state1);
		
		while(!pq.isEmpty()){
			
			State n = pq.poll();
			if(n.getLocation().equals(endpoint)){
				return n;
			}
			
			//System.out.println("Exploring from: " + n.getLocation().getLocationLong() + ". States in queue: " + q.size());
			
			for(Edge e : graph.getEdgesFrom(n.getLocation())){
				Amount<Energy> chargeNeeded = vehicle.chargeNeededToTravel(e.getDistance());
				//ensure the vehicle can travel along this edge
				if(chargeNeeded.isLessThan(vehicle.getCapacity())){
					Amount<Duration> time;
					Amount<Energy> charge;
					Amount<Length> distance;
					//add new state with lowest charge time
					if(chargeNeeded.isGreaterThan(n.getEnergy())){
						if(n.getLocation().canCharge(vehicle)){
							//charge the battery
							//get charge time
							Amount<Energy> toCharge = chargeNeeded.minus(n.getEnergy());
							Amount<Power> chargePower = n.getLocation().maxChargeOutput(vehicle);
							Amount<Duration> chargeTime = toCharge.divide(chargePower).to(SI.SECOND);
							State charged = new State(n.getLocation(),n.getTime().plus(chargeTime),chargeNeeded,n,n.getDistance(),n.getCar());
							//move along the edge
							distance = charged.getDistance().plus(e.getDistance());
							time = charged.getTime().plus(e.getTravelTime());
							charge = charged.getEnergy().minus(chargeNeeded);
							State low = new State(e.getEndPoint(),time,charge,charged,distance,vehicle);
							pq.add(low);
						}
					} else {
						distance = n.getDistance().plus(e.getDistance());
						time = n.getTime().plus(e.getTravelTime());
						charge = n.getEnergy().minus(chargeNeeded);
						State low = new State(e.getEndPoint(),time,charge,n,distance,vehicle);
						pq.add(low);
					}
					
					//add new state with full charge

					//charge the battery
					if(n.getLocation().canCharge(vehicle)){
						//get charge time
						Amount<Energy> toCharge = n.getCar().getCapacity().minus(n.getEnergy());
						Amount<Power> chargePower = n.getLocation().maxChargeOutput(vehicle);

						Amount<Duration> chargeTime = toCharge.divide(chargePower).to(SI.SECOND);
						State chargedState = new State(n.getLocation(),n.getTime().plus(chargeTime),vehicle.getCapacity(),n,n.getDistance(),n.getCar());
						//move along the edge
						distance = chargedState.getDistance().plus(e.getDistance());
						time = chargedState.getTime().plus(e.getTravelTime());
						charge = chargedState.getEnergy().minus(chargeNeeded);
						State high = new State(e.getEndPoint(),time,charge,chargedState,distance,vehicle);
						pq.add(high);
					}
				}
			}
			
		}
		
		return null;
	}
	
	static class StateTimeComparator implements Comparator<State>{
		@Override
		public int compare(State o1, State o2) {
			return o1.getTime().compareTo(o2.getTime());
		}
		
	}
	
}
