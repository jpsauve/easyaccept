package easyaccept.result;

/**
 * This class represents an Error Event that can occur during the tests execution. 
 * It holds a result object that contains information about the test error.
 * 
 * @author Gustavo Farias
 * @author Magno Jefferson
 * @author Alvaro Magnum
 */
public class ErrorEvent extends java.util.EventObject{

	Result testResult;
	
	/**
	 * Error Event constructor.
	 * 
	 * @param 
	 * 			the Result object that contains the error information.
	 */
	public ErrorEvent(Result event) {
		super(event);
		testResult = event;
	}

	/**
	 * Returns the error message of the error event.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage(){
		return testResult.getErrorMessage();
	}
	
	/**
	 * Returns the exception associated with the test error.
	 * 
	 * @return The exception.
	 */
	public Throwable getException(){
		return testResult.getException();
	}
	
	/**
	 * Returns the command that produced the test error.
	 * 
	 * @return the command that produced the test error.
	 */
	public String getCommand(){
		return testResult.getCommand();
	}
	
}
