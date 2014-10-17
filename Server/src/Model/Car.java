package Model;
import java.util.Collection;
import java.util.HashSet;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import Model.connectors.Connector;




public class Car {

	private String model;
	private Amount<Length> range;
	private Amount<Energy> capacity;
	//compatible connectors
	private Collection<Connector> connectors;
	
	//USE JSCIENCE.ORG FOR UNIT CONVERSION! - POSSIBLY EVEN COORDINATE PROCESSING...
	public Car(String model, Amount<Length> range, Amount<Energy> capacity){
		this.model = model;
		this.range = range;
		this.capacity = capacity;
		connectors = new HashSet<Connector>();
	}
	
	public Amount<Energy> chargeNeededToTravel(Amount<Length> distance){
		return distance.divide(range).times(capacity).to(SI.JOULE);
	}
	
	public String getModel(){
		return model;
	}
	
	public Amount<Length> getRange(){
		return range;
	}
	
	public Amount<Energy> getCapacity(){
		return capacity;
	}
	
	public void addCompatibleConnector(Connector connector){
		connectors.add(connector);
	}
	
	public void removeConnector(Connector connector){
		connectors.remove(connector);
	}
	
	public boolean compatibleWith(Connector connector){
		return connectors.contains(connector);
	}
	
	public Collection<Connector> getConnectors(){
		return connectors;
	}
	
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
		@SuppressWarnings("rawtypes")
		Amount speed = Amount.valueOf(25, SI.METER).divide(Amount.valueOf(5, SI.SECOND));
		
		System.out.println(speed);
		System.out.println(speed.getUnit());
		
		System.out.println(speed.times(Amount.valueOf(2, SI.SECOND)));
		
		System.out.println(Amount.valueOf(20, SI.METER).divide(speed));
		
		System.out.println(Amount.valueOf("306 MJ").to(SI.JOULE));
	
	}
	
}
