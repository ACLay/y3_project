package web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.measure.unit.NonSI;

import org.jscience.geography.coordinates.LatLong;
import org.json.JSONObject;

public class GeoCode {
	// https://developers.google.com/maps/documentation/geocoding/#Limits
	public static LatLong getCoordinates(String location){
		String urlString = buildURL(location);
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			System.out.println("Bad URL");
			return null;
		}
		JSONObject response;
		try {
			response = Json.readURL(url);
		} catch (IOException e) {
			System.out.println("Unable to read JSON");
			e.printStackTrace();
			return null;
		}
		String status = response.getString("status");
		if (status.equals("OK")){
			JSONObject topResult = response.getJSONArray("results").getJSONObject(0);
			JSONObject jsonLoc = topResult.getJSONObject("geometry").getJSONObject("location");
			Double lat = jsonLoc.getDouble("lat");
			Double lon = jsonLoc.getDouble("lng");
			return LatLong.valueOf(lat, lon, NonSI.DEGREE_ANGLE);
		} else {
			return null;
		}
	}
	
	private static String buildURL(String location){
		StringBuilder sb = new StringBuilder();
		String encodedLocation="";
		try {
			encodedLocation = URLEncoder.encode(location, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sb.append("https://maps.googleapis.com/maps/api/geocode/json?address=").append(encodedLocation)
		.append("&key=").append(MapEmbed.API_KEY);
		
		return sb.toString();
	}
	
}
