package easyaccept.outputter;

import java.util.Properties;

import easyaccept.result.Result;
import easyaccept.result.ScriptResultsManager;

/**
 * TODO comment/test
 * 
 * @author Danilo Queiroz
 * 
 */
public class XMLResultFormatter implements ResultFormatter {

	public String formatterExtension() {
		return "xml";
	}

	public String format(ScriptResultsManager scriptResults) {
		StringBuilder buf = new StringBuilder();
		head(buf, scriptResults);
		properties(buf);
		testCases(buf, scriptResults);
		tail(buf);
		return buf.toString();
	}

	private void testCases(StringBuilder buf, ScriptResultsManager scriptResults) {
		for (int line : scriptResults.getResults().keySet()) {
			Result result = scriptResults.getLineResult(line);
			buf.append("  <testcase classname=\"");
			buf.append(scriptResults.getFile());
			buf.append("\" name=\"");
			buf.append(result.getCommand());
			buf.append("\" time=\"");
			buf.append((double) result.getExecutionTimeInMilliseconds() / 1000);

			if (!result.hasError()) {
				buf.append("\" />\n");
			} else {
				buf.append("\" >\n");
				buf.append("    <failure message=\"");
				buf.append(result.getErrorMessage());
				buf.append("\" type=\"");
				buf.append(result.getException().getClass());
				buf.append("\">\n");
				buf.append(result.getException().getClass());
				buf.append(": ");
				buf.append(result.getErrorMessage());
				buf.append("\n    </failure>\n");
				buf.append("  </testcase>\n");
			}
		}
	}

	private void head(StringBuilder buf, ScriptResultsManager script) {
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		buf.append("<testsuite errors=\"0\" ");
		buf.append("failures=\"").append(script.getNumberOfErrors()).append("\" ");
		buf.append("name=\"").append(script.getFile()).append("\" ");
		buf.append("tests=\"").append(script.getNumOfTests()).append("\" ");
		buf.append("time=\"").append((double) script.getTotalTimeOfExecutionInMilliseconds() / 1000);
		buf.append("\" >\n");
	}

	private void properties(StringBuilder buf) {
		buf.append("  <properties>\n");
		Properties props = System.getProperties();
		for (String prop : props.stringPropertyNames()) {
			buf.append("    <property name=\"");
			buf.append(prop);
			buf.append(" value=\"");
			buf.append(props.getProperty(prop));
			buf.append("\"/>\n");
		}
		buf.append("  </properties>\n");
	}

	private void tail(StringBuilder buf) {
		buf.append("  <system-out><![CDATA[]]></system-out>\n");
		buf.append("  <system-err><![CDATA[]]></system-err>\n");
		buf.append("</testsuite>\n");

	}
}
