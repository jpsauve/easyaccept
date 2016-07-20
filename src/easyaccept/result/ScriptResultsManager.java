package easyaccept.result;

import java.util.Map;
import java.util.TreeMap;

/**
 * This is the script results manager. The manager storages information about a
 * test script execution results.
 * 
 * @author Gustavo Farias
 * @author Alvaro Magnum
 * @author Magno Jefferson
 */
public class ScriptResultsManager {

	private Map<Integer, Result> allScriptResults;

	private String file;

	private int numberOfTimeTraces;

	private int numberOfErrors;

	private int numberOfPassedTests;

	private long totalTimeOfExecution;

	/**
	 * The ScriptResultManager constructor.
	 * 
	 * @param file
	 *            the name of the test script to be maneged by the
	 *            ScriptResultManger
	 */
	public ScriptResultsManager(String file) {
		this.file = file;
		this.allScriptResults = new TreeMap<Integer, Result>();
		this.numberOfErrors = 0;
		this.numberOfPassedTests = 0;
		this.totalTimeOfExecution = 0;
	}

	/**
	 * Gets the name of the file that the ScriptResultManager is responsible
	 * for.
	 * 
	 * @return the name of the test file managed by the ScriptResultManager.
	 */
	public String getFile() {
		return this.file;
	}

	/**
	 * Adds a single Result for a line in the test file to the
	 * ScriptResultManager
	 * 
	 * @param oneResult
	 */

	/**
	 * Adds a test Result and its associated line to the script results list.
	 * 
	 * @param oneResult
	 *            The Result to be added at the script manager list.
	 */
	public void addResult(Result oneResult) {
		this.allScriptResults.put(oneResult.getLine(), oneResult);
		this.totalTimeOfExecution += oneResult.getExecutionTimeInMilliseconds();

		if (oneResult.hasError()) {
			this.numberOfErrors++;
		} else {
			this.numberOfPassedTests++;
		}
		if (oneResult.hasTimeTraceMessage()) {
			this.numberOfTimeTraces++;
		}
	}

	/**
	 * Returns all the test results of the test script associated with this
	 * manager.
	 * 
	 * @return a map that contains all the test results of the test script
	 *         associated with this manager.
	 */
	public Map<Integer, Result> getResults() {
		return this.allScriptResults;
	}

	/**
	 * Returns the number of errors of the test script associated with this
	 * manager.
	 * 
	 * @return the number of errors.
	 */
	public int getNumberOfErrors() {
		return numberOfErrors;
	}

	/**
	 * Returns the number of time traces of the test script associated with this
	 * manager.
	 * 
	 * @return the number of time traces.
	 */
	public int getNumberOfTimeTraces() {
		return numberOfTimeTraces;
	}

	/**
	 * Returns the number of passed tests of the test script associated with
	 * this manager.
	 * 
	 * @return The number of passed tests.
	 */
	public int getNumberOfPassedTests() {
		return numberOfPassedTests;
	}

	/**
	 * Returns the total number tests of the test script associated with this
	 * manager.
	 * 
	 * @return The total number of tests.
	 */
	public int getNumOfTests() {
		return this.allScriptResults.size();
	}

	/**
	 * This method returns the test result of a specified script line or null if
	 * the test script has not the specified line.
	 * 
	 * @param line
	 *            the test script line which the acceptance test is located.
	 * 
	 * @return The test result of the specified script line or null if the test
	 *         script has not the specified line.
	 */
	public Result getLineResult(int line) {
		if (allScriptResults.containsKey(line)) {
			return this.allScriptResults.get(line);
		}
		return null;
	}

	/**
	 * Returns the script's total execution time, in milliseconds.
	 * 
	 * @return The script's total execution time.
	 */
	public long getTotalTimeOfExecutionInMilliseconds() {
		return totalTimeOfExecution;
	}

	/**
	 * Cleans the ScriptResultManager class
	 */
	public void clear() {
		this.allScriptResults.clear();
		this.file = null;
		this.numberOfErrors = 0;
		this.numberOfPassedTests = 0;
		this.numberOfTimeTraces = 0;
		this.totalTimeOfExecution = 0;
	}
}
