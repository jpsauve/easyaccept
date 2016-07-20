package easyaccept;

import util.ParsedLine;

/**
 * This interface must be implemented by any EasyAccept facade adapter. It can be a java 
 * application facade adapter or any other facade supported by the EasyAccept.
 * 
 * @author Gustavo Farias
 * @author Magno Jefferson
 * @author Alvaro Magnum
 */
public interface Facade {
	/**
	 * This method executes an operation defined by the script line.
	 * @param parsedLine the script command being executed (and that refers to the internal command)
	 * @param stringDelimiter the string delimiter.
	 * @param int the number of the line of the test file 
	 * @return the result of the execution.
	 * @throws Exception this varies.
	 */
	public Object invoke(ParsedLine parsedLine, char stringDelimiter, int ScriptLineNumber) throws Exception;
	
}