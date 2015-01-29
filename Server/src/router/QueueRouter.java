package router;

import java.util.Comparator;
import java.util.PriorityQueue;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.graph.Graph;
import Model.Car;
import Model.Charger;

public class QueueRouter extends Router{

	Graph graph;
	PriorityQueue<State> pq;

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

		pq = new PriorityQueue<State>(3,new StateTimeComparator());
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
							addState(low);
						}
					} else {
						distance = n.getDistance().plus(e.getDistance());
						time = n.getTime().plus(e.getTravelTime());
						charge = n.getEnergy().minus(chargeNeeded);
						State low = new State(e.getEndPoint(),time,charge,n,distance,vehicle);
						addState(low);
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
						addState(high);
					}
				}
			}

		}

		return null;
	}

	protected void addState(State s){
		pq.add(s);
		created++;
		stored++;
	}

	private State getState(){
		explored++;
		return pq.poll();
	}

	static class StateTimeComparator implements Comparator<State>{
		@Override
		public int compare(State o1, State o2) {
			return o1.getTime().compareTo(o2.getTime());
		}

	}

	static class AStarStateTimeComparator implements Comparator<State>{

		private Charger endpoint;
		final Amount<Velocity> maxSpeed = Amount.valueOf(70, NonSI.MILES_PER_HOUR);
		final Amount<Length> earthRadius = Amount.valueOf(6353, SI.KILOMETRE);
		
		public AStarStateTimeComparator(Charger endpoint){
			this.endpoint = endpoint;
		}
		
		@Override
		public int compare(State s1, State s2) {
			
			Amount<Duration> state1time = heuristicTime(s1);
			Amount<Duration> state2time = heuristicTime(s2);
			
			
			return state1time.compareTo(state2time);
		}
		
		private Amount<Length> haversineDistance(Charger s1, Charger s2){
			double lat1 = s1.getCoordinates().latitudeValue(SI.RADIAN);
			double lon1 = s1.getCoordinates().longitudeValue(SI.RADIAN);
			
			double lat2 = s2.getCoordinates().latitudeValue(SI.RADIAN);
			double lon2 = s1.getCoordinates().longitudeValue(SI.RADIAN);
			
			double internal = Math.pow(Math.sin((lat2 - lat1)/2), 2) + Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin((lon2 - lon1)/2),2);
			double root = Math.sqrt(internal);
			return earthRadius.times(2*Math.asin(root));
		}
		
		private Amount<Duration> heuristicTime(State s1){
			Amount<Length> distanceEstimate = haversineDistance(s1.getLocation(),endpoint);
			Amount<Duration> timeEstimate = distanceEstimate.divide(maxSpeed).to(SI.SECOND);
			return s1.getTime().plus(timeEstimate);
		}
		
	}

}
