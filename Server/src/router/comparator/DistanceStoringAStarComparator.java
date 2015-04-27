package router.comparator;

import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;

import org.jscience.physics.amount.Amount;

import router.Geography;
import router.State;
import Model.Node;

public class DistanceStoringAStarComparator extends AStarStateTimeComparator{

	Map<Node, Amount<Length>> distances = new HashMap<Node, Amount<Length>>();
	
	
	public DistanceStoringAStarComparator(Node endpoint, Amount<Velocity> maxSpeed, Amount<Power> fastestCharge){
		super(endpoint,maxSpeed,fastestCharge);
	}
	

	
	protected Amount<Duration> heuristicTime(State s){
		
		Amount<Length> distance;
		if(distances.containsKey(s.getLocation())){
			distance = distances.get(s.getLocation());
		} else {
			distance = Geography.haversineDistance(s.getLocation(), endpoint);
			distances.put(s.getLocation(), distance);
		}
	
		return travelTime(distance).plus(chargeTime(s,distance));
	}
	
}