package sample;

import java.util.Collection;

import router.Edge;
import router.graph.FolderGraph;
import Model.Node;

public class FolderAnalyzer {

	public static void main(String[] args) {
		FolderGraph full = new FolderGraph("./xml/edges/", "./xml/edges/edited_registry.xml");
		FolderGraph reduced = new FolderGraph("./xml/powerCut2/", "./xml/powerCut2/nodes.xml");
		
		analyze(full);
		analyze(reduced);

	}

	public static void analyze(FolderGraph g){
		Collection<Node> chargers = g.getNodes();
		System.out.println(chargers.size() + " chargers");
		int edgeCount = 0;
		for(Node c : chargers){
			Collection<Edge> edges = g.getEdgesFrom(c);
			edgeCount += edges.size();
		}
		System.out.println(edgeCount + " edges");
	}
	
}
