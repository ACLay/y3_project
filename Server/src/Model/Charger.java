package Model;
import java.util.Collection;

import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;
import org.jscience.physics.amount.Amount;

import Model.connectors.Connector;


public class Charger {

	//device id (32 hex digits, 128 bits), unique
	private String chargeDeviceID;
	//device ref (string), not unique
	private String chargeDeviceRef;
	//device name
	private String chargeDeviceName;
	//latitude + longitude
	private LatLong coordinates;
	//address

	//location description (long + short)
	private String locationShort;
	private String locationLong;
	//connectors
	private Collection<Connector> connectors;

	public Charger(String id, String ref, String name, LatLong coordinates, String locationShort, String locationLong, Collection<Connector> connectors){
		this.chargeDeviceID = id;
		this.chargeDeviceRef = ref;
		this.chargeDeviceName = name;
		this.coordinates = coordinates;
		this.locationShort = locationShort;
		this.locationLong = locationLong;
		this.connectors = connectors;
	}

	public boolean canCharge(Car car){
		for(Connector c : connectors){
			if(car.compatibleWith(c)){
				return true;
			}
		}
		return false;
	}

	public Amount<Power> maxChargeOutput(Car car){
		Amount<Power> maxOut = Amount.valueOf(0, SI.WATT);

		//for each combination of connector at the charger and on the car
		for(Connector c_car : car.getConnectors()){
			for(Connector c_node : connectors){
				//if they are of the same type
				if(c_car.getType() == c_node.getType()){
					//get the lowest current and voltages that both the car and charger support
					Amount<ElectricCurrent> lowestCurrent = c_car.getCurrent();
					if(lowestCurrent.isGreaterThan(c_node.getCurrent())){
						lowestCurrent = c_node.getCurrent();
					}
					Amount<ElectricPotential> lowestVoltage = c_car.getVoltage();
					if(lowestVoltage.isGreaterThan(c_node.getVoltage())){
						lowestVoltage = c_node.getVoltage();
					}
					//calculate the power you can charge with using those values (Power = Voltage x Current)
					Amount<Power> lowest = Amount.valueOf(lowestCurrent.getMinimumValue() * lowestVoltage.getMinimumValue(), SI.WATT);
					//check that both the car and charger can provide/take that power
					//This shouldn't be necessary, but some power ratings in the NCR are inconsistent with their voltage and current
					if(c_car.getPower().isLessThan(lowest)){
						lowest = c_car.getPower();
					}
					if(c_node.getPower().isLessThan(lowest)){
						lowest = c_node.getPower();
					}
					//if the lowest usable power for this connection is better than the previous connection power, use it.
					if(lowest.isGreaterThan(maxOut)){
						maxOut = lowest;
					}
				}
			}
		}
		
		return maxOut;
	}

	public String getID(){
		return chargeDeviceID;
	}

	public String getReference(){
		return chargeDeviceRef;
	}

	public String getName(){
		return chargeDeviceName;
	}

	public LatLong getCoordinates(){
		return coordinates;
	}

	public String getLocationShort(){
		return locationShort;
	}

	public String getLocationLong(){
		return locationLong;
	}

	public Collection<Connector> getConnectors(){
		return connectors;
	}
	
}
