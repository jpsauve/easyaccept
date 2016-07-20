package easyaccept.result;

/**
 * The interface to be implemented by any application interested in error events.
 * 
 * @author Magno Jefferson
 * @author Gustavo Farias
 *
 */
public interface ErrorListener extends java.util.EventListener{
	
	/**
	 * Method to sign as an error listener.
	 */
	void receiveTestErrorsNotifications(ErrorEvent event);
}
