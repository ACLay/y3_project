package router;

import java.util.ArrayList;
import java.util.Collections;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import Model.Car;
import Model.Charger;

public class State {

	private Charger location;
	private Amount<Duration> time;
	private Amount<Energy> energy;
	private State previous;
	private Amount<Length> distance;
	private Car car;
	
	public State(Charger location, Amount<Duration> time, Amount<Energy> energy, State previous, Amount<Length> distance, Car car){
		this.location = location;
		this.time = time;
		this.energy = energy;
		this.previous = previous;
		this.distance = distance;
		this.car = car;
	}
	
	//Functions for generating successor states
	public State charge(Amount<Energy> newLevel){
		Amount<Energy> toCharge = newLevel.minus(this.energy);
		
		Amount<Duration> chargeTime = toCharge.divide(location.maxChargeOutput(car)).to(SI.SECOND);
		Amount<Duration> nextTime = time.plus(chargeTime);
		
		return new State(location, nextTime, newLevel, this, distance, car);
	}
	
	public State moveTo(Charger nextLocation, Amount<Duration> travelTime, Amount<Length> travelDistance){
		Amount<Duration> nextTime = time.plus(travelTime);
		Amount<Energy> nextCharge = energy.minus(car.chargeNeededToTravel(travelDistance));
		Amount<Length> nextDistance = distance.plus(travelDistance);
		
		return new State(nextLocation, nextTime, nextCharge, this, nextDistance, car);
	}
	
	//Accessors!
	public Charger getLocation(){
		return location;
	}
	
	public Amount<Duration> getTime(){
		return time;
	}
	
	public Amount<Energy> getEnergy(){
		return energy;
	}
	
	public State getPrevious(){
		return previous;
	}
	
	public Amount<Length> getDistance(){
		return distance;
	}
	
	public Car getCar(){
		return car;
	}

	public void printRoute(){
		State current = this;
		State predecessor;
		ArrayList<String> moves = new ArrayList<String>();
		//for each state in the route
		do{
			//get its predecessor
			predecessor = current.getPrevious();
			String locationDesc = current.getLocation().getLocationLong();
			
			//print the transition
			if(predecessor == null){
				moves.add("Start at " + locationDesc);
			}
			else if(predecessor.getLocation().equals(current.getLocation())){
				Amount<Duration> chargeTime = current.getTime().minus(predecessor.getTime());
				Amount<Energy> chargedBy = current.getEnergy().minus(predecessor.getEnergy());
				moves.add("Charge at " + locationDesc + " for " + stringAmount(chargeTime) + ", " + stringAmount(chargedBy));
			}
			else {
				Amount<Duration> travelTime = current.getTime().minus(predecessor.getTime());
				Amount<Length> travelDistance = current.getDistance().minus(predecessor.getDistance());
				moves.add("Travel to " + locationDesc + "(" + stringAmount(travelDistance) + ", " + stringAmount(travelTime) + ")");
			}
			
			

			current = predecessor;
		}while(predecessor != null);

		Collections.reverse(moves);
		
		for(String s : moves){
			System.out.println(s);
		}
	}
	
	public void printStats(){
		System.out.println("Stats: " + stringAmount(distance) + ", " + stringAmount(time) + ", " + stringAmount(car.chargeNeededToTravel(distance)));
	}

	private String stringAmount(Amount<?> quantity){
		return Double.toString(Math.ceil(quantity.getMaximumValue())) + quantity.getUnit();
	}
	
	public boolean isSuperiorTo(State s2){
		return(isFasterThan(s2) && isChargierThan(s2));
	}
	
	public boolean isFasterThan(State s2){
		return time.isLessThan(s2.getTime());
	}
	
	public boolean isChargierThan(State s2){
		return energy.isGreaterThan(s2.getEnergy());
	}
	
	public boolean isInferiorTo(State s2){
		return s2.isSuperiorTo(this);
	}
	
	public boolean couldBeBetterThan(State s2){
		return (isFasterThan(s2) || isChargierThan(s2));
	}
	
}
