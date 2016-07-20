package easyaccept.outputter;

import easyaccept.result.Result;
import easyaccept.result.ScriptResultsManager;

/**
 * TODO comment/test
 * 
 * @author Danilo Queiroz
 * 
 */
public class TextResultFormatter implements ResultFormatter {

	public String formatterExtension() {
		return "txt";
	}

	public String format(ScriptResultsManager scriptResults) {
		StringBuilder buf = new StringBuilder();
		
		String file = scriptResults.getFile();

		buf.append("Test file: ");
		buf.append(file);
		buf.append(" | Passed Tests: ");
		buf.append(scriptResults.getNumberOfPassedTests());
		buf.append(" | Not Passed Tests: ");
		buf.append(scriptResults.getNumberOfErrors());
		buf.append(" | Total execution time (ms): ");
		buf.append(scriptResults.getTotalTimeOfExecutionInMilliseconds());
		buf.append("\n\n");

		if (scriptResults.getNumberOfErrors() != 0) {
			buf.append("FAILURES:\n");
			for (Result oneResult : scriptResults.getResults().values()) {
				if (oneResult.hasError()) {
					String failureOriginal = oneResult.getErrorMessage();

					// The original failure string is at EasyAcceptException
					// class
					buf.append("\tAt line ");
					buf.append(oneResult.getLine());
					buf.append(':');
					buf.append(failureOriginal);
					buf.append('\n');
				}
			}
		}

		if (scriptResults.getNumberOfTimeTraces() != 0) {
			buf.append("TIME-TRACES:\n");
			for (Result oneResult : scriptResults.getResults().values()) {
				if (oneResult.hasTimeTraceMessage()) {
					buf.append("\tAt line ");
					buf.append(oneResult.getLine());
					buf.append(':');
					buf.append(oneResult.getTimeTraceMessage());
					buf.append('\n');
				}
			}
		}

		return buf.toString();
	}

//	private Object getCustomizedErrorMessage(String failureOriginal) {
//		int indice = failureOriginal.indexOf(":");
//		return failureOriginal.substring(indice + 1, failureOriginal.length());
//	}
}
