package easyaccept;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.ParsingException;
import util.Variables;
import easyaccept.result.ScriptResultsManager;

/**
 * The <code>EasyAccept</code> class is the entry point for EasyAccept. The main
 * method allows one to execute acceptance tests using scripts that are executed
 * against a facade object giving access to the functionality of the software
 * being tested.
 * <p>
 * Here is an example of how a "topogiggio" software product would be tested.
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 *      java -classpath ... easyaccept.EasyAccept topogiggio.TopogiggioFacade tests/script1.txt tests/script2.txt
 * 
 * </pre>
 * 
 * </blockquote>
 * <p>
 * <p>
 * This assumes that the software to be tested can be accessed through the
 * single object <code>topogiggio.TopogiggioFacade</code> and that acceptance
 * tests are present in the two files <code>script1.txt</code> and
 * <code>scripts.txt</code>.
 * <p>
 * An exit code of 0 implies that all tests have passed.
 * 
 * @author Jacques
 */
public class EasyAccept {
	/**
	 * Entry point for EasyAccept package.
	 * <p>
	 * Allows one to run a series of acceptance tests against a facade object
	 * giving access to the functionality of the software to be tested.
	 * 
	 * @param args
	 *            name of the facade class to be instantiated followed by script
	 *            files
	 */
	public static void main(String[] args) {
		int statusCode = 0;
		try {
			executeEasyAccept(args);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			statusCode = 1;
		} finally {
			System.exit(statusCode);
		}
	}

	/**
	 * The executeEasyAccept method allows the java application to be tested
	 * using test scripts that are executed against a java facade object, giving
	 * access to the functionality of the software being tested.
	 * 
	 * @param args
	 *            name of the facade class to be instantiated followed by script
	 *            files
	 * @throws EasyAcceptException 
	 */
	public static void executeEasyAccept(String[] args) throws EasyAcceptException {
		if (args.length < 2) {
			throw new EasyAcceptSyntaxException();
		}

		String facadeName = args[0];
		List<String> filesList = new ArrayList<String>();
		for (int i = 1; i < args.length; i++) {
			filesList.add(args[i]);
		}
		executeEasyAccept(facadeName, filesList);
	}

	/**
	 * The executeEasyAccept method allows the java application to be tested
	 * using test scripts that are executed against a java facade object, giving
	 * access to the functionality of the software being tested.
	 * 
	 * @param facadeName
	 *            the test facade name.
	 * @param filesList
	 *            the list of files to be executed.
	 */
	public static void executeEasyAccept(String facadeName, List<String> filesList) throws EasyAcceptException {
		EasyAcceptFacade eaFacade = null;
		try {
			eaFacade = executeEasyAcceptTests(facadeName, filesList);
		} catch (ClassNotFoundException e1) {
			throw new EasyAcceptException("Facade not found: " + facadeName, e1);
		} catch (Exception e) {
			throw new EasyAcceptException("Unexpected exception while running tests: " + e.getMessage(), e);
		}
		if (eaFacade != null && eaFacade.getTotalNumberOfNotPassedTests() > 0) {
			throw new EasyAcceptException("Acceptance tests failed!");
		}
	}

	/**
	 * The executeEasyAccept method allows the java application to be tested
	 * using test scripts that are executed against a java facade object, giving
	 * access to the functionality of the software being tested.
	 * 
	 * @param facadeName
	 *            the test facade name.
	 * @param filesList
	 *            the list of files to be executed.
	 * @return
	 */
	public static EasyAcceptFacade executeEasyAcceptTests(String facadeName, List<String> filesList) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		EasyAcceptFacade eaFacade = new EasyAcceptFacade(Class.forName(facadeName).newInstance(), filesList);
		eaFacade.executeTests();
		return eaFacade;
	}

	/**
	 * Runs acceptance tests taken from a script file.
	 * 
	 * @param facade
	 *            The facade object to be used to obtain the test results.
	 * @param testFileName
	 *            The file that contains the acceptance tests.
	 * @param variables
	 * 
	 * @return true if all tests have been passed; otherwise returns false.
	 * @throws IOException
	 *             if problems occur when reading the script file.
	 * @throws FileNotFoundException
	 *             if the script file cannot be found.
	 */
	public ScriptResultsManager runAcceptanceTest(Facade facade, String testFileName, Variables variables) throws IOException, FileNotFoundException,
			QuitSignalException, ParsingException {
		EasyAcceptRunner runner = new EasyAcceptRunner(testFileName, facade, variables);
		return runner.runScript();
	}
}
// TODO provide a means of choosing message formatting (como log4J)
// TODO when EA calls a method in the Facade and an exception is thrown (and EA
// didn't
// expect it), nothing is said about WHERE such exception occurred .. For
// example:
//
// Command: <rollDice firstDieResult="1"(java.lang.Byte)
// secondDieResult="1"(java.lang.Byte)>, produced error: <This place
// can't be sold>
//
// Would be nice to show the line of Java code and, if possible, the
// line of acceptance test code
// TODO pass collections of strings as parameters
// ex. command param={ abc, "def, ghi", "{"}
// TODO javadoc
// TODO command returning collection
// In the current implementation, a command returns a simple string. When a
// collection is returned, the expected string
// should have the syntax {"s1","s2",...}
// where s1 is the string representing the first object in the collection, etc.
// EasyAccept must take care to produce a string with this syntax before testing
// with the expected string whenever a business logic command returns a
// collection.
// example:
// expect {"John Doe","Mary Stuart"} getUserNames age=20
// In this case, the getUserNames command returns a collection.
// TODO treatment of collection can be better by extracting an attribute from
// each object in a returned collection
// example:
// expect {"John Doe","Mary Stuart"} whenExtractingAttribute attribute=name
// getStudents class="abc"
// TODO print command:
// a print command that simply executes a command and prints the result returned
// TODO make test management easier
// (test definition, packaging, execution, traceability, creation, etc. etc.)
// this user story must be expanded
// TODO command to stop on first error to avoid long list of errors. Or maybe a
// maxErrorToReport

