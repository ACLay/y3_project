package web;

import java.util.ArrayList;

import router.State;

public class MapEmbed {

	private static String API_KEY = "AIzaSyBgGuqODb7D-IX0E4BTvWqVIWhrRyAFoSE";
	//map embed api guide: https://developers.google.com/maps/documentation/embed/guide
	
	public static String getEmbedCode(State route){
		return getEmbedCode(route, 600, 450);
	}
	
	public static String getEmbedCode(State route, int width, int height){
		StringBuilder sb = new StringBuilder();

		String widthStr = "\"" + width + "\"";
		String heightStr = "\"" + height + "\"";
		
		sb.append("<iframe").append("\n")
		.append("width=").append(widthStr).append("\n")
		.append("height=").append(heightStr).append("\n")
		.append("frameborder=\"0\" style=\"border:0\"").append("\n")
		.append("src=\"").append(getSrc(route)).append("\">").append("\n")
		.append("</iframe>\n");
		
		return sb.toString();
	}
	
	private static String getSrc(State routeEnd){
		StringBuilder sb = new StringBuilder();
		sb.append("https://www.google.com/maps/embed/v1/");	
		
		ArrayList<State> states = new ArrayList<State>();
		State prev = routeEnd;
		while(prev != null){
			states.add(0, prev);
			prev = prev.getPrevious();
		}
		
		if(states.size() == 0){
			//if no states, blank map
			sb.append("view") 
			.append("?key=").append(API_KEY)
			.append("&center=0,0")
			.append("&zoom=0");
		} else if(states.size() == 1){
			//if 1 state, point on a map
			sb.append("place")
			.append("?key=").append(API_KEY)
			.append("&q=").append(routeEnd.getCoordinateString());
		} else {
			//if 2+ states, draw between
			sb.append("directions")
			.append("?key=").append(API_KEY)
			.append("&origin=").append(states.get(0).getCoordinateString());
			
			if(states.size() > 2){
				//if 3+ states, draw multiple lines
				sb.append("&waypoints=");
				for(int i=1; i<states.size()-1; i++){
					if(i != 1){
						sb.append("|");
					}
					sb.append(states.get(i).getCoordinateString());
				}
			}

			sb.append("&destination=").append(states.get(states.size()-1).getCoordinateString());
			sb.append("&mode=driving");
		}
		
		return sb.toString();
	}

}
