package router.router;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.Scenario;
import router.State;
import router.comparator.StateTimeComparator;
import router.graph.Graph;
import Model.Car;
import Model.Node;

public class PowerLimitedRouter extends ListPrunedQueueRouter{

	protected Amount<Power> powerLimit;
	
	public PowerLimitedRouter(StateTimeComparator comparator, Amount<Power> powerLimit) {
		super(comparator);
		this.powerLimit = powerLimit;
	}

	public State route(Scenario scenario) {
		
		graph = scenario.getGraph();

		Node startpoint = scenario.getStart();
		Node endpoint = scenario.getFinish();
		Car vehicle = scenario.getCar();

		created = 0;
		stored = 0;
		explored = 0;
		
		Set<Node> usableChargers = new HashSet<Node>();
		for(Node c : graph.getNodes()){
			if(c.maxChargeOutput(vehicle).isGreaterThan(powerLimit)){
				usableChargers.add(c);
			}
		}
		usableChargers.add(startpoint);
		usableChargers.add(endpoint);
		System.out.println(usableChargers.contains(endpoint));
		System.out.println("usable chargers " + usableChargers.size());
		boolean linked = connected(startpoint,endpoint,usableChargers,graph,vehicle.getRange());
		if(!linked){
			return null;
		}
		System.out.println("start and end are linked");
		
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

			for(Edge e : graph.getEdgesFrom(n.getLocation())){
				Amount<Energy> chargeNeeded = vehicle.chargeNeededToTravel(e.getDistance());
				//ensure the vehicle can travel along this edge
				if(chargeNeeded.isLessThan(vehicle.getCapacity()) && usableChargers.contains(e.getEndPoint())){
					Amount<Duration> time;
					Amount<Energy> charge;
					Amount<Length> distance;
					//add new state with lowest charge time
					if(chargeNeeded.isGreaterThan(n.getEnergy())){
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
					} else {
						distance = n.getDistance().plus(e.getDistance());
						time = n.getTime().plus(e.getTravelTime());
						charge = n.getEnergy().minus(chargeNeeded);
						State low = new State(e.getEndPoint(),time,charge,n,distance,vehicle);
						addState(low);
					}

					//add new state with full charge

					//charge the battery
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

		return null;
	}
	
	protected boolean connected(Node startPoint, Node endPoint, Set<Node> usableChargers, Graph g, Amount<Length> range){
		
		if(startPoint.equals(endPoint)){
			return true;
		}
		Set<Node> connected = new HashSet<Node>();
		connected.add(startPoint);
		Set<Node> toAdd = new HashSet<Node>();
		toAdd.add(startPoint);
		Set<Node> justAdded;
		do{
			justAdded = toAdd;
			toAdd = new HashSet<Node>();
			//get all the edges from the connected nodes
			for(Node c : justAdded){
				for(Edge e : g.getEdgesFrom(c)){
					Node linked = e.getEndPoint();
					if(usableChargers.contains(linked) && !connected.contains(linked) && !e.getDistance().isGreaterThan(range)){
						if(linked.equals(endPoint)){
							return true;
						}
						toAdd.add(linked);
					}
				}
			}
			//check for new usable chargers to add
			for(Node c : toAdd){
				connected.add(c);
			}
		}while(!toAdd.isEmpty());
		
		return false;
	}
	
	protected boolean usableEdge(Edge e, Car c){
		Node endNode = e.getEndPoint();
		Amount<Power> p = endNode.maxChargeOutput(c);
		//it's usable if equal to or greater than the power limit
		return p.isGreaterThan(powerLimit);
	}
}
