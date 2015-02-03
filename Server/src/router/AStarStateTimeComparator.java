package router;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import Model.Car;
import Model.Charger;

public class AStarStateTimeComparator extends StateTimeComparator{

	protected Charger endpoint;
	final Amount<Velocity> maxSpeed = Amount.valueOf(30, NonSI.MILES_PER_HOUR);
	final Amount<Length> earthRadius = Amount.valueOf(6353, SI.KILOMETRE);
	protected final Amount<Power> fastestCharge;
	
	public AStarStateTimeComparator(Charger endpoint, Amount<Power> fastestCharge){
		this.endpoint = endpoint;
		this.fastestCharge = fastestCharge;
	}
	
	@Override
	public int compare(State s1, State s2) {
		
		Amount<Duration> state1time = heuristicTime(s1).plus(s1.getTime());
		Amount<Duration> state2time = heuristicTime(s2).plus(s2.getTime());
		
		
		return state1time.compareTo(state2time);
	}
	
	protected Amount<Duration> heuristicTime(State s){
		Amount<Length> distance = haversineDistance(s.getLocation(), endpoint);
		return travelTime(distance).plus(chargeTime(s,distance));
	}
	
	protected Amount<Length> haversineDistance(Charger s1, Charger s2){
		double lat1 = s1.getCoordinates().latitudeValue(SI.RADIAN);
		double lon1 = s1.getCoordinates().longitudeValue(SI.RADIAN);
		
		double lat2 = s2.getCoordinates().latitudeValue(SI.RADIAN);
		double lon2 = s2.getCoordinates().longitudeValue(SI.RADIAN);
		
		double internal = Math.pow(Math.sin((lat2 - lat1)/2), 2) + Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin((lon2 - lon1)/2),2);
		double root = Math.sqrt(internal);
		return earthRadius.times(2*Math.asin(root));
	}

	
	protected Amount<Duration> travelTime(Amount<Length> distance){
		Amount<Duration> timeEstimate = distance.divide(maxSpeed).to(SI.SECOND);
		
		return timeEstimate;
	}
	
	protected Amount<Duration> chargeTime(State s, Amount<Length> distance){
		Car vehicle = s.getCar();
		Amount<Energy> currentCharge = s.getEnergy();
		Amount<Energy> energyNeeded = vehicle.chargeNeededToTravel(distance);
		if(currentCharge.isLessThan(energyNeeded)){
			return Amount.valueOf(0,SI.SECOND);
		} else {
			Amount<Energy> excess = energyNeeded.minus(currentCharge);
			return excess.divide(fastestCharge).to(SI.SECOND);
		}

	}
	
}