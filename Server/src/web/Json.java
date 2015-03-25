package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public class Json {
	
	public static JSONObject readURL(URL url) throws IOException{
		URLConnection yc = url.openConnection();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						yc.getInputStream()));
		
		StringBuilder urlData = new StringBuilder();
		
		String inputLine;
		
		while ((inputLine = br.readLine()) != null){
			urlData.append(inputLine).append("\n");
		}
		br.close();
		
		String jsonText = urlData.toString();
		return new JSONObject(jsonText);
	}
	
}
