package router.comparator;

import java.util.Comparator;

import router.State;

public class StateTimeComparator implements Comparator<State>{
	@Override
	public int compare(State o1, State o2) {
		return o1.getTime().compareTo(o2.getTime());
	}

}
