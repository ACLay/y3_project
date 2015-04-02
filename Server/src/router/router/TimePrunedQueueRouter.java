package router.router;

import java.util.HashMap;

import router.State;
import router.comparator.StateTimeComparator;
import Model.Charger;

public class TimePrunedQueueRouter extends QueueRouter {
	
	public TimePrunedQueueRouter(StateTimeComparator comparator) {
		super(comparator);
		// TODO Auto-generated constructor stub
	}

	HashMap<Charger, State> fastestStates = new HashMap<Charger, State>();
	
	protected void addState(State s){
		
		/*
		 * store fastest state for each node
		 * store chargiest state for each node
		 * 
		 * new candidate state
		 * case: faster than fastest state
		 * 		> add candidate, set as fastest
		 * case: chargier than chargiest state
		 * 		> add candidate, set as chargiest
		 * case: chargier than fastest, faster than chargiest
		 * 		> add candidate (fastest may not have enough charge, this may be faster than chargiest)
		 */
		
		Charger location = s.getLocation();
		
		boolean addable = false;
		
		if(fastestStates.containsKey(location)){
			State fastest = fastestStates.get(location);
			//A state for a location should be checked:
			//if it's the fastest
			if(s.getTime().isLessThan(fastest.getTime())){
				fastestStates.put(location, s);
				addable = true;
			}
			//if its got the most charge
			if(s.getEnergy().isGreaterThan(fastest.getEnergy())){
				addable = true;
			}
			
		} else {
			fastestStates.put(location, s);
			addable = true;
		}
		
		created++;
		
		if(addable){
			pq.add(s);
			stored++;
		}
		
	}
	
}
