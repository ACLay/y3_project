package router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import Model.Car;
import Model.Charger;

public class UberPrunedRouter extends ListPrunedQueueRouter {

	protected Map<State, List<State>> successors;
	protected long successionPruned = 0;
	protected long expansionBlocked = 0;
	
	public UberPrunedRouter(StateTimeComparator comparator) {
		super(comparator);
		successors = new HashMap<State, List<State>>();
	}

	public State route(Scenario scenario) {
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
							if(checkUsable(charged)){
								//move along the edge
								distance = charged.getDistance().plus(e.getDistance());
								time = charged.getTime().plus(e.getTravelTime());
								charge = charged.getEnergy().minus(chargeNeeded);
								State low = new State(e.getEndPoint(),time,charge,charged,distance,vehicle);
								addState(low);
							}
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
						if(checkUsable(chargedState)){
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

		}

		return null;
	}
	
	protected boolean checkUsable(State s){
		ArrayList<State> candidates;
		if(candidateStates.containsKey(s.getLocation())){
			candidates = candidateStates.get(s.getLocation());
		} else {
			return true;
		}
		
		for(State oldState : candidates){
			//if there exists a state that the new one is no better than in either stat, the new one shouldn't be added
			if(!s.isFasterThan(oldState) && !s.isChargierThan(oldState)){//TODO this in the superior function
				expansionBlocked++;
				return false;
			//if there exists an old state with no better stats than the new one, the old one should be dropped
			}
		}
		
		return true;
	}
	
	//TODO check inferiority of charged states before advancing
	
	/*The idea behind these alterations is thus:
	 *If a state is found to be inferior to a new one,
	 *then the new one will generate successors superior to the old ones
	 *These successors of the inferior state should be removed to prevent unnecessary expansion
	 */
	protected void storeState(State s){
		super.storeState(s);
		
		State parent = getParent(s);
		//if the parent hasn't got successors yet
		if(!successors.containsKey(parent)){
			//make it a new list
			successors.put(parent, new ArrayList<State>());
		}
		//get the parents successors and add the new one
		List<State> siblings = successors.get(parent);
		siblings.add(s);
	}

	protected void removeState(State s){
		//remove this state from records
		super.removeState(s);

		if(successors.containsKey(s)){
			//remove its successors from records
			for(State child : successors.get(s)){
				removeState(child);
				successionPruned ++;
			}
			//remove the list of its successors
			successors.remove(s);
		}
		//remove it from it's parents successor list - causes concurrency errors
		/*State parent = getParent(s);

		
		if(successors.containsKey(parent)){
			List<State> siblings = successors.get(parent);
			siblings.remove(s);
		}*/
	}
	
	protected State getParent(State child){
		State parent = child.getPrevious();
		if(parent != null && parent.getPrevious() != null){
			if(parent.getLocation().equals(parent.getPrevious().getLocation())){
				parent = parent.getPrevious();
			}
		}
		return parent;
	}
	
	public long getSuccessionDropped(){
		return successionPruned;
	}
	
	public void printStats(){
		super.printStats();
		System.out.println("Dropped via succession: " + getSuccessionDropped());
		System.out.println("Charge blocked: " + expansionBlocked);
	}
}
