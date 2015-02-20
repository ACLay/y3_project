package Model.connectors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;


public class Connector{

	private Type type;
	private Amount<Power> output;
	private Amount<ElectricPotential> voltage;
	private Amount<ElectricCurrent> current;
	
	//default values of amps and volts to generate connectors from, using common UK outlet values
	private static final double[] defaultCurrents = {7,10,13};
	private static final double[] defaultVoltages = {250};

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

	//Returns a collection of connectors built from the classes default arrays of current and voltage options
	public static Collection<Connector> getNewCollection(double p, boolean canBeEmpty){
		return getNewCollection(p,canBeEmpty, defaultVoltages, defaultCurrents);
	}
	
	//Returns a collection of connectors built using the provided arrays of current and voltage options
	public static Collection<Connector> getNewCollection(double p, boolean canBeEmpty, double[] voltages, double[] currents){
		Random rnd = new Random();
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		if(canBeEmpty){
			for(Type t : Type.values()){
				if(rnd.nextDouble() < p){
					connectors.add(buildConnector(t,rnd,voltages,currents));
				}
			}
		} else {
			int n = rnd.nextInt(Type.values().length);
			for(Type t : Type.values()){
				if(t.equals(Type.values()[n])){
					connectors.add(buildConnector(t,rnd,voltages,currents));
				} else if(rnd.nextDouble() < p){
					connectors.add(buildConnector(t,rnd,voltages,currents));
				}
			}
		}

		return connectors;
	}

	//Build a new connector of a given type with a randomly chosen voltage and current
	private static Connector buildConnector(Type t, Random rnd, double[] voltages, double[] currents){
		double amps = currents[rnd.nextInt(currents.length)];
		double volts = currents[rnd.nextInt(voltages.length)];
		Amount<ElectricPotential> voltage = Amount.valueOf(volts, SI.VOLT);
		Amount<ElectricCurrent> current = Amount.valueOf(amps,SI.AMPERE);
		Amount<Power> output = Amount.valueOf(volts * amps, SI.WATT);
		Connector c = new Connector(t, output, voltage, current);
		return c;
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
		} else if (!current.approximates(other.current))
			return false;
		if (output == null) {
			if (other.output != null)
				return false;
		} else if (!output.approximates(other.output))
			return false;
		if (type != other.type)
			return false;
		if (voltage == null) {
			if (other.voltage != null)
				return false;
		} else if (!voltage.approximates(other.voltage))
			return false;
		return true;
	}

}