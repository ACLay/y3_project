package router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Model.Charger;

public class ListPrunedQueueRouter extends QueueRouter {
	//TODO remove successors to inferior states
	public ListPrunedQueueRouter(StateTimeComparator comparator) {
		super(comparator);
	}

	HashMap<Charger,ArrayList<State>> candidateStates = new HashMap<Charger,ArrayList<State>>();
	Set<State> inferiors = new HashSet<State>();
	long dropped = 0;
	
	protected void addState(State s){
		created++;
		//get the chargers candidates
		ArrayList<State> candidates;
		if(candidateStates.containsKey(s.getLocation())){
			candidates = candidateStates.get(s.getLocation());
		} else {
			candidates = new ArrayList<State>();
			candidateStates.put(s.getLocation(), candidates);
		}
		
		boolean addNewState = true;
		
		for(State oldState : candidates){
			//if there exists a state that the new one is no better than in either stat, the new one shouldn't be added
			if(!s.isFasterThan(oldState) && !s.isChargierThan(oldState)){
				addNewState = false;
			//if there exists an old state with no better stats than the new one, the old one should be dropped
			} else if(!oldState.isFasterThan(s) && !oldState.isChargierThan(s)){
				inferiors.add(oldState);
			}
		}
		
		if(addNewState){
			pq.add(s);
			candidates.add(s);
			stored++;
		}
		
		for(State old : inferiors){
			pq.remove(old);
			candidates.remove(old);
			dropped++;
		}
		inferiors.clear();
	}
	
	public long getDropped(){
		return dropped;
	}
	
	public void printStats(){
		super.printStats();
		System.out.println("States dropped: " + getDropped());
	}
	

}
