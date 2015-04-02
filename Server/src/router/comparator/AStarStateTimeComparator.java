package router.comparator;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Geography;
import router.State;
import Model.Car;
import Model.Charger;

public class AStarStateTimeComparator extends StateTimeComparator{

	protected Charger endpoint;
	protected Amount<Velocity> maxSpeed;
	protected Amount<Power> fastestCharge;
	
	public AStarStateTimeComparator(Charger endpoint, Amount<Velocity> maxSpeed, Amount<Power> fastestCharge){
		this.endpoint = endpoint;
		this.maxSpeed = maxSpeed;
		this.fastestCharge = fastestCharge;
	}
	
	@Override
	public int compare(State s1, State s2) {
		
		Amount<Duration> state1time = heuristicTime(s1).plus(s1.getTime());
		Amount<Duration> state2time = heuristicTime(s2).plus(s2.getTime());
		
		
		return state1time.compareTo(state2time);
	}
	
	protected Amount<Duration> heuristicTime(State s){
		Amount<Length> distance = Geography.haversineDistance(s.getLocation(), endpoint);
		return travelTime(distance).plus(chargeTime(s,distance));
	}

	
	protected Amount<Duration> travelTime(Amount<Length> distance){
		Amount<Duration> timeEstimate = distance.divide(maxSpeed).to(SI.SECOND);
		
		return timeEstimate;
	}
	
	protected Amount<Duration> chargeTime(State s, Amount<Length> distance){
		Car vehicle = s.getCar();
		Amount<Energy> currentCharge = s.getEnergy();
		Amount<Energy> energyNeeded = vehicle.chargeNeededToTravel(distance);
		if(!energyNeeded.isGreaterThan(currentCharge)){
			return Amount.valueOf(0,SI.SECOND);
		} else {
			Amount<Energy> excess = energyNeeded.minus(currentCharge);
			return excess.divide(fastestCharge).to(SI.SECOND);
		}

	}
	
}