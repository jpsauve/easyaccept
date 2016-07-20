package easyaccept.result;

/**
 * This interface determines the main test results characteristics.
 * 
 * @author Magno Jefferson
 */
public interface Result {

	/**
	 * Returns the script line associated with this Result
	 */
	public int getLine();

	/**
	 * Returns the command that produced the Result.
	 */
	public String getCommand();

	/**
	 * Returns the Result object.
	 */
	public Object getResult();

	/**
	 * Returns the Result as a string.
	 */
	public String getResultAsString();

	/**
	 * Returns the error message associated with this Result.
	 */
	public String getErrorMessage();

	/**
	 * Returns the exception associated with a test error.
	 */
	public Throwable getException();

	/**
	 * Returns true if some error has occurred. False, otherwise.
	 */
	public boolean hasError();

	/**
	 * Sets the script line associated with this test Result.
	 */
	public void setLine(int line);

	/**
	 * Sets the command's execution time in milliseconds.
	 */
	public void setExecutionTimeInMilliseconds(long time);

	/**
	 * Returns the command's execution time in milliseconds.
	 */
	public long getExecutionTimeInMilliseconds();

	/**
	 * Sets the time trace message.
	 */
	public void setTimeTraceMessage(String timeTraceMessage);

	/**
	 * Returns the time trace message.
	 */
	public String getTimeTraceMessage();

	/**
	 * Returns true if some trace message has been set.
	 */
	public boolean hasTimeTraceMessage();
}
