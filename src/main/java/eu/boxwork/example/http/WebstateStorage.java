package eu.boxwork.example.http;

import java.util.HashMap;
import java.util.Map;


/**
 * Diese h�lt zentral durch das Singleton die Statusinformationen. Der erste Thread, erstellt den Speicher.
 * @author Patrick Jungk
 * @version 1.0
 */
public class WebstateStorage {
	/*
	 * private Elemente: Singleton und die eigentliche Map zum Halten der Informationen
	 * */
	private static WebstateStorage instance = null;
	private Map<String, StateEntry> entries = new HashMap<>();
	
	/**
	 * privater Konstruktor. Singleton.
	 * */
	private WebstateStorage()
	{}

	/**
	 * @return this storage as an instance
	 */
	public static synchronized WebstateStorage getInstance() {
		if (instance==null) instance = new WebstateStorage();
		return instance;
	}
	
	/**
	 * aktualisiert die Verbindungsinformationen zu einem Server
	 * @param host Host-Name
	 * @param stateIn aktueller Status
	 * @param requestTime Eingangszeit des Requests bzw. der Aktualisierung
	 * */
	public void setConnection(String host, StateEntry.STATE stateIn, long requestTime)
	{
		synchronized (entries) {
			if (!entries.containsKey(host))
			{
				StateEntry state = new StateEntry();
				state.setHost(host);
				state.setState(stateIn);
				state.setLastRequestTime(requestTime);
				entries.put(host,state);
			}
			entries.get(host).setState(stateIn);
			entries.get(host).setLastRequestTime(requestTime);
		}
	}
	
	/**
	 * @return {@link String} gibt die Statusinformationen zu allen Eintr�gen zur�ck
	 * */
	public synchronized String getStates()
	{
		String ret = "<TABLE><TR><TH>HOST</TH><TH>STATE</TH><TH>LAST REQUEST</TH></TR>";
		synchronized (entries) {			
			for (Map.Entry<String, StateEntry> entry : entries.entrySet())
			{
				ret = ret + entry.getValue().toHTML();
			}
		}
		ret = ret + "</TABLE>";
		return ret;
	}
}
