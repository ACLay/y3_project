package router;

import router.graph.Graph;
import Model.Car;
import Model.Charger;

public class Scenario {
	
	protected Graph graph;
	protected Charger start;
	protected Charger finish;
	protected Car car;
	
	public Scenario(Graph g, Charger start, Charger finish, Car car){
		this.graph = g;
		this.start = start;
		this.finish = finish;
		this.car = car;
	}
	
	public Graph getGraph(){
		return graph;
	}
	
	public Charger getStart(){
		return start;
	}
	
	public Charger getFinish(){
		return finish;
	}
	
	public Car getCar(){
		return car;
	}
	
}
