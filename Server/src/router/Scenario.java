package router;

import router.graph.Graph;
import Model.Car;
import Model.Node;

public class Scenario {
	
	protected Graph graph;
	protected Node start;
	protected Node finish;
	protected Car car;
	
	public Scenario(Graph g, Node start, Node finish, Car car){
		this.graph = g;
		this.start = start;
		this.finish = finish;
		this.car = car;
	}
	
	public Graph getGraph(){
		return graph;
	}
	
	public Node getStart(){
		return start;
	}
	
	public Node getFinish(){
		return finish;
	}
	
	public Car getCar(){
		return car;
	}
	
}
