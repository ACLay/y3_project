package web;

import java.io.IOException;

import javax.measure.unit.NonSI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jscience.geography.coordinates.LatLong;

import Model.Node;

import router.State;

public class HelloWorld extends AbstractHandler{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Server server = new Server(8080);
		server.setHandler(new HelloWorld());
		
		server.start();
		server.join();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Hello World</h1>");
		State s1 = new State(new Node(null, null, null, LatLong.valueOf(51.029304, -1.433716, NonSI.DEGREE_ANGLE), null, null, null), null, null, null, null, null);
		State s2 = new State(new Node(null, null, null, LatLong.valueOf(52.509245, -1.906525, NonSI.DEGREE_ANGLE), null, null, null), null, null, s1, null, null);
		State s3 = new State(new Node(null, null, null, LatLong.valueOf(53.475582, -2.217379, NonSI.DEGREE_ANGLE), null, null, null), null, null, s2, null, null);
		response.getWriter().println(MapEmbed.getEmbedCode(null));
		response.getWriter().println(MapEmbed.getEmbedCode(s1));
		response.getWriter().println(MapEmbed.getEmbedCode(s2));
		response.getWriter().println(MapEmbed.getEmbedCode(s3));
	}

}
