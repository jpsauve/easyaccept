package easyaccept;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;
import easyaccept.result.ErrorEvent;
import easyaccept.result.ErrorListener;
import easyaccept.script.test.TestFacade;

/**
 * This class will test all methods of Easy Accept API( {@link EasyAcceptFacade} ).
 * @author Gustavo Farias
 * @author Magno Jefferson
 */
public class EasyAcceptFacadeTest extends TestCase {

	private static String SEPARATOR = System.getProperty("file.separator");
	
	private static String TEST_FILES_PATH = "src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+SEPARATOR+"test"+SEPARATOR;
	
	ArrayList<String> testes;
	
	
	
	protected void setUp() throws Exception {
		testes = new ArrayList<String>();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		testes.clear();
		super.tearDown();
	}

	public void testNumberOfTests() {
		
		String file1 = TEST_FILES_PATH + "script1.txt";
		String file2 = TEST_FILES_PATH + "script2.txt";
		
		Object facade = new TestFacade();
		testes.add(file1);
		testes.add(file2);
		EasyAcceptFacade easyAcceptFacade = new EasyAcceptFacade(facade, testes);
		
		easyAcceptFacade.executeTests();
		
		assertEquals(0, easyAcceptFacade.getScriptNumberOfPassedTests(file1));
		assertEquals(0, easyAcceptFacade.getScriptNumberOfNotPassedTests(file1));
		assertEquals(0, easyAcceptFacade.getScriptTotalNumberOfTests(file1));
		
		assertEquals(1, easyAcceptFacade.getScriptNumberOfPassedTests(file2));
		assertEquals(0, easyAcceptFacade.getScriptNumberOfNotPassedTests(file2));
		assertEquals(1, easyAcceptFacade.getScriptTotalNumberOfTests(file2));
		
		assertEquals(1, easyAcceptFacade.getTotalNumberOfTests());
		
	}
	
	public void testGetResumedResult() throws IOException {
		
		File expectedFile = new File("src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+
							SEPARATOR+"test"+SEPARATOR+"easyacceptfacade_test"+SEPARATOR+"exp001.txt");
		
		String file1 = TEST_FILES_PATH + "script1.txt";
		String file2 = TEST_FILES_PATH + "script5.txt";
		String file3 = TEST_FILES_PATH + "script6.txt";
		Object facade = new TestFacade();
		testes.add(file1);
		testes.add(file2);
		testes.add(file3);
		EasyAcceptFacade easyAcceptFacade = new EasyAcceptFacade(facade, testes);
		
		easyAcceptFacade.executeTests();
		
		assertEquals("Test file: src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+SEPARATOR+"test"+SEPARATOR+
								"script1.txt | Passed Tests: 0 | Not Passed Tests: 0"+"\n\n", easyAcceptFacade.getScriptSummarizedResults(file1));

		
		assertEquals("Test file: src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+SEPARATOR+"test"+SEPARATOR+
				"script5.txt | Passed Tests: 2 | Not Passed Tests: 0"+"\n\n", easyAcceptFacade.getScriptSummarizedResults(file2));
		
		assertEquals("Test file: src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+SEPARATOR+"test"+SEPARATOR+
				"script6.txt | Passed Tests: 0 | Not Passed Tests: 2"+"\n\n", easyAcceptFacade.getScriptSummarizedResults(file3));
		
		String out = easyAcceptFacade.getSummarizedResults();
		
		assertEquals( getFileContent( expectedFile.getAbsolutePath() ).trim(), out.trim() );
		
	}
	
	public void testReceiveErrorNotification() throws IOException {
		
		File expectedFile = new File("src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+
				SEPARATOR+"test"+SEPARATOR+"easyacceptfacade_test"+SEPARATOR+"exp008.txt");

		String file = TEST_FILES_PATH + "script8.txt";
		
		ErrorListenerApplication errorListApp = new ErrorListenerApplication();
		
		Object facade = new TestFacade();

		testes.add(file);
		EasyAcceptFacade easyAcceptFacade = new EasyAcceptFacade(facade, testes);
		
		easyAcceptFacade.addFailureListener(errorListApp);
		
		easyAcceptFacade.executeTests();
		
		assertEquals( getFileContent(expectedFile.getAbsolutePath()).trim(),errorListApp.getErrorMessage().trim() );
		
	}
	
	public void testGetLineResult() throws IOException {
		
		String file = TEST_FILES_PATH + "script11.txt";
		
		Object facade = new TestFacade();

		testes.add(file);
		EasyAcceptFacade easyAcceptFacade = new EasyAcceptFacade(facade, testes);
		
		easyAcceptFacade.executeTests();
		
		assertEquals("commandManyParametersNum param1=123 param2=456", easyAcceptFacade.getLineResult(file, 2).getCommand());
		assertEquals("OK", easyAcceptFacade.getLineResult(file, 3).getResultAsString());
		assertEquals("(no exception)", easyAcceptFacade.getLineResult(file, 4).getErrorMessage());
		assertEquals(null, easyAcceptFacade.getLineResult(file, 4).getException());
	
	}
	
	public void testGetErros(){
		
		String file = TEST_FILES_PATH + "script4.txt";
		
		File expectedFile = new File("src"+SEPARATOR+"easyaccept"+SEPARATOR+"script"+
				SEPARATOR+"test"+SEPARATOR+"easyacceptfacade_test"+SEPARATOR+"exp004.txt");

		
		Object facade = new TestFacade();

		testes.add(file);
		EasyAcceptFacade easyAcceptFacade = new EasyAcceptFacade(facade, testes);
		
		easyAcceptFacade.executeTests();
		
		assertEquals(getFileContent(expectedFile.getAbsolutePath()), easyAcceptFacade.getScriptFailures(file));
		
		
	}
	
	public void testInexistentFiles(){

		Object facade = new TestFacade();
		String file3 = TEST_FILES_PATH + "script3.txt";
		testes.add(file3);
	
		EasyAcceptFacade easyAcceptFacade = new EasyAcceptFacade(facade, testes);

		easyAcceptFacade.executeTests();

		String inexistentFile = "script.txt";

		assertEquals(null, easyAcceptFacade.getScriptSummarizedResults(inexistentFile));
		assertEquals(null, easyAcceptFacade.getScriptCompleteResults(inexistentFile));
		assertEquals(-1, easyAcceptFacade.getScriptNumberOfNotPassedTests(inexistentFile));
		assertEquals(-1, easyAcceptFacade.getScriptNumberOfPassedTests(inexistentFile));
		assertEquals(-1, easyAcceptFacade.getScriptTotalNumberOfTests(inexistentFile));
		assertEquals(null, easyAcceptFacade.getLineResult(file3, 100));
		assertEquals(null, easyAcceptFacade.getScriptResults(inexistentFile));
		assertEquals(null, easyAcceptFacade.getScriptFailures(inexistentFile));
		
	}
	
	
	/**
	 * This method returns a String representation of a file content.
	 */
	private String getFileContent(String arq) {
		String out = new String();
		try {
			BufferedReader in = new BufferedReader(new FileReader(arq));
			String line = in.readLine();
			while ( line != null ) {
				line += "\n";
				out += line;
				line = in.readLine();
			}			
		} catch (FileNotFoundException e) {
			fail("File not Found: " + arq);
			
		} catch (IOException e) {
			fail("I/O Error: " + arq);
		}		
		return out;
	}
	
	/**
	 * This class will be used by tests to receive error notifications.
	 * @author Gustavo Farias
	 */
	class ErrorListenerApplication implements ErrorListener {

		private String errorMessage;

		public void receiveTestErrorsNotifications(ErrorEvent event) {
			this.setErrorMessage(event.getErrorMessage());			
		}

		private void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;			
		}
		
		public String getErrorMessage(){
			return this.errorMessage;
		}

	}

}
