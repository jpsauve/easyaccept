package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Gustavo Farias
 * @author Magno Jefferson
 * @author Álvaro Magnum
 */
public class JavaFileUtil {

	public static String getFileContent(String file) throws IOException {
		String resultado = new String();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		while (line != null) {
			line += "\n";
			resultado += line;
			line = in.readLine();
		}
		return resultado;
	}

}
