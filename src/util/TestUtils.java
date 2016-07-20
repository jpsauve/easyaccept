package util;

import easyaccept.Facade;
import easyaccept.JavaApplicationFacadeAdapter;
import easyaccept.script.Script;
import easyaccept.script.test.TestFacade;

/**
 * This class must contains auxiliar methods for EasyAccept test executions.
 * @author Gustavo Pereira
 */
public class TestUtils {

	/**
	 * This method creates a script with a java aplication facade.
	 * @throws Exception
	 * @author Gustavo Pereira
	 */
	public static Script createJavaAppScript(String script, Object facade) throws Exception{
		
		return new Script(script, new JavaApplicationFacadeAdapter( facade));
	}

	/**
	 * This method creates a script with a java aplication facade.
	 * @throws Exception
	 * @author Gustavo Pereira
	 */
	public static Script createJavaAppScript(String script, TestFacade facade, Variables variables) throws Exception {
		return new Script(script, new JavaApplicationFacadeAdapter( facade), variables);
	}
		
}
