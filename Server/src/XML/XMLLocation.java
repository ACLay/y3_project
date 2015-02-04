package XML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ChargeDeviceLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLLocation {
	
	@XmlElement(name = "Latitude")
	Double latitude;
	
	@XmlElement(name = "Longitude")
	Double longitude;
	
	@XmlElement(name = "LocationShortDescription")
	String shortDescription;
	
	@XmlElement(name = "LocationLongDescription")
	String longDescription;
	
	public XMLLocation(){
		
	}
	
	public void setLatitude(Double latitude){
		this.latitude = latitude;
	}
	public void setLongitude(Double longitude){
		this.longitude = longitude;
	}
	public void setLongDescription(String longDescription){
		this.longDescription = longDescription;
	}
	public void setShortDescription(String shortDescription){
		this.shortDescription = shortDescription;
	}
	
	public Double getLatitude(){
		return latitude;
	}
	public Double getLongitude(){
		return longitude;
	}
	public String getLongDescription(){
		return longDescription;
	}
	public String getShortDescription(){
		return shortDescription;
	}
	
}
