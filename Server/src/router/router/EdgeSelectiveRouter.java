package router.router;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javax.measure.quantity.Energy;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.Scenario;
import router.State;
import router.comparator.StateTimeComparator;
import router.graph.Graph;
import Model.Car;
import Model.Node;

public class EdgeSelectiveRouter extends Router {

	Graph graph;
	PriorityQueue<State> pq;
	StateTimeComparator comparator;
	
	State fastestToEnd;
	Map<Node,Map<Node,State>> minimalChargeStates;
	Map<Node,State> fullChargeStates;
	Map<State,Integer> useCounts;
	
	Node endpoint;
	/*
	 * perform charging of a state when it's added
	 * on adding, store the number of times its used
	 * when a new state displaces it from a use, reduce the old ones count
	 * if its count hits 0, remove it from the queue
	 * only add to the queue if it has uses
	 */

	public EdgeSelectiveRouter(StateTimeComparator comparator){
		this.comparator = comparator;		
	}

	@Override
	public State route(Scenario scenario) {
		// TODO Auto-generated method stub
		graph = scenario.getGraph();

		Node startpoint = scenario.getStart();
		endpoint = scenario.getFinish();
		Car vehicle = scenario.getCar();

		useCounts = new HashMap<State,Integer>();
		fullChargeStates = new HashMap<Node,State>();
		minimalChargeStates = new HashMap<Node,Map<Node,State>>();
		for(Node c : graph.getNodes()){
			minimalChargeStates.put(c, new HashMap<Node,State>());
		}
		
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
			Node location = n.getLocation();
			if(location.equals(endpoint)){
				return n;
			}

			//for each scenario this should be expanded along
			if(fullChargeStates.get(location).equals(n)){
				//if its the fastest to full charge at a node, expand it along all edges
				for(Edge edge : graph.getEdgesFrom(location)){
					//if it can travel down the edge
					if(!vehicle.chargeNeededToTravel(edge.getDistance()).isGreaterThan(n.getEnergy())){
						State travelled = n.moveTo(edge.getEndPoint(), edge.getTravelTime(), edge.getDistance());
						addState(travelled);
					}
				}
			} else {
				int expansions = 0;
				//if its the fastest minimal charge at a node for an edge, expand along that edge
				Map<Node,State> minimalCharges = minimalChargeStates.get(location);
				for(Edge edge : graph.getEdgesFrom(location)){
					Node edgeEnd = edge.getEndPoint();
					if(n.equals(minimalCharges.get(edgeEnd))){
						State travelled = n.moveTo(edgeEnd, edge.getTravelTime(), edge.getDistance());
						addState(travelled);
						expansions++;
					}
				}
				if(expansions != 0){
					System.err.println("No expansions performed for polled state");
				}
			}
		}

		return null;
	}

	protected void addState(State s){
		//store if it's at the destination, don't charge then
		if(s.getLocation().equals(endpoint)){
			if(fastestToEnd == null){
				enqueue(s);
				fastestToEnd=s;
			} else if(s.isFasterThan(fastestToEnd)) {
				enqueue(s);
				fastestToEnd=s;
			}
			return;
		}
		int uses = 0;
		//fully charge and see if it's the new fastest full charge
		boolean canCharge = s.getLocation().canCharge(s.getCar());
		if(canCharge){
			State fullCharge = s.charge(s.getCar().getCapacity());
			State fastestFullCharge = fullChargeStates.get(s.getLocation());
			if(fastestFullCharge == null){
				fullChargeStates.put(s.getLocation(), fullCharge);
				enqueue(fullCharge);
				increaseCount(fullCharge);
			} else if(fullCharge.isFasterThan(fastestFullCharge)){
				reduceCount(fastestFullCharge);
				fullChargeStates.put(s.getLocation(), fullCharge);
				enqueue(fullCharge);
				increaseCount(fullCharge);
			}
		}
		//minimally charge for each edge and see if its fastest
		Map<Node,State> minimals = minimalChargeStates.get(s.getLocation());
		for(Edge e : graph.getEdgesFrom(s.getLocation())){
			Amount<Energy> chargeNeeded  = s.getCar().chargeNeededToTravel(e.getDistance());
			if(chargeNeeded.isGreaterThan(s.getCar().getCapacity())){
				//if it can't make it down the edge, don't bother with the checks
				continue;
			}
			State minimal = minimals.get(e.getEndPoint());
			if(!chargeNeeded.isGreaterThan(s.getEnergy())){
				//if there's already enough charge in the battery
				if(minimal == null){
					//store it
					minimals.put(s.getLocation(), s);
					enqueue(s);
					uses++;
				} else if(s.isFasterThan(minimal)){
					minimals.put(s.getLocation(), s);
					reduceCount(minimal);
					enqueue(s);
					uses++;
				}
			} else if(canCharge){
				State charged = s.charge(chargeNeeded);
				if(minimal == null){
					minimals.put(s.getLocation(),charged);
					enqueue(charged);
					increaseCount(charged);
				} else if(charged.isFasterThan(minimal)){
					minimals.put(s.getLocation(), charged);
					reduceCount(minimal);
					enqueue(charged);
					increaseCount(charged);
				}
			}
		}
		if(uses != 0){
			useCounts.put(s, uses);
		}
	}
	
	protected void enqueue(State s){
		pq.add(s);
		created++;
		stored++;
	}

	protected void reduceCount(State s){
		Integer currentUses = useCounts.get(s);
		Integer newUses = currentUses - 1;
		if(newUses.equals(0)){
			pq.remove(s);
		}
	}
	
	protected void increaseCount(State s){
		Integer currentUses = useCounts.get(s);
		if(currentUses == null){
			useCounts.put(s, 1);
		} else {
			useCounts.put(s, currentUses + 1);
		}
	}
	
	protected State getState(){
		explored++;
		State s = pq.poll();
		useCounts.remove(s);
		return s;
	}


}
