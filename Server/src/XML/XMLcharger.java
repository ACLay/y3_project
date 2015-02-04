package XML;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jscience.geography.coordinates.LatLong;

import Model.Charger;
import Model.connectors.Connector;

@XmlRootElement(name = "ChargeDevice")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLcharger {
	
	public XMLcharger(){
		connectors = new ArrayList<XMLconnector>();
	}
	
	public XMLcharger(Charger charger){
		this.chargeDeviceID = charger.getID();
		this.chargeDeviceRef = charger.getReference();
		this.chargeDeviceName = charger.getName();
		this.location = new XMLLocation();
		location.setLatitude(charger.getCoordinates().latitudeValue(NonSI.DEGREE_ANGLE));
		location.setLongitude(charger.getCoordinates().longitudeValue(NonSI.DEGREE_ANGLE));
		location.setLongDescription(charger.getLocationLong());
		location.setShortDescription(charger.getLocationShort());
		this.connectors = new ArrayList<XMLconnector>();
		for(Connector c : charger.getConnectors()){
			connectors.add(new XMLconnector(c));
		}
	}
	
	public Charger makeCharger(){
		ArrayList<Connector> realConnectors = new ArrayList<Connector>();
		for(XMLconnector xmlConn : connectors){
			Connector c = xmlConn.makeConnector();
			if(c != null){
				realConnectors.add(xmlConn.makeConnector());
			}
		}
		
		return new Charger(chargeDeviceID,
				chargeDeviceRef,
				chargeDeviceName,
				LatLong.valueOf(location.latitude,location.longitude,NonSI.DEGREE_ANGLE),
				location.shortDescription,
				location.longDescription,
				realConnectors);
	}
	
	//device id (32 hex digits, 128 bits), unique
	@XmlElement(name = "ChargeDeviceId")
 	private String chargeDeviceID;
	//device ref (string), not unique
	@XmlElement(name = "ChargeDeviceRef")
	private String chargeDeviceRef;
	//device name
	@XmlElement(name = "ChargeDeviceName")
	private String chargeDeviceName;
	//latitude + longitude + location descriptions (long + short)
	@XmlElement(name = "ChargeDeviceLocation")
	private XMLLocation location;


	//connectors
	@XmlElement(name = "Connector")
	private List<XMLconnector> connectors;
	
	
	public void setChargeDeviceID(String chargeDeviceID){
		this.chargeDeviceID = chargeDeviceID;
	}
	public void setChargeDeviceRef(String chargeDeviceRef){
		this.chargeDeviceRef = chargeDeviceRef;
	}
	
	public void setChargeDeviceName(String chargeDeviceName){
		this.chargeDeviceName = chargeDeviceName;
	}
	public void setLocation(XMLLocation location){
		this.location = location;
	}
	public void setConnectors(List<XMLconnector> connectors){
		this.connectors = connectors;
	}

	public String getChargeDeviceID(){
		return chargeDeviceID;
	}
	public String getChargeDeviceRef(){
		return chargeDeviceRef;
	}
	public String getChargeDeviceName(){
		return chargeDeviceName;
	}
	public XMLLocation getLocation(){
		return location;
	}
	public Collection<XMLconnector> getConnectors(){
		return connectors;
	}

}
