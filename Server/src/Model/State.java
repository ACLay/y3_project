package Model;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

public class State {

	private Node location;
	private Amount<Duration> time;
	private Amount<Energy> energy;
	private State previous;
	private Amount<Length> distance;
	private Car car;
	
	public State(Node location, Amount<Duration> time, Amount<Energy> energy, State previous, Amount<Length> distance, Car car){
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
	
	public State moveTo(Node nextLocation, Amount<Duration> travelTime, Amount<Length> travelDistance){
		Amount<Duration> nextTime = time.plus(travelTime);
		Amount<Energy> nextCharge = energy.minus(car.chargeNeededToTravel(travelDistance));
		Amount<Length> nextDistance = distance.plus(travelDistance);
		
		return new State(nextLocation, nextTime, nextCharge, this, nextDistance, car);
	}
	
	//Accessors!
	public Node getLocation(){
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
	
}
