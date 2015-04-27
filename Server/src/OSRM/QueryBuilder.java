package OSRM;

import java.net.MalformedURLException;
import java.net.URL;

import javax.measure.unit.NonSI;

import org.jscience.geography.coordinates.LatLong;

import Model.Node;

public class QueryBuilder {

	private String serverName;
	private Integer port;
	
	public QueryBuilder(String server, Integer port){
		serverName = server;
		this.port = port;
	}
	
	public URL routeBetween(Node start, Node end) throws MalformedURLException{
		return routeBetween(start.getCoordinates(),end.getCoordinates());
	}
	
	public URL routeBetween(LatLong start, LatLong end) throws MalformedURLException{
		
		Double lat1 = start.latitudeValue(NonSI.DEGREE_ANGLE);
		Double lon1 = start.longitudeValue(NonSI.DEGREE_ANGLE);
		
		Double lat2 = end.latitudeValue(NonSI.DEGREE_ANGLE);
		Double lon2 = end.longitudeValue(NonSI.DEGREE_ANGLE);
		
		StringBuilder sb = new StringBuilder()
		.append("http://")
		.append(serverName);
		if(port != null){
			sb.append(":")
			.append(port);
		}
		sb.append("/viaroute?loc=")
		.append(lat1)
		.append(",")
		.append(lon1)
		.append("&loc=")
		.append(lat2)
		.append(",")
		.append(lon2);
		
		
		return new URL(sb.toString());
	}
	
	
}
