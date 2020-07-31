package eu.boxwork.example.webservice;

import javax.ws.rs.core.Application;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Webserver extends Application{
	private static final Logger logger = LogManager.getLogger(Webserver.class);
	private static final String PROTOCOL = "http";
	public static final String BASEURL = "example";
	private HttpServer server=null;
	
	/**
	 * starts the webserver, IP and port has to be set as parameter
	 * like java -jar webserverexample.jar localhost 8080
	 * @param args: 0 = IP, 1 = port
	 * */
	public static void main( String[] args )
	{	
		if (args.length!=2)
		{
			logger.error("unable to start server. server needs 2 params: <IP> <PORT>");
			System.exit(-1);
		}

		String ip = args[0];
		String port = args[1];
		
		Webserver server = new Webserver();
		server.startServer(ip, port);
	}

	/**
	 * starts the http server for the webservice, protocol is http per default
	 * as set as the static final variable
	 * @param ip IP to use, maybe a name or localhost
	 * @param port port to use
	 * */
	public void startServer(String ip, String port)
	{
		String serverString = PROTOCOL+"://"+ip+":"+port+"/";
		String baseURLConf = BASEURL;
		if (baseURLConf==null)
		{
			logger.error("unable to start server, no base url given");
			System.exit(-2);
		}
		String baseURLRoot = serverString+baseURLConf;
		try {
			// add resources here
			ResourceConfig rc = new PackagesResourceConfig("eu.boxwork.example.webservice.services");

			// we could add a CORSFilter e.g. for Browser access
			// rc.getContainerResponseFilters().add(new CORSFilter());

			// we add a service exception handler
			rc.getSingletons().add(new ServiceExceptionMapper());

			// let us create the final server now
			server = HttpServerFactory.create( serverString,rc  );
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("unable to start server: "+e.toString());
		}
		if (server!=null)
		{
			server.start();
			logger.info("started server at IP '"+ip+"' and port '"+port+"'. Request base URL: "+baseURLRoot);
		}
		else
		{
			logger.error("server not started !");
		}
	}
}
