package sample;

import java.util.ArrayList;
import java.util.HashSet;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.QueueRouter;
import router.Router;
import router.Scenario;
import router.State;
import router.StateTimeComparator;
import router.graph.Graph;
import router.graph.RamGraph;
import Model.Car;
import Model.Charger;
import Model.connectors.Connector;
import Model.connectors.Type;

public class RouteTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Graph g = new RamGraph();
		HashSet<Connector> connectors = new HashSet<Connector>();
		connectors.add(new Connector(Type.IEC_TYPE_3, Amount.valueOf("2750W").to(SI.WATT), Amount.valueOf("250V").to(SI.VOLT), Amount.valueOf("7A").to(SI.AMPERE)));
		HashSet<Connector> fastConns = new HashSet<Connector>();
		fastConns.add(new Connector(Type.IEC_TYPE_3, Amount.valueOf("3250W").to(SI.WATT), Amount.valueOf("250V").to(SI.VOLT), Amount.valueOf("13A").to(SI.AMPERE)));
		
		ArrayList<Charger> nodes = new ArrayList<Charger>();
		Charger startPoint = new Charger("1","c1","start point",null,"Bag end","Hobbiton",connectors);
		Charger endPoint = new Charger("2","c2","end point",null,"Mt. Doom","Mordor",connectors);
		nodes.add(startPoint); nodes.add(endPoint);
		
		Charger midPoint1 = new Charger("3","c3","midpoint 1",null,"prancing pony","bree", fastConns);
		Charger midPoint2 = new Charger("4","c4","midpoint 2",null,"orthanc","isengard", connectors);
		nodes.add(midPoint1); nodes.add(midPoint2);
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(startPoint,midPoint1,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		edges.add(new Edge(midPoint1,midPoint2,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		edges.add(new Edge(midPoint2,endPoint,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		
		g.addNodes(nodes);
		g.addEdges(edges);
		
		Car c = new Car("Shadowfax", Amount.valueOf(900,SI.METER), Amount.valueOf(15, SI.MEGA(SI.JOULE)));
		c.addCompatibleConnectors(fastConns);
		//check startpoint.cancharge(shadowfax)
		System.out.println(startPoint.canCharge(c));
		System.out.println(midPoint1.canCharge(c));
		
		Router r = new QueueRouter(new StateTimeComparator());

		State endState = r.route(new Scenario(g,startPoint,endPoint,c));

		if(endState == null){
			System.out.println("Cannot route");
		} else {
			System.out.println(endState.getRouteString("\n"));
		}
		
	}

}
