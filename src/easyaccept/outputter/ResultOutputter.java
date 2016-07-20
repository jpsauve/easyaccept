package easyaccept.outputter;

import easyaccept.result.ResultsHandler;

/**
 * TODO comment
 * 
 * @author Danilo Queiroz
 * 
 */
public interface ResultOutputter {

	public void setFormatter(ResultFormatter formatter);

	public void printResult(ResultsHandler results);

	public void printSummary(ResultsHandler resultsHandler);
}
