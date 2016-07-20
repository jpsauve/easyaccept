/*
 * Project: topogiggio
 * Script.java 
 *
 * Copyright 2004 Universidade federal da Campina Grande. All rights reserved.
 */

package easyaccept.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import util.LogicalLineReader;
import util.MultiFileEvent;
import util.MultiFileReader;
import util.MultiFileReaderObserver;
import util.ParsedLine;
import util.ParsedLineReader;
import util.ParsingException;
import util.Variables;
import util.VariablesImpl;
import easyaccept.EasyAcceptException;
import easyaccept.EasyAcceptInternalException;
import easyaccept.EasyAcceptSyntax;
import easyaccept.Facade;
import easyaccept.QuitSignalException;
import easyaccept.TimeTraceSignalException;
import easyaccept.result.ErrorEvent;
import easyaccept.result.ErrorObserverImpl;
import easyaccept.result.Result;
import easyaccept.result.ResultImpl;
import easyaccept.result.ScriptResultsManager;

/**
 * The <code>Script</code> class must be instantiated for each test script to be
 * executed. A test script is a sequence of commands present in a test file. A
 * script may consist of two types of commands: internal and external. The
 * internal commands are part of EasyAccept itself. So far, these commands
 * include:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * expect &quot;string&quot; command ... stringdelimiter delimiter
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Each of these internal commands is explained in the corresponding
 * "xxxProcessor" class.
 * <p>
 * The external commands are those available through the facade providing access
 * to the functionality of the software being tested. Basically, any public
 * method available in the facade can be called in the script. The returned
 * value can then be tested (using an internal command such as
 * <code>expect</code>).
 * <p>
 * Once a script is created, it can be executed, either one command at a time or
 * all commands at once. Results can be obtained about the execution of a
 * script's commands.
 * 
 * @author jacques
 */
public class Script implements MultiFileReaderObserver, Runnable {
	private static final String TIMEOUT_PROPERTY = "easyaccept.timeout";
	
	/**
	 * Table to check if a command is an internal command.
	 */
	private Map<String, Command> internalCommands;
	/**
	 * Provides access to the script file, with automatic parsing of the command
	 * line.
	 */
	private ParsedLineReader plr;
	/**
	 * The facade object giving access to the software being tested.
	 */
	private Facade facade;

	/**
	 * The stringDelimiter is needed here so that we can format the command with
	 * the same syntax as the script file when reporting errors.
	 */
	private char stringDelimiter;

	/**
	 * The ScriptResultManager is responsible for to storage info about results
	 * of a test script.
	 */
	private ScriptResultsManager scriptResultsManager;

	private ErrorObserverImpl eaErrorObserver;

	/**
	 * Construct a Script object. This should be done for each script file to be
	 * executed.
	 * 
	 * @param fileName
	 *            The name of the file containing the test script.
	 * @param facade
	 *            The facade object giving access to the functionality of the
	 *            software to be tested
	 * @param variables
	 *            The Map to hold script variables during execution
	 * @throws FileNotFoundException
	 * @throws EasyAcceptException
	 *             if the facade object was not given.
	 * @throws FileNotFoundException
	 *             if the script file cannot be found.
	 * @throws EasyAcceptException
	 * @throws EasyAcceptInternalException
	 */
	public Script(String fileName, Facade facade, Variables variables) throws FileNotFoundException, EasyAcceptException, EasyAcceptInternalException {
		MultiFileReader mfReader = new MultiFileReader();

		plr = new ParsedLineReader(new LogicalLineReader(mfReader, EasyAcceptSyntax.defaultComment, EasyAcceptSyntax.defaultContinuation),
				EasyAcceptSyntax.defaultStringDelimiter, EasyAcceptSyntax.defaultEscapeCharacter, variables);

		setStringDelimiter(EasyAcceptSyntax.defaultStringDelimiter);
		mfReader.addMultiFileReaderObserver(plr);
		mfReader.addMultiFileReaderObserver(this);

		try {
			plr.addFile(fileName);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File not found: " + fileName);
		}
		if (facade == null) {
			throw new EasyAcceptException("Facade can't be null");
		}
		this.facade = facade;
		this.scriptResultsManager = new ScriptResultsManager(fileName);
		initInternalCommands();
	}

	/**
	 * Construct a Script object. This should be done for each script file to be
	 * executed.
	 * 
	 * @param fileName
	 *            The name of the file containing the test script.
	 * @param facade
	 *            The facade object giving access to the functionality of the
	 *            software to be tested
	 * @throws EasyAcceptException
	 *             if the facade object was not given.
	 * @throws FileNotFoundException
	 *             if the script file cannot be found.
	 */
	public Script(String fileName, Facade facade) throws EasyAcceptException, FileNotFoundException, EasyAcceptInternalException {
		this(fileName, facade, new VariablesImpl());
	}

	/**
	 * This method do the request to run a script.
	 */
	public void run() {
		scriptExecutor();
	}

	/**
	 * Initialize hash map to discover internal commands quickly.
	 * 
	 * @throws EasyAcceptInternalException
	 */
	private void initInternalCommands() throws EasyAcceptInternalException {
		internalCommands = new HashMap<String, Command>();
		String command = null;
		String processor = null;
		Class<?> procClass = null;
		Command procObject = null;
		try {
			for (int i = 0; i < EasyAcceptSyntax.internalCommandsArray.length; i++) {
				command = EasyAcceptSyntax.internalCommandsArray[i][0];
				processor = EasyAcceptSyntax.internalCommandsArray[i][1];
				procClass = Class.forName(processor);
				procObject = (Command) procClass.newInstance();
				internalCommands.put(command, procObject);
			}
		} catch (ClassNotFoundException e) {
			throw new EasyAcceptInternalException(e, "The class specified to process command " + command + " was not found.");
		} catch (InstantiationException e) {
			throw new EasyAcceptInternalException(e, "The class specified to process command " + command + " could not be instanciated.");
		} catch (IllegalAccessException e) {
			throw new EasyAcceptInternalException(e, "The class specified to process command " + command + " caused IllegalAccessException.");
		}
	}

	/**
	 * Close a script. After closing, a script can be executed; it will start
	 * again. Old results are thrown out.
	 */
	public void close() throws IOException {
		plr.close();
	}

	/**
	 * Read and execute a single command (in sequence) from the script.
	 * 
	 * @return a {@link ResultImpl}indicating the result of the command's
	 *         execution.
	 * @throws IOException
	 *             if IO errors occur while reading the script.
	 * @throws ParsingException
	 *             if syntax errors are discovered in the script.
	 * @throws EasyAcceptException
	 * @throws EasyAcceptException
	 *             if syntax errors are discovered in the script.
	 */
	public Result getAndExecuteCommand() throws IOException, ParsingException, EasyAcceptException {
		ParsedLine parsedLine = plr.getParsedLine();
		return executeCommand(parsedLine);
	}

	/**
	 * Execute the command givem by the ParsedLine object.
	 * 
	 * @param parsedLine
	 *            The object that represents the command to be executed.
	 * @return The script execution result.
	 * @throws QuitSignalException
	 */
	final Result executeCommand(ParsedLine parsedLine) throws QuitSignalException {
		if (parsedLine != null) {
			Throwable cause = null;
			Object result = null;
			String timeTraceMessage = null;
			assert parsedLine.numberOfParameters() > 0;
			try {
				if (isInternalCommand(parsedLine)) {
					result = executeInternalCommand(parsedLine);
				} else {
					result = execute(parsedLine);
				}
			} catch (InvocationTargetException ex) {
				cause = ex.getCause();
			} catch (TimeTraceSignalException ex) {
				timeTraceMessage = ex.getMessage();
				cause = ex.getCause();
			} catch (QuitSignalException ex) {
				throw ex;
			} catch (EasyAcceptException ex) {
				cause = ex;
			} catch (IllegalAccessException ex) {
				cause = ex;
			} catch (Exception ex) {
				// should only catch the particular exceptions we're interested in
				cause = ex;
			} catch (Throwable ex) {
				cause = ex;
			}

			// handle variables
			String varName = parsedLine.getParameter(0).getName();
			if (varName != null && cause == null) {
				setVariable(varName, result);
			}
			return new ResultImpl(parsedLine.getCommandString(stringDelimiter), result, cause, timeTraceMessage);
		} else {
			return null;
		}
	}

	/**
	 * Set the variable at the ParsedLineReader
	 * 
	 * @param varName
	 *            The name of the variable to be seted.
	 * @param value
	 *            The variable's value to be seted.
	 */
	private void setVariable(String varName, Object value) {
		plr.setVariable(varName, value);
	}

	/**
	 * Execute an internal EasyAccept command by the ParsedLine given.
	 * 
	 * @param parsedLine
	 *            The object where the command will be catch.
	 * @return The result of the command execution.
	 * @throws Exception
	 */
	private Object executeInternalCommand(ParsedLine parsedLine) throws Exception {
		Command command = internalCommands.get(parsedLine.getParameter(0).getValueAsString().toLowerCase());
		return command.execute(this, parsedLine);
	}

	/**
	 * Inform if the ParsedLine given is an internal EasyAccept command.
	 * 
	 * @param parsedLine
	 *            The object that will be verified.
	 * @return Return true if the ParsedLine is an internal command, otherwise
	 *         returns false.
	 */
	private boolean isInternalCommand(ParsedLine parsedLine) {
		return internalCommands.containsKey(parsedLine.getParameter(0).getValueAsString().toLowerCase());
	}

	private Long getTimeout() {
		String timeoutPropety = System.getProperty(TIMEOUT_PROPERTY);
		if (timeoutPropety != null) {
			try {
				return Long.parseLong(timeoutPropety);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	/**
	 * Execute the command given by the ParsedLine to all facade's methods.
	 * 
	 * @param parsedLine
	 *            The object that represents the command.
	 * @return The command result execution.
	 * 
	 * @author Gustavo Farias
	 * @author Magno Jefferson
	 * @author �lvaro Magnum
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AxisFault
	 */
	private Object execute(final ParsedLine parsedLine) throws Throwable {
		assert parsedLine.numberOfParameters() > 0;
		
		Long timeout = getTimeout();
		if (timeout != null) {
			return invokeWithTimeout(parsedLine, timeout);
		} else {
			return facade.invoke(parsedLine, stringDelimiter, plr.getLineNumber());
		}
	}

	private Object invokeWithTimeout(final ParsedLine parsedLine, Long timeout) throws Throwable {
		Callable<Object> callable = new Callable<Object>() {
			public Object call() throws Exception {
				return facade.invoke(parsedLine, stringDelimiter, plr.getLineNumber());
			}
		};

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Object> task = executor.submit(callable);
		try {
			return task.get(timeout, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new EasyAcceptException("EasyAccept timeout: command timed out!");
		} catch (ExecutionException e) {
			throw e.getCause();
		} finally {
			task.cancel(true);
		}
	}

	/**
	 * Obtain the name of the script file being processed.
	 * 
	 * @return the name of the script.
	 */
	public String getFileName() {
		return plr.getCurrentFileName();
	}

	/**
	 * Execute the whole script.
	 * 
	 * @return true if there were no errors; otherwise, returns false.
	 * @throws IOException
	 *             if IO errors occur while reading the script.
	 * @throws ParsingException
	 *             if syntax errors are discovered while parsing the script.
	 * @throws EasyAcceptException
	 *             if syntax errors are discovered while trying to execute
	 *             commands, or the "quit" command was found.
	 * @throws
	 * @throws IOException
	 */
	public boolean executeAndCheck() throws EasyAcceptException, IOException, ParsingException {
		execute();
		return check();
	}

	/**
	 * Obtain the number of errors that occurred during script execution.
	 * 
	 * @return the number of errors.
	 */
	public int numberOfErrors() {
		return scriptResultsManager.getNumberOfErrors();
	}

	/**
	 * Check if there ware errors during the script execution.
	 * 
	 * @return True if there ware no erros. Otherwise, returns false.
	 */
	public boolean check() {
		return numberOfErrors() == 0;
	}

	/**
	 * Execute the command.
	 * 
	 * @throws EasyAcceptException
	 * @throws IOException
	 * @throws ParsingException
	 */
	private void execute() throws EasyAcceptException, IOException, ParsingException {
		Result oneResult;
		eaErrorObserver = ErrorObserverImpl.getInstance();
		long start = System.currentTimeMillis();
		while ((oneResult = getAndExecuteCommand()) != null) {
			// adjust execution time and line
			long now = System.currentTimeMillis();
			oneResult.setExecutionTimeInMilliseconds(now - start);
			oneResult.setLine(plr.getLineNumber());
			// adds to the scriptResultsManager
			this.scriptResultsManager.addResult(oneResult);
			start = now;
			if (oneResult.hasError()) {
				if (eaErrorObserver.hasListeners()) {
					eaErrorObserver.notifyError(new ErrorEvent(oneResult));
				}
			}
		}

	}

	/**
	 * Obtain a formatted string containing all error messages reported during
	 * script execution.
	 * 
	 * @return A formatted string errors messages reported during script
	 *         execution.
	 */
	public String allErrorMessages() {
		StringBuffer answer = new StringBuffer();
		Iterator<Result> it = scriptResultsManager.getResults().values().iterator();
		while (it.hasNext()) {
			Result result = it.next();
			if (result.hasError()) {
				answer.append(result.getErrorMessage());
				answer.append(System.getProperty("line.separator"));
				answer.append("Command producing error: <");
				answer.append(result.getCommand());
				answer.append(">");
				answer.append(System.getProperty("line.separator"));
			}
		}
		return answer.toString();
	}

	/**
	 * Returns the line number in the script file of the last command executed.
	 * 
	 * @return The line number.
	 */
	public int getLineNumber() {
		return plr.getLineNumber();
	}

	/**
	 * Obtain the total number of results (see {@link #getResults}).
	 * 
	 * @return The total number of tests.
	 */
	public int numberOfTests() {
		return scriptResultsManager.getResults().size();
	}

	/**
	 * Define the string delimiter for the script.
	 * 
	 * @param delimiter
	 *            the new string delimiter.
	 * @uml.property name="stringDelimiter"
	 */
	public void setStringDelimiter(char delimiter) {
		stringDelimiter = delimiter;
		plr.setStringDelimiter(delimiter);

	}

	/**
	 * Obtain the current ParsedLineReader object.
	 * 
	 * @return The ParsedLineReader object.
	 */
	public ParsedLineReader getParsedLineReader() {
		return plr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * easyaccept.util.MultiFileReaderListener#afileWasClosed(easyaccept.util
	 * .MultiFileEvent)
	 */
	public void aFileWasClosed(MultiFileEvent event) {
		restoreDefaults();
	}

	/**
	 * Set a default String Delimiter.
	 */
	private void restoreDefaults() {
		stringDelimiter = EasyAcceptSyntax.defaultStringDelimiter;
	}

	/**
	 * Returns the value of the variable names varName. Variable varName is set
	 * when a line like varName=command ... is executed.
	 * 
	 * @param varName
	 *            The name of the variable whose value is sought.
	 * @return The varName variable value.
	 */
	public String getVariableValue(String varName) {
		return plr.getVariableValue(varName);
	}

	/**
	 * Returns the facade
	 * 
	 * @return The facade object.
	 * @uml.property name="facade"
	 */
	public Facade getFacade() {
		return facade;
	}

	/**
	 * Set a facade
	 * 
	 * @param facade
	 * @uml.property name="facade"
	 */
	public void setFacade(Facade facade) {
		this.facade = facade;
	}

	/**
	 * This method run the current script.
	 */
	private void scriptExecutor() {
		try {
			this.executeAndCheck();
			this.close();
		} catch (QuitSignalException e) {
		} catch (EasyAcceptException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * An object that contains the result of each script line.
	 * 
	 * @return ScriptResultManager An object that contains the result of each
	 *         script line.
	 */
	public ScriptResultsManager getResultManager() {
		return this.scriptResultsManager;
	}

}