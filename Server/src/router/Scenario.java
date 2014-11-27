package router;

import router.graph.Graph;
import Model.Car;
import Model.Charger;

public class Scenario {
	
	private Graph g;
	private Charger start;
	private Charger finish;
	private Car car;
	
	public Scenario(Graph g, Charger start, Charger finish, Car car){
		this.g = g;
		this.start = start;
		this.finish = finish;
		this.car = car;
	}
	
	public Graph getGraph(){
		return g;
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
