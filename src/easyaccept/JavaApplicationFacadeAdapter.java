package easyaccept;

import java.lang.reflect.Method;

import util.MethodMatcher;
import util.ParsedLine;

/**
 * This class represents the java application facade adapter. It allows the EasyAccept to acces and test
 * a java application.
 * 
 * @author Magno Jefferson
 * @author Alvaro Magnum
 * @author Gustavo Farias
 */
public class JavaApplicationFacadeAdapter implements Facade {
	
	private Object facade;
	
	/**
	 * The JavaApplicationFacadeAdapter constructor.
	 * @param facade facade object that allows the access to the java application.
	 */
	public JavaApplicationFacadeAdapter(Object facade) {
		this.facade = facade;
	}
	
	/**
	 * The invoke method is responsible to call the method invocation. The method to be
	 * invoked and its arguments are passed by the parsedLine parameter.
	 * 
	 * @param parsedLine
	 * 					The object that obtains the method to be invoked and its arguments.
	 * @param stringDelimiter
	 * 					The string delimiter.
	 * 
	 * @rerturn Object An object containing the results of the invoked test.
	 */
	public Object invoke(ParsedLine parsedLine, char stringDelimiter, int scriptLineNumber) throws Exception {
		Method[] methods = facade.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (MethodMatcher.methodMatch(methods[i], parsedLine)) {
				return methods[i].invoke(facade, parsedLine.getArgsValues());
			}
		}
		throw new EasyAcceptException("Line "+scriptLineNumber+": Unknown command: "
				+ parsedLine.getCommandString(stringDelimiter));
	}

	
}
