package XML;

import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jscience.physics.amount.Amount;

import Model.connectors.Connector;
import Model.connectors.Type;

@XmlRootElement(name = "Connector")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLconnector {
	
	public XMLconnector(){ }
	
	public XMLconnector(Connector connector){
		connectorType = connector.getType().getDescription();
		ratedOutputkW = connector.getPower().doubleValue(SI.KILO(SI.WATT));
		ratedOutputVoltage = connector.getVoltage().doubleValue(SI.VOLT);
		ratedOutputCurrent = connector.getCurrent().doubleValue(SI.AMPERE);
	}
	
	@XmlElement(name = "ConnectorType")
	private String connectorType;
	
	@XmlElement(name = "RatedOutputkW")
	private Double ratedOutputkW;
	
	@XmlElement(name = "RatedOutputVoltage")
	private Double ratedOutputVoltage;
	
	@XmlElement(name = "RatedOutputCurrent")
	private Double ratedOutputCurrent;
	
	public Connector makeConnector(){
		Type type = null;
		for(Type t : Type.values()){
			if(t.getDescription().equals(connectorType)){
				type = t;
			}
		}
		
		if(ratedOutputkW == null){
			if(ratedOutputVoltage != null && ratedOutputCurrent != null){
				ratedOutputkW = ratedOutputVoltage * ratedOutputCurrent / 1000;
			} else {
				System.err.println("Unmakable connector");
				return null;
			}
		} else if(ratedOutputVoltage == null){
			if(ratedOutputkW != null && ratedOutputCurrent != null){
				ratedOutputVoltage = ratedOutputkW / (1000 * ratedOutputCurrent);
			} else {
				System.err.println("Unmakable connector");
				return null;
			}
		} else if(ratedOutputCurrent == null){
			if(ratedOutputkW != null && ratedOutputVoltage != null){
				ratedOutputCurrent = ratedOutputkW / (1000 * ratedOutputVoltage);
			} else {
				System.err.println("Unmakable connector");
				return null;
			}
		}
		
		
		return new Connector(type,
				Amount.valueOf(ratedOutputkW, SI.KILO(SI.WATT)),
				Amount.valueOf(ratedOutputVoltage, SI.VOLT),
				Amount.valueOf(ratedOutputCurrent, SI.AMPERE));
		
	}
	
	public void setConnectorType(String connectorType){
		this.connectorType = connectorType;
	}
	public void setRatedOutputkW(Double ratedOutputkW){
		this.ratedOutputkW = ratedOutputkW;
	}
	public void setRatedOutputVoltage(Double ratedOutputVoltage){
		this.ratedOutputVoltage = ratedOutputVoltage;
	}
	public void setRatedOutputCurrent(Double ratedOutputCurrent){
		this.ratedOutputCurrent = ratedOutputCurrent;
	}
	
	public String getConnectorType(){
		return connectorType;
	}
	public Double getRatedOutputkW(){
		return ratedOutputkW;
	}
	public Double getRatedOutputVoltage(){
		return ratedOutputVoltage;
	}
	public Double getRatedOutputCurrent(){
		return ratedOutputCurrent;
	}
}
