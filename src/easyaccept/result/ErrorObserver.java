package easyaccept.result;

/**
 * interface to be implemented by the error notifier.
 * 
 * @author Magno Jefferson
 * @author Gustavo Farias
 *
 */
public interface ErrorObserver {
	
	void notifyError(ErrorEvent event);
	void addListener(ErrorListener listener);
	void removeListener(ErrorListener listener);

}
