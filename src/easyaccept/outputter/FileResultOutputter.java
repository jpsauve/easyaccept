package easyaccept.outputter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import easyaccept.result.ResultsHandler;
import easyaccept.result.ScriptResultsManager;

/**
 * TODO comment/test
 * 
 * @author Danilo Queiroz
 * 
 */
public class FileResultOutputter implements ResultOutputter {

	private static final String FILE_PREFIX = "TEST-";

	private ResultFormatter formatter;
	private String directory;

	public FileResultOutputter() {
		// Nothing to do
	}

	public FileResultOutputter(String directory) {
		this.setOutputDirectory(directory);
	}

	public void setOutputDirectory(String directory) {
		if ((new File(directory)).isDirectory()) {
			this.directory = directory;
		}
	}

	public void setFormatter(ResultFormatter formatter) {
		this.formatter = formatter;
	}

	public void printResult(ResultsHandler results) {
		for (ScriptResultsManager srm : results.getAllScriptResultsManager()) {
			OutputStreamWriter writer = null;
			try {
				writer = getStream(srm);
				writer.write(formatter.format(srm));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeStream(writer);
			}
		}
	}

	public void printSummary(ResultsHandler resultsHandler) {
		// TODO
	}

	private void closeStream(OutputStreamWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private OutputStreamWriter getStream(ScriptResultsManager srm) throws FileNotFoundException, UnsupportedEncodingException {
		String name = getScriptFileName(srm.getFile());
		String filename = FILE_PREFIX + name + "." + this.formatter.formatterExtension();
		File file;
		if (directory != null) {
			file = new File(directory, filename);
		} else {
			file = new File(filename);
		}

		return new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, false)), "UTF-8");
	}

	/**
	 * @param srm
	 * @return
	 */
	private String getScriptFileName(String fullname) {
		File scriptFile = new File(fullname);
		String name = scriptFile.getName();
		int idx = name.lastIndexOf(".");
		name = name.substring(0, idx);
		return name;
	}
}