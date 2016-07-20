package easyaccept.outputter;

import easyaccept.result.ScriptResultsManager;

/**
 * TODO comment
 * 
 * @author Danilo Queiroz
 * 
 */
public interface ResultFormatter {

	/**
	 * Formats the results from the given <code>ScriptResultManager</code> to be
	 * printed by the <code>ResultOutputter</code>.
	 * 
	 * @param scriptResults
	 *            The <code>ScriptResultsManager</code> for a given script file.
	 * @return The script's results formatted to be printed.
	 */
	public String format(ScriptResultsManager scriptResults);

	/**
	 * Returns this formatter brief name. It's typically few letters that
	 * determine the format used by this formatter. Eg.: html, xml...
	 * 
	 * @return
	 */
	public String formatterExtension();
}