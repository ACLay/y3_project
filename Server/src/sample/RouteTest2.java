package sample;

import java.util.ArrayList;
import java.util.HashSet;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import router.Edge;
import router.Router;
import router.State;
import router.graph.Graph;
import router.graph.RamGraph;
import Model.Car;
import Model.Charger;
import Model.connectors.Connector;
import Model.connectors.Type;

public class RouteTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Graph g = new RamGraph();
		HashSet<Connector> connectors = new HashSet<Connector>();
		connectors.add(new Connector(Type.IEC_TYPE_3, Amount.valueOf("1750W").to(SI.WATT), Amount.valueOf("250V").to(SI.VOLT), Amount.valueOf("7A").to(SI.AMPERE)));
		HashSet<Connector> medConns = new HashSet<Connector>();
		medConns.add(new Connector(Type.IEC_TYPE_3, Amount.valueOf("2500W").to(SI.WATT), Amount.valueOf("250V").to(SI.VOLT), Amount.valueOf("10A").to(SI.AMPERE)));
		HashSet<Connector> carConns = new HashSet<Connector>();
		carConns.add(new Connector(Type.IEC_TYPE_3, Amount.valueOf("3250W").to(SI.WATT), Amount.valueOf("250V").to(SI.VOLT), Amount.valueOf("13A").to(SI.AMPERE)));
		HashSet<Connector> fastConns = new HashSet<Connector>();
		fastConns.add(new Connector(Type.IEC_TYPE_2, Amount.valueOf("3250W").to(SI.WATT), Amount.valueOf("250V").to(SI.VOLT), Amount.valueOf("13A").to(SI.AMPERE)));
		
		ArrayList<Charger> nodes = new ArrayList<Charger>();
		Charger startPoint = new Charger("1","c1","start point",null,"Bag end","Hobbiton",connectors);
		Charger endPoint = new Charger("2","c2","end point",null,"Mt. Doom","Mordor",connectors);
		nodes.add(startPoint); nodes.add(endPoint);
		
		Charger midPoint1 = new Charger("3","c3","midpoint 1",null,"prancing pony","bree", connectors);
		Charger midPoint2 = new Charger("4","c4","midpoint 2",null,"orthanc","isengard", medConns);
		Charger midPoint3 = new Charger("5","c5","midpoint 3",null,"helms deep","rohan",fastConns);
		nodes.add(midPoint1); nodes.add(midPoint2); nodes.add(midPoint3);
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(new Edge(startPoint,midPoint1,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		edges.add(new Edge(startPoint,midPoint2,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		edges.add(new Edge(startPoint,midPoint3,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		
		edges.add(new Edge(midPoint1,endPoint,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		edges.add(new Edge(midPoint2,endPoint,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		edges.add(new Edge(midPoint3,endPoint,Amount.valueOf("500m").to(SI.METER), Amount.valueOf("1000s").to(SI.SECOND)));
		
		g.addNodes(nodes);
		g.addEdges(edges);
		
		Car c = new Car("Shadowfax", Amount.valueOf(900,SI.METER), Amount.valueOf(15, SI.MEGA(SI.JOULE)));
		c.addCompatibleConnectors(carConns);
		//check startpoint.cancharge(shadowfax)
		System.out.println(startPoint.canCharge(c));
		System.out.println(midPoint1.canCharge(c));
		System.out.println(midPoint3.canCharge(c));
		
		Router r = new Router(g);

		State endState = r.route(startPoint, endPoint, c);

		if(endState == null){
			System.out.println("Cannot route");
		} else {
			endState.printRoute();
		}
		
	}

}
