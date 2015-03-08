package router;

import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;

import org.jscience.physics.amount.Amount;

import Model.Charger;

public class DistanceStoringAStarComparator extends AStarStateTimeComparator{

	Map<Charger, Amount<Length>> distances = new HashMap<Charger, Amount<Length>>();
	
	
	public DistanceStoringAStarComparator(Charger endpoint, Amount<Velocity> maxSpeed, Amount<Power> fastestCharge){
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