package sample;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import Model.Car;

public class AmountTest {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		//create a car
		Car tesla = new Car("Tesla Model S", Amount.valueOf(500, SI.KILOMETER) ,Amount.valueOf("306 MJ").to(SI.MEGA(SI.JOULE)));
		
		//output energy needed to go 1km
		Amount<Energy> e = tesla.chargeNeededToTravel(Amount.valueOf(1, SI.KILOMETER));
		
		System.out.println("Energy per kilometer:\n" + e.to(SI.KILO(SI.JOULE)));
		//bounds on what e might be, (based on computer calculation inaccuracy?)
		System.out.println("Min: " + e.getMinimumValue());
		System.out.println("Max: " + e.getMaximumValue());
		//estimated total charge based on charge/km
		System.out.println(e.getEstimatedValue() * 500);
		
		//experimenting with units
		Amount<Velocity> speed = Amount.valueOf(25, SI.METER).divide(Amount.valueOf(5, SI.SECOND)).to(SI.METERS_PER_SECOND);
		
		System.out.println(speed);
		System.out.println(speed.getUnit());
		
		System.out.println(speed.times(Amount.valueOf(2, SI.SECOND)));
		
		System.out.println(Amount.valueOf(20, SI.METER).divide(speed));
		
		System.out.println(Amount.valueOf("306 MJ").to(SI.JOULE));
	
	}

}
