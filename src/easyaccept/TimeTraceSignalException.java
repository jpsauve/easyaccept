package easyaccept;

public class TimeTraceSignalException extends EasyAcceptException {
	private static final long serialVersionUID = -254302862071433114L;

	public TimeTraceSignalException(String fileName, int lineNumber, long executionTime) {
		this(fileName, lineNumber, executionTime, null);
	}
	
	public TimeTraceSignalException(String fileName, int lineNumber, long executionTime, Throwable cause) {
		super(fileName, lineNumber, "Execution time: " + executionTime + " ms", cause);
	}
}
