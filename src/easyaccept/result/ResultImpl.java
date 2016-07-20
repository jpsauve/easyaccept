package easyaccept.result;

/**
 * Stores the results of a single script command after execution.
 * 
 * @author jacques
 */
public class ResultImpl implements Result {

	/**
	 * The executed command associated with this Result.
	 */
	private String command;
	/**
	 * The object returned by the command when executed.
	 */
	private Object result;
	/**
	 * The exception thrown by the command when executed.
	 */
	private Throwable exception;

	/**
	 * The script line associated to this result.
	 */
	private int line = 0;
	private long time;
	private String timeTraceMessage;

	/**
	 * A Result constructor.
	 * 
	 * @param command
	 *            The command that produced this Result.
	 * @param result
	 *            The Result object returned by the command.
	 * @param exception
	 *            The exception thrown by the command when executed.
	 * @param timeTraceMessage
	 *            The time trace message generated when executing.
	 */
	public ResultImpl(String command, Object result, Throwable exception, String timeTraceMessage) {
		this.command = command;
		this.result = result;
		this.exception = exception;
		this.timeTraceMessage = timeTraceMessage;
	}

	/**
	 * Returns the script line associated with this Result
	 */
	public int getLine() {
		return this.line;
	}

	/**
	 * Sets the script line associated with this test Result.
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * Returns the command that produced the Result.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the Result object.
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @return The string equivalent of the object returned by the command when
	 *         executed.
	 */
	public String getResultAsString() {
		return result == null ? "null" : result.toString();
	}

	/**
	 * @return null, if no exception was thrown by the command that produced
	 *         this Result; otherwise, the error message contained in the
	 *         exception that was thrown is returned.
	 */
	public String getErrorMessage() {
		if (exception == null) {
			return "(no exception)";
		} else if (exception.getMessage() == null) {
			return "(no message: exception = " + exception.getClass().getName()
					+ ")";
		}

		return exception.getMessage();
	}

	/**
	 * @return The exception thrown by the command when executed.
	 * @uml.property name="exception"
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return true, if the command that produced this Result threw an exception
	 *         when executed.
	 */
	public boolean hasError() {
		return exception != null;
	}

	public void setExecutionTimeInMilliseconds(long time) {
		this.time = time;
	}

	public long getExecutionTimeInMilliseconds() {
		return time;
	}

	public void setTimeTraceMessage(String timeTraceMessage) {
		this.timeTraceMessage = timeTraceMessage;
	}

	public String getTimeTraceMessage() {
		return timeTraceMessage;
	}

	public boolean hasTimeTraceMessage() {
		return timeTraceMessage != null;
	}
}