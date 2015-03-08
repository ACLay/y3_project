package router;

import javax.measure.quantity.Length;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import Model.Charger;

public class Geography {

	final static Amount<Length> earthRadius = Amount.valueOf(6353, SI.KILOMETRE);
	
	public static Amount<Length> haversineDistance(Charger s1, Charger s2){
		double lat1 = s1.getCoordinates().latitudeValue(SI.RADIAN);
		double lon1 = s1.getCoordinates().longitudeValue(SI.RADIAN);
		
		double lat2 = s2.getCoordinates().latitudeValue(SI.RADIAN);
		double lon2 = s2.getCoordinates().longitudeValue(SI.RADIAN);
		
		double internal = Math.pow(Math.sin((lat2 - lat1)/2), 2) + Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin((lon2 - lon1)/2),2);
		double root = Math.sqrt(internal);
		return earthRadius.times(2*Math.asin(root));
	}
	
}
