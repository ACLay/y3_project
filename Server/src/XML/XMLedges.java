package XML;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Edges")
public class XMLedges{
	
	@XmlElement(name = "Edge")
	List<XMLedge> chargers = new ArrayList<XMLedge>();
	
	public List<XMLedge> getChargers(){
		return chargers;
	}
	
	public void setChargers(List<XMLedge> chargers){
		this.chargers = chargers;
	}
}
