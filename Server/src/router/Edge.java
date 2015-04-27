package router;

import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;

import org.jscience.physics.amount.Amount;

import Model.Node;

public class Edge {

	private Node startPoint;
	private Node endPoint;
	private Amount<Length> distance;
	private Amount<Duration> time;
	
	
	public Edge(Node start, Node end, Amount<Length> distance, Amount<Duration> time){
		this.startPoint = start;
		this.endPoint = end;
		this.distance = distance;
		this.time = time;
	}
	
	public Node getStartPoint(){
		return startPoint;
	}
	
	public Node getEndPoint(){
		return endPoint;
	}
	
	public Amount<Length> getDistance(){
		return distance;
	}
	
	public Amount<Duration> getTravelTime(){
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((distance == null) ? 0 : distance.hashCode());
		result = prime * result
				+ ((endPoint == null) ? 0 : endPoint.hashCode());
		result = prime * result
				+ ((startPoint == null) ? 0 : startPoint.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Edge))
			return false;
		Edge other = (Edge) obj;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.approximates(other.distance))
			return false;
		if (endPoint == null) {
			if (other.endPoint != null)
				return false;
		} else if (!endPoint.equals(other.endPoint))
			return false;
		if (startPoint == null) {
			if (other.startPoint != null)
				return false;
		} else if (!startPoint.equals(other.startPoint))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.approximates(other.time))
			return false;
		return true;
	}
	
}
