package router.comparator;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;

import org.jscience.physics.amount.Amount;

import router.Geography;
import router.State;
import Model.Node;

public class ChargeTimeStateComparator extends AStarStateTimeComparator {

	public ChargeTimeStateComparator(Node endpoint, Amount<Velocity> maxSpeed, Amount<Power> fastestCharge) {
		super(endpoint, maxSpeed, fastestCharge);
	}
	
	protected Amount<Duration> heuristicTime(State s){
		Amount<Length> distance = Geography.haversineDistance(s.getLocation(), endpoint);
		return chargeTime(s,distance);
	}

}
