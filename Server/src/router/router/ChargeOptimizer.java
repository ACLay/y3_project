package router.router;

import Model.Node;
import router.Edge;
import router.Scenario;
import router.State;
import router.comparator.StateTimeComparator;
import router.graph.Graph;
import router.graph.RamGraph;

public class ChargeOptimizer {

	public static State optimize(State finalState, Graph g){
		//build the new graph
		System.out.println("building optimizer graph");
		Graph reduced = buildGraph(finalState, g);
		System.out.println("Optimizer graph built");
		//route it with dijkstra's
		Node startPoint,endPoint;
		endPoint = finalState.getLocation();
		startPoint = finalState.getLocation();
		State firstState = finalState;
		while(firstState.getPrevious() != null){
			firstState = firstState.getPrevious();
		}
		startPoint = firstState.getLocation();
		Router router = new ListPrunedQueueRouter(new StateTimeComparator());
		Scenario scenario = new Scenario(reduced, startPoint, endPoint, finalState.getCar());
		System.out.println("Optimizer scenario built, starting routing");
		State optimized = router.route(scenario);
		System.out.println("Optimization complete");
		return optimized;
	}
	
	private static RamGraph buildGraph(State finalState, Graph g){
		RamGraph reduced = new RamGraph();
		//add the final state
		reduced.addNode(finalState.getLocation());
		State edgeEndState = finalState;
		State previousState = finalState.getPrevious();
		//for each state and its predecessor
		while(previousState != null){
			//add the predecessor to the graph
			reduced.addNode(previousState.getLocation());
			//add an edge between them
			for(Edge edge : g.getEdgesFrom(previousState.getLocation())){
				if(edge.getEndPoint().equals(edgeEndState.getLocation())){
					reduced.addEdge(edge);
					break;
				}
			}
			edgeEndState = previousState;
			previousState = edgeEndState.getPrevious();
		}
		return reduced;
	}
}
