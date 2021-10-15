package eu.boxwork.example.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

/**
 * Diese Klasse verarbeitet HTML - Zeit - Anfragen
 * @author Patrick Jungk
 * @version 1.0
 */
public class WebrequestWorker implements Runnable {
	protected static Logger Log = LogManager.getLogger(WebrequestWorker.class.getName());
	/*
	 * Auflistung der unterstuetzen URLs
	 * */
	public static final String LOCALTIME = "/time";
	public static final String CLIENTSTATE = "/state";
	private int throttle = 0; // Bremse um DOS zu verzögern
	
	private Socket clientSocket = null;
	private String client = "";
	
	public WebrequestWorker(Socket clientSocket, int throttle) {
		super();
		this.clientSocket = clientSocket;
		this.throttle = throttle;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (this.clientSocket!=null)
		{
			try {
				handleSocketRequest(this.clientSocket);
			} catch (Exception e) {
				Log.error(e.getLocalizedMessage());
			}
			if (throttle>0)
			{
				try {
					Thread.sleep(throttle);
				} catch (InterruptedException e) {
					Log.error(e.getLocalizedMessage());
				}
			}
		}
		else
		{
			Log.error("no client socket. terminating");
		}
	}

	/**
	 * Bearbeitet den Request am Socket
	 * @param clientSocket {@link Socket} des aktuellen Clients
	 * */
	private void handleSocketRequest(Socket clientSocket) throws Exception
	{
		Long start = Calendar.getInstance().getTimeInMillis();
		Log.info("accepting connection from Client: "+clientSocket);
		client = clientSocket.getInetAddress().getHostAddress() + "/" +clientSocket.getPort();
		
		WebstateStorage.getInstance().setConnection(client, StateEntry.STATE.NEW, Calendar.getInstance().getTimeInMillis());
		
		PrintWriter out =
		        new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(clientSocket.getInputStream()));	
		boolean closeConnection = false;
		
		
		while(clientSocket.isConnected() && !closeConnection)
		{					
			/* erst Request: lese Daten von Client */	
			String line = in.readLine();
			
			String method = "";
			String actRequestLink = "";
			
			if (line!=null && !line.trim().equals(""))
			{
				WebstateStorage.getInstance().setConnection(client, StateEntry.STATE.ACTIVE, Calendar.getInstance().getTimeInMillis());
				
				String[] requ = line.split(" ");
				method = requ[0];
				actRequestLink = requ[1];
				
				String request = ""; 
				while (line!=null && !line.equals(""))
				{							
					request=request+line+"\n";
					line = in.readLine();
				}					
				Log.info("client request: "+request);
				if (method.trim().toLowerCase().equals("get"))
				{
					/* Antworte auf Request */
					String answer=getResponse(actRequestLink,start );
					Log.info("Writing to client: "+answer);
					out.write(answer+"\0");
					out.flush();
				}
				else
				{
					/* Antworte auf Request mit Fehler; da nicht unterst�tzt*/
					String answer=getResponseError();
					Log.info("Writing to client: "+answer);
					out.write(answer+"\0");
					out.flush();
				}
			}						
			closeConnection=true;
			if (throttle>0)
			{
				try {
					Thread.sleep(throttle);
				} catch (InterruptedException e) {
					Log.error(e.getLocalizedMessage());
				}
			}
		}

		Log.info("closing connection to Client: "+clientSocket);
		clientSocket.close();
		WebstateStorage.getInstance().setConnection(client, StateEntry.STATE.CLOSED, Calendar.getInstance().getTimeInMillis());
		
	}
	

	/**
	 * Erstellt eine Fehlermeldung, wenn etwas nicht vom Server unterst�tzt wird
	 * @return {@link String} Fehlermeldung.
	 * */
	private String getResponseError()
	{
		int statusCode=500;
		String header = getResponseHeader(statusCode, "Not supported by server");
		return header+"INTERNAL SERVER ERROR 500.\n";
	}
	
	/**
	 * Erstellt die Response zu einer URL.
	 * @param url Pfad anzuzeigen
	 * @param additinalInfo Info, die angeh�ngt wird
	 * @return {@link String} Nachricht zur URL oder Fehlermeldung 404
	 * */
	private String getResponse(String url, Object additinalInfo)
	{
		String header = ""; 
		String content = "";
		if (url.equals(LOCALTIME))
		{
			header = getResponseHeader(200, "OK");
			content = getContentLocalTime(((Long)additinalInfo));
		}
		else if (url.equals(CLIENTSTATE))
		{
			header = getResponseHeader(200, "OK");
			content = getClientOverview();
		}
		else
		{
			header = getResponseHeader(404, "Not Found");
			content = "ERROR 404: requested URL '"+url+"' not found";
		}
		return header+"\n"+content;
	}

	/**
	 * Erstellt die Antwort f�r die Client�bersicht
	 * @return {@link String} aktuelle Client�bersicht
	 * */
	private String getClientOverview() {		
		return "<HTML><HEAD><meta http-equiv='refresh' content='1'></HEAD><BODY>"
				+ WebstateStorage.getInstance().getStates()
				+ "</BODY></HTML>";
	}
	
	/**
	 * Erstellt die Antwort f�r die Serverzeit.
	 * @param startTime Zeit, zu der der request reingekommen ist
	 * @return {@link String} aktuelle Serverzeit in ms.
	 * */
	private String getContentLocalTime(Long startTime) {
		long diff = Calendar.getInstance().getTimeInMillis() - startTime;
		
		return "<HTML><HEAD></HEAD><BODY><TABLE>"+
				"<TR><TH>CLIENT</TH><TD>"+client+"</TD></TR>"+
				"<TR><TH>INCOMING-Time</TH><TD>"+startTime+"</TD></TR>"+
				"<TR><TH>ACTUAL-Time</TH><TD>"+Calendar.getInstance().getTimeInMillis()+"</TD></TR>"+
				"<TR><TH>Request-Duration</TH><TD>"+diff+"</TD></TR>"+
				"</TABLE></BODY></HTML>";
	}

	/**
	 * Erstellt einen Status Header f�r den Status samt html Text.
	 * @param statusCode HTML Status code
	 * @param statusMessage Nachricht zu dem Status
	 * @return Ersteller HTML Header samt aktueller Zeit
	 * */
	private String getResponseHeader(int statusCode, String statusMessage) {
		/*
HTTP/1.x 200 OK
Date: Tue, 08 Sep 2009 15:47:06 GMT
Server: Apache/1.3.34 Ben-SSL/1.55
Keep-Alive: timeout=2, max=200
Connection: Keep-Alive
Transfer-Encoding: chunked
Content-Type: text/html
		 * */		
		String ret = "HTTP/1.x "+statusCode+" "+statusMessage+"\n"+
		"Date: "+Calendar.getInstance().getTime().toGMTString()+"\n"+
		"Server: Simple-JAVA-Webserver Ben-SSL/1.55\n"+
		"Content-Language: de\n"+
		"Content-Type: text/html\n"+
		"Connection: close\n";
		return ret;
	}
	
}
