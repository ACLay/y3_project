package router.router;

import java.util.HashMap;

import router.State;
import router.comparator.StateTimeComparator;
import Model.Node;

public class DualPrunedQueueRouter extends QueueRouter {
	
	public DualPrunedQueueRouter(StateTimeComparator comparator) {
		super(comparator);
	}

	HashMap<Node, State> fastestStates = new HashMap<Node, State>();
	HashMap<Node, State> chargestStates = new HashMap<Node, State>();
	
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
		
		Node location = s.getLocation();
		
		boolean addable = false;
		
		if(fastestStates.containsKey(location)){
			State fastest = fastestStates.get(location);
			State chargest = chargestStates.get(location);
			//A state for a location should be checked:
			//if it's the fastest
			if(s.getTime().isLessThan(fastest.getTime())){
				fastestStates.put(location, s);
				addable = true;
			}
			//if its got the most charge
			if(s.getEnergy().isGreaterThan(chargest.getEnergy())){
				chargestStates.put(location, s);
				addable = true;
			}
			//if its got more charge than the fastest, and is faster than the best charged
			if(s.getTime().isLessThan(chargest.getTime()) && s.getEnergy().isGreaterThan(fastest.getEnergy())){
				addable = true;
			}
			
		} else {
			fastestStates.put(location, s);
			chargestStates.put(location, s);
			addable = true;
		}
		
		created++;
		
		if(addable){
			pq.add(s);
			stored++;
		}
		
	}
	
}
