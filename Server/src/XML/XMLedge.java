package XML;

import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import router.Edge;

@XmlRootElement(name = "Edge")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLedge {
	@XmlElement(name = "StartPointId")
	String startPointId;
	@XmlElement(name = "EndPointId")
	String endPointId;
	@XmlElement(name = "TravelDistance")
	Double travelDistance;
	@XmlElement(name = "TravelTime")
	Double travelTime;
	
	public XMLedge(){}
	
	public XMLedge(Edge edge){
		startPointId = edge.getStartPoint().getID();
		endPointId = edge.getEndPoint().getID();
		travelDistance = edge.getDistance().doubleValue(SI.METER);
		travelTime = edge.getTravelTime().doubleValue(SI.SECOND);
	}
	
	public String getStartPointId(){
		return startPointId;
	}
	public String getEndPointId(){
		return endPointId;
	}
	public Double getTravelDistance(){
		return travelDistance;
	}
	public Double getTravelTime(){
		return travelTime;
	}
	
	public void setStartPointId(String id){
		startPointId = id;
	}
	public void setEndPointId(String id){
		endPointId = id;
	}
	public void setTravelDistance(Double meters){
		travelDistance = meters;
	}
	public void setTravelTime(Double seconds){
		travelTime = seconds;
	}
	
}
