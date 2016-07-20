package easyaccept.outputter;

import easyaccept.result.ResultsHandler;
import easyaccept.result.ScriptResultsManager;

/**
 * TODO comment
 * 
 * @author Danilo Queiroz
 * 
 */
public class ConsoleResultOutputter implements ResultOutputter {

	private ResultFormatter formatter;

	public ConsoleResultOutputter() {
		// nothing to do
	}

	public ConsoleResultOutputter(ResultFormatter formatter) {
		this.setFormatter(formatter);
	}

	public void setFormatter(ResultFormatter formatter) {
		this.formatter = formatter;
	}
	
	public void printResult(ScriptResultsManager srm) {
		System.out.println(this.formatter.format(srm));
		System.out.println("======================== \n\n");
	}

	public void printResult(ResultsHandler results) {
		if (this.formatter != null) {
			for (ScriptResultsManager srm : results.getAllScriptResultsManager()) {
				printResult(srm);
			}
		}
	}

	public void printSummary(ResultsHandler resultsHandler) {
		System.out.println(resultsHandler.getStatisticResults());
	}
}
