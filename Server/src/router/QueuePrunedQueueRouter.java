package router;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Model.Charger;

public class QueuePrunedQueueRouter extends QueueRouter {

	HashMap<Charger,ArrayList<State>> candidateStates = new HashMap<Charger,ArrayList<State>>();
	Set<State> inferiors = new HashSet<State>();
	
	protected void addState(State s){
		created++;
		//get the chargers candidates, which are kept ordered by time and charge
		//if new state is faster than a state, add it in place
		//if it's slower, then only keep trying if its better charged
		ArrayList<State> candidates;
		if(candidateStates.containsKey(s.getLocation())){
			candidates = candidateStates.get(s.getLocation());
		} else {
			candidates = new ArrayList<State>();
			candidateStates.put(s.getLocation(), candidates);
			pq.add(s);
			candidates.add(s);
			stored++;
			return;
		}
		
		for(int i=0; i < candidates.size(); i++){
			State prev = candidates.get(i);
			if(s.isFasterThan(prev)){
				//add in place
				candidates.add(i, s);
				pq.add(s);
				stored++;
				//TODO remove now inferior states
				break;
			} else {
				if(!s.isChargierThan(prev)){
					break;
				}
			}
			if(i == candidates.size() - 1){
				if(s.isChargierThan(prev)){
					candidates.add(s);
					pq.add(s);
					stored++;
				}
			}
		}
		
		
		for(State old : inferiors){
			pq.remove(old);
			candidates.remove(old);
		}
		inferiors.clear();
	}
	
	static class StateDualComparator implements Comparator<State>{
		@Override
		public int compare(State o1, State o2) {
			int timeComp = o1.getTime().compareTo(o2.getTime());
			if(timeComp == 0){
				return o2.getEnergy().compareTo(o1.getEnergy());
			} else {
				return timeComp;
			}
		}

	}
	

}
