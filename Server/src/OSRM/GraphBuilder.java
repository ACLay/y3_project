package OSRM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;
import org.json.JSONException;
import org.json.JSONObject;

import XML.GraphIO;

import registry.ChargerLoader;
import router.Edge;
import router.graph.Graph;
import router.graph.RamGraph;
import Model.Charger;

public class GraphBuilder {

	QueryBuilder qb;

	public GraphBuilder(QueryBuilder queryBuilder){
		qb = queryBuilder;
	}
	
	public static void main(String[] args){
		Collection<Charger> chargers = ChargerLoader.loadFromFile("./edited_registry.xml");
		GraphBuilder gb = new GraphBuilder(new QueryBuilder("127.0.0.1",5000));
		Graph graph = gb.buildGraph(chargers);
		GraphIO.saveGraph(graph, "./generated_graph.xml");
	}

	public Graph buildGraph(Collection<Charger> chargers){
		Graph graph = new RamGraph();
		graph.addNodes(chargers);

		Iterator<Charger> iter1 = chargers.iterator();

		while(iter1.hasNext()){
			Charger startPoint = iter1.next();
			Iterator<Charger> iter2 = chargers.iterator();
			while(iter2.hasNext()){
				Charger endPoint = iter2.next();
				if(!startPoint.equals(endPoint)){
					Edge edge;
					try {
						edge = makeEdge(startPoint, endPoint);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					graph.addEdge(edge);
				}
			}
		}

		return graph;
	}

	public Edge makeEdge(Charger startPoint, Charger endPoint) throws UnroutableException, MalformedURLException, IOException, JSONException{
		URL url;
		try {
			url = qb.routeBetween(startPoint, endPoint);
		} catch (MalformedURLException e) {
			return null;
		}
		try{
			
			JSONObject json = readURL(url);
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
	
	private static JSONObject readURL(URL url) throws IOException{
		URLConnection yc = url.openConnection();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						yc.getInputStream()));
		
		StringBuilder urlData = new StringBuilder();
		
		String inputLine;
		
		while ((inputLine = br.readLine()) != null){
			System.out.println(inputLine);
			urlData.append(inputLine).append("\n");
		}
		br.close();

		String jsonText = urlData.toString();
		return new JSONObject(jsonText);
	}

}
