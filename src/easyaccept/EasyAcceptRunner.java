package easyaccept;

import java.io.FileNotFoundException;
import java.io.IOException;

import util.ParsingException;
import util.Variables;
import easyaccept.result.ScriptResultsManager;
import easyaccept.script.Script;
/**
 * The EasyAcceptRunner is responsible for requesting a script's execution. The
 * Script Class accomplishes the execution itself.
 * @author  Win XP
 */
public class EasyAcceptRunner {
	
	private String testFileName;
	private Facade facade;
	private Variables variables;
	
	
	/**
	 * Constructor of an EasyAcceptRunner object.
	 * @param testFileName
	 * 				The file name that will be tasted. 
	 * @param facade
	 * 				facade object that give access to the functionality of the software to be tested. 
	 * @param variables
	 * @param testResult 
	 * 
	 */
	public EasyAcceptRunner(String testFileName, Facade facade, Variables variables) {
		this.testFileName = testFileName;
		this.facade = facade;
		this.variables = variables;
	}
	
	/**
	 * Entry point for EasyAccept package.
	 * <p>
	 * Allows one to run a series of acceptance tests against a facade object
	 * giving access to the functionality of the software to be tested.
	 * 
	 * @param args
	 *            name of the facade class to be instantiated followed by script
	 *            files.
	 * @return statusOK
	 * 			  boolean that represents the execution status.
	 * 			            
	 * @throws ParsingException 
	 * @throws IOException 
	 * @throws EasyAcceptInternalException 
	 * @throws EasyAcceptException 
	 */
	public synchronized ScriptResultsManager runScript() throws FileNotFoundException{
		
		Script script = null;
		try {
			script = new Script(testFileName, facade, variables);
			script.run();
			return script.getResultManager();
		}catch(FileNotFoundException e){
			System.err.println(e.getMessage());
			return null;
		}catch (Exception e){
			return null;
		}
	}
	
}
