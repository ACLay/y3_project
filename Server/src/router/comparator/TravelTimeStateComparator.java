package router.comparator;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;

import org.jscience.physics.amount.Amount;

import router.Geography;
import router.State;
import Model.Charger;

public class TravelTimeStateComparator extends AStarStateTimeComparator {

	public TravelTimeStateComparator(Charger endpoint, Amount<Velocity> maxSpeed, Amount<Power> fastestCharge) {
		super(endpoint, maxSpeed, fastestCharge);
	}
	
	protected Amount<Duration> heuristicTime(State s){
		Amount<Length> distance = Geography.haversineDistance(s.getLocation(), endpoint);
		return travelTime(distance);
	}

}
