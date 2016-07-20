/*
 * Created on 23/02/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package easyaccept.script;

import util.ParsedLine;
import easyaccept.EasyAcceptException;
import easyaccept.TimeTraceSignalException;
import easyaccept.result.Result;

public class TimeTraceProcessor implements Command {
	/**
	 * Execute the TimeTrace command.
	 */
	public Object execute(Script script, ParsedLine parsedLine)
			throws EasyAcceptException {
		if (parsedLine.numberOfParameters() < 2) {
			throw new EasyAcceptException(script.getFileName(), script
					.getLineNumber(), "Syntax error: timeTrace <command ...>");
		}

		long before = System.currentTimeMillis();
		Result result = script.executeCommand(parsedLine.subLine(1));
		long after = System.currentTimeMillis();
		
		throw new TimeTraceSignalException(script.getFileName(), script.getLineNumber(), (after - before), result.getException());
	}
}