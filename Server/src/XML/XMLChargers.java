package XML;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ChargeDevices")
public class XMLChargers{
	
	@XmlElement(name = "ChargeDevice")
	List<XMLcharger> chargers = new ArrayList<XMLcharger>();
	
	public List<XMLcharger> getChargers(){
		return chargers;
	}
	
	public void setChargers(List<XMLcharger> chargers){
		this.chargers = chargers;
	}
}
