package eu.boxwork.example.http;


/**
 * Diese Klasse h�lt den Status einer Verbindung. Die Aktualisierung erfolgt extern.
 * @author Patrick Jungk
 * @version 1.0
 */
public class StateEntry {

	/*
	 * oeffentliche Status, den eine Verbindung annehmen kann
	 * */
	public enum STATE {NEW, OPEN, ACTIVE, CLOSED};
	
	/*
	 * Statusinformationen zu einem Host 
	 * */
	private String host = "";
	private STATE state = STATE.NEW;
	private long lastRequestTime = 0;
		
	/**
	 * @return the lastRequestTime
	 */
	public long getLastRequestTime() {
		return lastRequestTime;
	}
	/**
	 * @param lastRequestTime the lastRequestTime to set
	 */
	public void setLastRequestTime(long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @return the state
	 */
	public STATE getState() {
		return state;
	}
		
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(STATE state) {
		this.state = state;
	}
	
	/**
	 * @return {@link String}, gibt einen aufbereiteten HTML-String (Tabellen-Eintrag) zur�ck.
	 * */
	public String toHTML()
	{
		return "<TR><TH>"+getHost()+"</TH><TD>"+getState().toString()+"</TD><TD>"+getLastRequestTime()+"</TD></TR>";
	}
}
