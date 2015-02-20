package OSRM;

import java.net.MalformedURLException;
import java.net.URL;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.geography.coordinates.LatLong;

public class QueryBuilder {

	private String serverName;
	private Integer port;
	
	public QueryBuilder(String server, Integer port){
		serverName = server;
		this.port = port;
	}
	
	public URL routeBetween(LatLong start, LatLong end) throws MalformedURLException{
		
		Double lat1 = start.latitudeValue(NonSI.DEGREE_ANGLE);
		Double lon1 = start.longitudeValue(NonSI.DEGREE_ANGLE);
		
		Double lat2 = end.latitudeValue(NonSI.DEGREE_ANGLE);
		Double lon2 = end.longitudeValue(NonSI.DEGREE_ANGLE);
		
		StringBuilder sb = new StringBuilder().append("http://")
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
	
	public static void main(String[] args){
		QueryBuilder qb = new QueryBuilder("osrm.mapzen.com/car", null);
		try {
			System.out.println(qb.routeBetween(LatLong.valueOf(0, 0, SI.RADIAN), LatLong.valueOf(0.1,0.1, SI.RADIAN)));
		} catch (MalformedURLException e) {
			System.err.println("Unable to build valid url");
		}
	}
	
}
