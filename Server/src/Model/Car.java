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

	public void addCompatibleConnectors(Collection<Connector> connectors){
		for(Connector c : connectors){
			addCompatibleConnector(c);
		}
	}
	
	public void removeConnector(Connector connector){
		connectors.remove(connector);
	}
	
	public boolean compatibleWith(Connector connector){
		for(Connector c : connectors){
			if(c.getType().equals(connector.getType())){
				return true;
			}
		}
		return false;
	}
	
	public Collection<Connector> getConnectors(){
		return connectors;
	}
	

	
}
