package OSRM;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jscience.physics.amount.Amount;
import org.json.JSONException;
import org.json.JSONObject;

import registry.ChargerLoader;
import router.Edge;
import router.Geography;
import web.Json;
import Model.Node;
import XML.XMLedge;
import XML.XMLedges;

public class GraphBuilder {

	QueryBuilder qb;
	Amount<Length> maxDistance;

	public GraphBuilder(QueryBuilder queryBuilder, Amount<Length> maxDistance){
		qb = queryBuilder;
		this.maxDistance = maxDistance;
	}
	
	public static void main(String[] args){
		Collection<Node> chargers = ChargerLoader.loadFromFile("./xml/edited_registry.xml");
		GraphBuilder gb = new GraphBuilder(new QueryBuilder("127.0.0.1",5000), Amount.valueOf(426, SI.KILOMETER));
		long startTime = System.currentTimeMillis();
		gb.buildGraph(chargers);//EPA range of the (85kWh) tesla model s
		long endTime = System.currentTimeMillis();
		System.out.println("In " + (endTime-startTime) + "ms");
	}

	public Amount<Length> getMaxDistance(){
		return maxDistance;
	}
	
	public void buildGraph(Collection<Node> chargers){

		Iterator<Node> iter1 = chargers.iterator();
		
		int toCheck = chargers.size() * chargers.size();
		int routesMade = 0;
		int unmakableRoutes = 0;
		int tooLongPre = 0;
		int tooLongPost = 0;
		int routesTried = 0;
		
		while(iter1.hasNext()){
			Node startPoint = iter1.next();
			Iterator<Node> iter2 = chargers.iterator();
			ArrayList<XMLedge> xEdges = new ArrayList<XMLedge>();
			while(iter2.hasNext()){
				Node endPoint = iter2.next();
				if(routesTried%1000 == 0){
					System.out.println(routesTried + " / " + toCheck);
				}
				routesTried ++;
				if(Geography.haversineDistance(startPoint, endPoint).isGreaterThan(maxDistance)){
					tooLongPre ++;
					continue;
				}
				
				if(!startPoint.equals(endPoint)){
					Edge edge;
					try {
						edge = makeEdge(startPoint, endPoint);
					} catch (Exception e) {
						unmakableRoutes ++;
						continue;
					}
					if(edge.getDistance().isGreaterThan(maxDistance)){
						tooLongPost ++;
						continue;
					}
					xEdges.add(new XMLedge(edge));
					routesMade++;
				}
			}
			File f = new File("./xml/edges/"+startPoint.getID()+".xml");
			
			XMLedges xmlEdges = new XMLedges();
			xmlEdges.setEdges(xEdges);
			
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(XMLedges.class);
				
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
				jaxbMarshaller.marshal(xmlEdges,f);
				
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		System.out.println(routesMade + " routes made");
		System.out.println(unmakableRoutes + " unmakable routes");
		System.out.println(tooLongPre + " too far as the crow flies");
		System.out.println(tooLongPost + " too far to drive");
	}

	public Edge makeEdge(Node startPoint, Node endPoint) throws UnroutableException, MalformedURLException, IOException, JSONException{
		URL url;
		try {
			url = qb.routeBetween(startPoint, endPoint);
		} catch (MalformedURLException e) {
			return null;
		}
		try{
			
			JSONObject json = Json.readURL(url);
			int status = json.getInt("status");
			if(status == 0){
				JSONObject summary = json.getJSONObject("route_summary");
				int meters = summary.getInt("total_distance");
				int seconds = summary.getInt("total_time");
				return new Edge(startPoint, endPoint, Amount.valueOf(meters, SI.METER) , Amount.valueOf(seconds, SI.SECOND));
			} else {
				throw new UnroutableException();
			}
		} catch (IOException e){
			throw e;
		} catch (JSONException je){
			throw je;
		}
	}

}
