package Model.connectors;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;

import org.jscience.physics.amount.Amount;


public class Connector{

	private Type type;
	private Amount<Power> output;
	private Amount<ElectricPotential> voltage;
	private Amount<ElectricCurrent> current;

	public Connector(Type type, Amount<Power> output, Amount<ElectricPotential> voltage, Amount<ElectricCurrent> current){
		this.type = type;
		this.output = output;
		this.voltage = voltage;
		this.current = current;
	}

	public Type getType(){
		return type;
	}
	
	public Amount<Power> getPower(){
		return output;
	}

	public Amount<ElectricPotential> getVoltage(){
		return voltage;
	}

	public Amount<ElectricCurrent> getCurrent(){
		return current;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((current == null) ? 0 : current.hashCode());
		result = prime * result + ((output == null) ? 0 : output.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((voltage == null) ? 0 : voltage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Connector))
			return false;
		Connector other = (Connector) obj;
		if (current == null) {
			if (other.current != null)
				return false;
		} else if (!current.equals(other.current))
			return false;
		if (output == null) {
			if (other.output != null)
				return false;
		} else if (!output.equals(other.output))
			return false;
		if (type != other.type)
			return false;
		if (voltage == null) {
			if (other.voltage != null)
				return false;
		} else if (!voltage.equals(other.voltage))
			return false;
		return true;
	}

}