package web;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jscience.geography.coordinates.LatLong;
import org.jscience.physics.amount.Amount;

import router.Scenario;
import router.State;
import router.comparator.DistanceStoringAStarComparator;
import router.comparator.StateTimeComparator;
import router.graph.CustomPointFolderGraph;
import router.graph.FolderGraph;
import router.graph.Graph;
import router.router.ChargeOptimizer;
import router.router.FastChargeRouter;
//import router.router.ListPrunedQueueRouter;
import router.router.Router;
//import router.router.TimeOnlyRouter;
import Model.Car;
import Model.Node;
import Model.connectors.Connector;
import Model.connectors.Type;
import OSRM.GraphBuilder;
import OSRM.QueryBuilder;

public class RouteServerHandler extends AbstractHandler{

	public static final String START_ID = "startID";
	public static final String END_ID = "endID";
	public static final String START_LAT = "startLat";
	public static final String START_LON = "startLon";
	public static final String END_LAT = "endLat";
	public static final String END_LON = "endLon";
	public static final String CAR_RANGE = "carRange";
	public static final String CAR_CAPACITY = "carCapacity";

	//static final String edgeDir = "./xml/edges/", nodeFile = "./xml/edges/edited_registry.xml";
	static final String edgeDir = "./xml/powerCut2/", nodeFile = "./xml/powerCut2/nodes.xml";
	
	public static void main(String[] args) throws Exception{

		Server server = new Server(8080);
		server.setHandler(new RouteServerHandler());

		server.start();
		server.join();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		String path = request.getPathInfo();
		System.out.println(path);

		if(path.equals("/") || path.equals("/index") || path.equals("/index.html")){
			//if main page, make main page
			Map<String,String[]> parameters = request.getParameterMap();
			for (String param : parameters.keySet()){
				String[] values = parameters.get(param);
				System.out.print(param + ":");
				if(values.length == 0){
					System.out.println("no values");
				} else {
					for(String value : values){
						System.out.print(value + " & ");
					}
					System.out.println();
				}
			}
			List<String> lines = Files.readAllLines(Paths.get("./html/form.html"), Charset.forName("utf-8"));
			for(String s : lines){
				response.getWriter().println(s);
			}

		} else if (path.equals("/search") || path.equals("/search.html")){
			//if search page, run query
			Map<String,String[]> parameters = request.getParameterMap();
			//get the startpoint, endpoint, car from url parameters
			//startpoint and endpoint need charger ID or (lat,long) data
			Node startPoint = null, endPoint = null;
			Graph g;

			if(parameters.containsKey(START_ID) && parameters.containsKey(END_ID)){
				g = new FolderGraph(edgeDir, nodeFile);
				System.out.println("original graph");
				Collection<Node> nodes = g.getNodes();
				String startPointID = parameters.get(START_ID)[0];
				String endPointID = parameters.get(END_ID)[0];
				for(Node c : nodes){
					if(c.getID().equals(startPointID)){
						startPoint = c;
						if(endPoint != null){
							break;
						}
					}
					if(c.getID().equals(endPointID)){
						endPoint = c;
						if(startPoint != null){
							break;
						}
					}
				}
				if(endPoint == null || startPoint == null){
					if(startPoint == null){
						System.out.println("Trying to build custom start " + startPointID);
						LatLong coordinates = GeoCode.getCoordinates(startPointID);
						if(coordinates != null){
							System.out.println("custom start success" + coordinates);
							startPoint = new Node("customStart", null, null, coordinates, startPointID, null, new HashSet<Connector>());
						} else {
							System.out.println("custom start failed");
						}
					}
					if(endPoint == null){
						System.out.println("Trying to build custom end " + endPointID);
						LatLong coordinates = GeoCode.getCoordinates(endPointID);
						if(coordinates != null){
							System.out.println("custom end success" + coordinates);
							endPoint = new Node("customEnd", null, null, coordinates, endPointID, null, new HashSet<Connector>());
						} else {
							System.out.println("custom end failed");
						}
					}
					if(startPoint != null && endPoint != null){
						GraphBuilder gb = new GraphBuilder(new QueryBuilder("localhost", 5000), Amount.valueOf(426, SI.KILOMETER)/*Tesla Model S 85kWh*/);
						g = new CustomPointFolderGraph(edgeDir, nodeFile, startPoint, endPoint, gb);
						System.out.println("next graph");
					}
					if(endPoint == null || startPoint == null){
						//cannot find chargers
						response.getWriter().println("<h1>Unable to locate specified chargers</h1>");
						return;
					}
				}
			} else if (parameters.containsKey(START_LAT) && parameters.containsKey(START_LON) && parameters.containsKey(END_LAT) && parameters.containsKey(END_LON)){
				double startLat;
				double startLon;
				double endLat;
				double endLon;

				try{
					startLat = Double.parseDouble(parameters.get(START_LAT)[0]);
					startLon = Double.parseDouble(parameters.get(START_LON)[0]);
					endLat = Double.parseDouble(parameters.get(END_LAT)[0]);
					endLon = Double.parseDouble(parameters.get(END_LON)[0]);
				} catch(NumberFormatException e){
					//cannot parse coordinates
					response.getWriter().println("<h1>Unable to parse co-ordinates</h1>");
					return;
				}
				LatLong startLoc = LatLong.valueOf(startLat, startLon, NonSI.DEGREE_ANGLE);
				LatLong endLoc = LatLong.valueOf(endLat, endLon, NonSI.DEGREE_ANGLE);

				startPoint = new Node("customStart", null, null, startLoc, null, null, new HashSet<Connector>());
				endPoint = new Node("customEnd", null, null, endLoc, null, null, new HashSet<Connector>());

				GraphBuilder gb = new GraphBuilder(new QueryBuilder("localhost", 5000), Amount.valueOf(426, SI.KILOMETER)/*Tesla Model S 85kWh*/);
				g = new CustomPointFolderGraph(edgeDir, nodeFile, startPoint, endPoint, gb);
				System.out.println("third graph");
			} else {
				//cannot make start and end points
				response.getWriter().println("<h1>No start and end points specified</h1>");
				return;
			}
			//car needs range, connectors, capacity
			Amount<Length> range;
			Amount<Energy> capacity;

			if(parameters.containsKey(CAR_RANGE) && parameters.containsKey(CAR_CAPACITY)){
				double rangeVal;
				double capacityVal;
				try{
					rangeVal = Double.parseDouble(parameters.get(CAR_RANGE)[0]);
					capacityVal = Double.parseDouble(parameters.get(CAR_CAPACITY)[0]);
				} catch (NumberFormatException e){
					//cannot parse car data
					response.getWriter().println("<h1>Unable to parse car data</h1>");
					return;
				}
				range = Amount.valueOf(rangeVal, NonSI.MILE);
				capacity = Amount.valueOf(capacityVal  *60*60, SI.KILO(SI.JOULE));//kilo watt hours
			} else {
				response.getWriter().println("<h1>Car data not specified</h1>");
				return;
			}

			Car car = new Car("electric vehicle", range, capacity);

			//add connectors using their short descriptions as parameter keys
			//This class is immune to changes in available connectors!
			for(Type t : Type.values()){
				String desc = t.getShortDescription();
				if(parameters.containsKey(desc)){
					if(parameters.get(desc)[0].equals("true")){
						car.addCompatibleConnector(new Connector(t,
								Amount.valueOf(Double.MAX_VALUE, SI.WATT),
								Amount.valueOf(Double.MAX_VALUE, SI.VOLT),
								Amount.valueOf(Double.MAX_VALUE, SI.AMPERE)));
					}
				}
			}


			//find the fastest charger in the network
			Amount<Power> fastestCharge = Amount.valueOf(0, SI.WATT);
			for(Node c : g.getNodes()){
				for(Connector con : c.getConnectors()){
					if(con.getPower().isGreaterThan(fastestCharge)){
						fastestCharge = con.getPower();
					}
				}
			}

			StateTimeComparator comp = new DistanceStoringAStarComparator(endPoint, Amount.valueOf(120, NonSI.KILOMETERS_PER_HOUR)/*ROI motorways*/, fastestCharge);
			Router router;
			//router = new ListPrunedQueueRouter(comp);
			router = new FastChargeRouter(comp);
			//router = new TimeOnlyRouter(comp);
			
			//build a scenario
			Scenario s = new Scenario(g, startPoint, endPoint, car);
			//route that scenario
			long startTime = System.currentTimeMillis();
			State result = router.route(s);
			long endTime = System.currentTimeMillis();
			
			long optimizerStart = System.currentTimeMillis();
			State optimizedResult = ChargeOptimizer.optimize(result, g);
			long optimizerEnd = System.currentTimeMillis();
			
			//display the results
			if(result == null){
				response.getWriter().println("<h1>Route could not be calculated</h1>");
			} else {
				response.getWriter().println("<h1>Route calculated:</h1>");
				
				response.getWriter().println("<h3>" + (int)optimizedResult.getDistance().doubleValue(NonSI.MILE) + "miles" + 
						" in " + (int)optimizedResult.getTime().doubleValue(SI.SECOND) + "s" +
						" using " + (int)car.chargeNeededToTravel(optimizedResult.getDistance()).doubleValue(SI.KILO(SI.JOULE)) + "kJ"+ "</h3>");
				
				response.getWriter().println("<body>" + optimizedResult.getRouteString("<br>"));
				response.getWriter().println(MapEmbed.getEmbedCode(result) + "<br>");
				response.getWriter().println("routing took " + new Double(endTime-startTime).toString() + "ms<br>");
				response.getWriter().println("optimizing took " + new Double(optimizerEnd-optimizerStart).toString() + "ms</body>");
				
				router.printStats();
			}
		} else {
			//else error page
			response.getWriter().println("<h1>CompanyName.website</h1>");
			response.getWriter().println("<h2>If you're reading this, the web server was installed correctly™</h2>");
			response.getWriter().println("<body>Also, error 404: page not found</body>");
			response.setStatus(404);
		}
	}

}
