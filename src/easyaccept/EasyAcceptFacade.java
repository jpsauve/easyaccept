package easyaccept;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import util.ParsingException;
import util.Variables;
import util.VariablesImpl;
import easyaccept.outputter.ConsoleResultOutputter;
import easyaccept.outputter.ResultOutputter;
import easyaccept.outputter.TextResultFormatter;
import easyaccept.result.ErrorListener;
import easyaccept.result.ErrorObserverImpl;
import easyaccept.result.Result;
import easyaccept.result.ResultsHandler;
import easyaccept.result.ScriptResultsManager;

/**
 * This is the EasyAccept facade. It enables its users to request tests
 * execution and to get the tests results. The Facade makes available many
 * information about the executed tests.
 * 
 * @author Gustavo Farias
 * @author Magno Jefferson
 * @author Alvaro Magnum
 */
public class EasyAcceptFacade {

	private Facade facadeAdapter;
	private ErrorObserverImpl eaErrorObserver;
	private ResultsHandler resultsHandler;
	private List<String> files;

	/**
	 * EasyAccept Facade constructor.
	 * 
	 * @param facade
	 *            the Facade of the application to be tested.
	 * @param files
	 *            a list of test files.
	 */
	public EasyAcceptFacade(Object facade, List<String> files) {
		this.facadeAdapter = new JavaApplicationFacadeAdapter(facade);
		resultsHandler = new ResultsHandler();
		this.files = files;
	}

	/**
	 * Runs acceptance tests taken from a script file.
	 * 
	 * @param facade
	 *            the facade object, used to execute application commands and to
	 *            obtain its results.
	 * @param testFileName
	 *            file that contains the acceptance tests.
	 * @param variables
	 *            the current variables.
	 * 
	 * @throws IOException
	 *             if problems occur when reading the script file.
	 * @throws FileNotFoundException
	 *             if the script file can not be found.
	 * 
	 * @return true if all tests have been passed; otherwise returns false.
	 */
	private boolean runAcceptanceTest(Facade facade, String file, Variables variables) throws IOException, FileNotFoundException,
			QuitSignalException, ParsingException {
		ConsoleResultOutputter out = new ConsoleResultOutputter(new TextResultFormatter());
		EasyAcceptRunner runner = new EasyAcceptRunner(file, facade, variables);
		ScriptResultsManager scriptResultsManager = runner.runScript();
		out.printResult(scriptResultsManager);
		resultsHandler.addResult(scriptResultsManager);
		return true;
	}

	/**
	 * Executes the acceptance test scripts.
	 */
	public void executeTests() {
		try {
			VariablesImpl variablesImpl = new VariablesImpl();
			for (int i = 0; i < files.size(); i++) {
				runAcceptanceTest(this.facadeAdapter, files.get(i), variablesImpl);
			}
			ConsoleResultOutputter out = new ConsoleResultOutputter(new TextResultFormatter());
			out.printSummary(resultsHandler);
		} catch (Exception e) {
		}
	}

	/**
	 * Gets the <code>ResultHandler</code>.
	 * 
	 * @return The ResultHandler.
	 */
	public ResultsHandler getResultHandler() {
		return this.resultsHandler;
	}

	/**
	 * This method returns a map with all test results of the specified test
	 * script. null is returned if there is no test file with the specified
	 * name. Each script line result is a Result object.
	 * 
	 * @param file
	 *            the name of the test script.
	 * 
	 * @return A map with all the test results of the specified test script or
	 *         null if there is no test file with the specified name.
	 */
	public Map<Integer, Result> getScriptResults(String file) {
		return resultsHandler.getScriptResults(file);
	}

	/**
	 * This method returns the test result of a specified script line or null if
	 * there is no test file with the specified name. The test script line
	 * result is a Result object.
	 * 
	 * @param file
	 *            name of the test file.
	 * @param line
	 *            the test script line which the acceptance test is located.
	 * 
	 * @return The test result of the specified script line or null if the test
	 *         script has not the specified line.
	 */
	public Result getLineResult(String file, int line) {
		return this.resultsHandler.getLineResult(file, line);
	}

	/**
	 * Returns the number of passed tests of a specified test script.
	 * 
	 * @param file
	 *            the name of the file that contains the executed acceptance
	 *            tests.
	 * 
	 * @return The number of passed tests or -1 if there is no test file with
	 *         the specified name.
	 */
	public int getScriptNumberOfPassedTests(String file) {
		return resultsHandler.getScriptNumberOfPassedTests(file);
	}

	/**
	 * Returns the total number of passed tests.
	 * 
	 * @return The number of passed tests of all executed test scripts.
	 */
	public int getTotalNumberOfPassedTests() {
		return resultsHandler.getTotalNumberOfPassedTests();
	}

	/**
	 * Returns the number of not passed tests of a specified test script.
	 * 
	 * @param file
	 *            the name of the file that contains the executed acceptance
	 *            tests.
	 * 
	 * @return The number of not passed tests or -1 if there is no test file
	 *         with the specified test script name.
	 */
	public int getScriptNumberOfNotPassedTests(String file) {
		return resultsHandler.getScriptNumberOfNotPassedTests(file);
	}

	/**
	 * Returns the total number of not passed tests.
	 * 
	 * @return The number of not passed tests of all executed test scripts.
	 */
	public int getTotalNumberOfNotPassedTests() {
		return resultsHandler.getTotalNumberOfNotPassedTests();
	}

	/**
	 * Returns the total number of tests.
	 * 
	 * @return The total number of tests.
	 */
	public int getTotalNumberOfTests() {
		return resultsHandler.getTotalNumberOfTests();
	}

	/**
	 * Returns the total number of tests of a specified test script.
	 * 
	 * @return The total number of tests of the specified test script or -1 if
	 *         there is no test file with the specified test script name.
	 */
	public int getScriptTotalNumberOfTests(String file) {
		return resultsHandler.getScriptTotalNumberOfTests(file);
	}

	/**
	 * This method adds an application as a test failure listener.
	 * 
	 * @param listener
	 *            the test execution listener.
	 */
	public void addFailureListener(ErrorListener listener) {
		eaErrorObserver = ErrorObserverImpl.getInstance();
		eaErrorObserver.addListener(listener);
	}

	/**
	 * This method returns the complete results of a specified test file.
	 * Complete result contains: Each tested script's name. Passed tests. Not
	 * passed tests. Failures (if they occur). Failures lines. System Messages.
	 * 
	 * @param file
	 *            the name of the acceptance test script.
	 * 
	 * @return The complete results of the specified test file or null if there
	 *         is no test file with the specified name.
	 */
	public String getScriptCompleteResults(String file) {
		return resultsHandler.getScriptCompleteResults(file);
	}

	/**
	 * This method returns a summarized result of a specified test script.
	 * Summarized result contains: Each tested script's name. Number of passed
	 * tests. Number of not passed tests.
	 * 
	 * @param file
	 *            the name of the file that contains the executed acceptance
	 *            tests.
	 * 
	 * @return A summarized result of specified test script execution or null if
	 *         there is no script with the specified name.
	 */
	public String getScriptSummarizedResults(String file) {
		return this.resultsHandler.getScriptSummarizedResults(file);
	}

	/**
	 * This method returns a summarized result of all executed tests. Summarized
	 * result contains: Each tested script's name. Number of passed tests.
	 * Number of not passed tests.
	 * 
	 * @return A summarized result of all executed tests.
	 */
	public String getSummarizedResults() {
		return this.resultsHandler.getSummarizedResults();
	}

	/**
	 * This method returns the complete results of all executed tests. Complete
	 * result contains: Each tested script's name. Passed tests. Not passed
	 * tests. Failures (if they occur). Failures lines. System Messages.
	 * 
	 * @return The complete results of all executed tests.
	 */
	public String getCompleteResults() {
		return resultsHandler.getCompleteResults();
	}

	/**
	 * Returns the errors of the specified test script file (if they occur).
	 * 
	 * @param file
	 *            the name of the file that contains the executed acceptance
	 *            tests.
	 * @return The errors of the executed test script (if they occur). The empty
	 *         string "" is returned if no errors have been occurred. null is
	 *         returned if there is no test file with the specified name.
	 */
	public String getScriptFailures(String file) {
		return resultsHandler.getScriptFailures(file);
	}

}
