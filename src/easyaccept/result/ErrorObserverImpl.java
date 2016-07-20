package easyaccept.result;
import java.util.ArrayList;

/**
 * The ErrorObserverImpl notifies the listeners about occurred errors.
 * 
 * @author Gustavo Farias
 * @author Magno Jefferson
 * @author Alvaro Magnum
 */
public class ErrorObserverImpl implements ErrorObserver{
	
	private static ArrayList<ErrorListener> listeners = new ArrayList<ErrorListener>(); 
	private static ErrorObserverImpl uniqueInstance = null;
	
	/**
	 * ErrorObserverImpl singleton constructor.
	 */
	public static ErrorObserverImpl getInstance() {
		if(uniqueInstance == null)
			uniqueInstance = new ErrorObserverImpl();
		return uniqueInstance;
	}
	
	/**
	 * Notifies all the listeners about errors occurence.
	 * 
	 * @param the error event to be notified.
	 */
	public void notifyError(ErrorEvent event) {
		for (ErrorListener listener : listeners) {
			listener.receiveTestErrorsNotifications(event);
		}
	}	
	
	/**
	 * Adds an error listener.
	 * 
	 * @param the listener.
	 */
	public void addListener(ErrorListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a listener from ther listeners list.
	 * 
	 * @param The listener to be removed.
	 */
	public void removeListener(ErrorListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Informs if the tests execution has signed listeners.
	 * 
	 * @return  true is there are signed listeners, false otherwise.
	 */
	public boolean hasListeners() {
		return this.listeners.size() > 0;
	}

}
