package easyaccept.script;

import junit.framework.TestCase;
import util.TestUtils;
import util.Variables;
import util.VariablesImpl;
import easyaccept.result.Result;
import easyaccept.script.test.TestFacade;

/**
 * Provide the variable substitution tests.
 * @author Jacques
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestVariableSubstitution extends TestCase {
	
	/**
	 * Execute the variable substitution 1.
	 * @throws Exception
	 */
	public void testVariableSubstitution1() throws Exception {
		Script script = TestUtils.createJavaAppScript("src/easyaccept/script/test/script13.txt",
				new TestFacade());
		Result result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("a=commandReturningString param1=hello", result.getCommand());
		assertFalse(result.hasError());
		assertEquals("HELLO", script.getVariableValue("a"));

		result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect HELLO commandReturningString param1=hello", result
				.getCommand());
		assertFalse(result.hasError());
		script.close();

	}
	/**
	 * Execute the variable substitution 2.
	 * @throws Exception
	 */
	public void testVariableSubstitution2() throws Exception {
		Script script = TestUtils.createJavaAppScript("src/easyaccept/script/test/script14.txt",
				new TestFacade());

		Result result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect HELLO b=commandReturningString param1=hello", result
				.getCommand());
		assertFalse(result.hasError());

		result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect HELLO commandReturningString param1=hello", result
				.getCommand());
		assertFalse(result.hasError());
		script.close();

	}
	/**
	 * Execute the variable substitution 3.
	 * @throws Exception
	 */
	public void testVariableSubstitution3() throws Exception {
		Script script = TestUtils.createJavaAppScript("src/easyaccept/script/test/script15.txt",
				new TestFacade());

		Result result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("d=expect HELLO c=commandReturningString param1=hello", result
				.getCommand());
		assertFalse(result.hasError());

		result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect HELLO commandReturningString param1=hello", result
				.getCommand());
		assertFalse(result.hasError());

		result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect \"Result is OK\" echo Result is OK",
				result.getCommand());
		assertFalse(result.hasError());
		script.close();
	}
	/**
	 * Execute the variable substitution 4.
	 * @throws Exception 
	 * @throws Exception
	 */
	public void testVariableSubstitution4() throws Exception {
		Script script = TestUtils.createJavaAppScript("src/easyaccept/script/test/script16.txt",
				new TestFacade());

		Result result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("a=commandReturningInt param1=46", result.getCommand());
		assertFalse(result.hasError());

		result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect 47 echo 47", result.getCommand());
		assertFalse(result.hasError());
		script.close();
	}
	/**
	 * Execute the variable substitution 5.
	 * @throws Exception 
	 * @throws Exception 
	 * @throws Exception
	 */
	public void testVariableSubstitution5() throws Exception {

		Script script = TestUtils.createJavaAppScript("src/easyaccept/script/test/script17.txt",
					new TestFacade());

		Result result = script.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect a=b command1", result.getCommand());
		assertFalse(result.hasError());
		script.close();
	}
	/**
	 * Execute the variable substitution in two scripts.
	 * @throws Exception 
	 */
	public void testVariableSubstitutionInTwoScripts() throws Exception {
		Variables variables = new VariablesImpl();
		Script script1 = TestUtils.createJavaAppScript("src/easyaccept/script/test/script19.txt",
				new TestFacade(), variables);
		Result result = script1.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("a=commandReturningString param1=hello", result.getCommand());
		assertFalse(result.hasError());
		assertEquals("HELLO", script1.getVariableValue("a"));
		script1.close();

		Script script2 = TestUtils.createJavaAppScript("src/easyaccept/script/test/script20.txt",
				new TestFacade(), variables);
		result = script2.getAndExecuteCommand();
		assertNotNull(result);
		assertEquals("expect HELLO commandReturningString param1=hello", result
				.getCommand());
		assertFalse(result.hasError());
		script2.close();
	}

}
