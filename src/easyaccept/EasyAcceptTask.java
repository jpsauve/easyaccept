package easyaccept;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.ClasspathUtils.Delegate;

import util.Variables;
import util.VariablesImpl;
import easyaccept.outputter.ConsoleResultOutputter;
import easyaccept.outputter.FileResultOutputter;
import easyaccept.outputter.ResultOutputter;
import easyaccept.outputter.TextResultFormatter;
import easyaccept.outputter.XMLResultFormatter;
import easyaccept.result.ResultsHandler;
import easyaccept.result.ScriptResultsManager;

/**
 * This class represents an Ant task that executes EasyAccept.
 * 
 * @author Guilherme Mauro Germoglio - germoglio@gmail.com
 * @author Danilo Penna Queiroz - dpenna.queiroz@gmail.com
 */
public class EasyAcceptTask extends Task {

	/**
	 * Property value to be set when there's a failure during tests and
	 * 'failureproperty' attribute is set.
	 */
	private static final String TRUE_VALUE = "true";

	/**
	 * Message shown when no facade attribute was specified.
	 */
	private static final String NO_FACADE_SPECIFIED_MSG = "No Facade specified.";

	/**
	 * Message shown when the todir attribute don't refers to a directory.
	 */
	private static final String NOT_A_DIR_MSG = "Invalid value to \"todir\" attribute.";

	/**
	 * Message shown when a non valid formatter is specified.
	 */
	private static final String INVALID_FORMATTER_MSG = "Invalid value to \"formatter\" attribute.";

	/**
	 * Message shown when the facade attribute was specified but the class was
	 * not found.
	 */
	private static final String THE_FACADE_CLASS_WAS_NOT_FOUND_MSG = "The Facade class was not found.";
	/**
	 * Message shown when no files are found.
	 */
	private static final String NO_FILES_FOUND_MSG = "No files found.";

	/**
	 * Attribute to make the build fail when there's any error during the
	 * execution. The default value is true. It is not required.
	 */
	private boolean failonerror = true;

	/**
	 * Attribute that holds the facade class to be used. It is required.
	 */
	private String facade;

	/**
	 * The paths to be used by EasyAccept.
	 */
	private Collection<Path> paths = new LinkedList<Path>();

	/**
	 * The property to be set when there's a failure during the tests.
	 */
	private String failureproperty;

	private String formatter;

	private String directory;

	private Delegate cpDelegate;

	private Permissions perm = null;

	@Override
	public void init() {
		this.cpDelegate = ClasspathUtils.getDelegate(this);
		super.init();
	}

	public void setClasspathRef(Reference r) {
		this.cpDelegate.setClasspathref(r);
	}

	public Path createClasspath() {
		return this.cpDelegate.createClasspath();
	}

	public void setClassname(String fqcn) {
		this.cpDelegate.setClassname(fqcn);
	}

	/**
	 * Sets the value of 'formatter' attribute.
	 * 
	 * @param formatter
	 *            The attribute value.
	 * @uml.property name="formatter"
	 */
	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	/**
	 * Sets the value of 'todir' attribute.
	 * 
	 * @param todir
	 *            The attribute value.
	 * @uml.property name="todir"
	 */
	public void setTodir(String directory) {
		this.directory = directory;
	}

	/**
	 * Sets the value of 'failonerror' attribute.
	 * 
	 * @param failonerror
	 *            The attribute value.
	 * @uml.property name="failonerror"
	 */
	public void setFailonerror(boolean failonerror) {
		this.failonerror = failonerror;
	}

	/**
	 * Sets the value of 'failureproperty' attribute.
	 * 
	 * @param failureproperty
	 *            The name of the property to be declared and set
	 *            <code>true</code> when there's an failure during tests.
	 * @uml.property name="failureproperty"
	 */
	public void setFailureproperty(String failureproperty) {
		this.failureproperty = failureproperty;
	}

	/**
	 * Sets the value of 'facade' attribute.
	 * 
	 * @param facade
	 *            The attribute value.
	 * @uml.property name="facade"
	 */
	public void setFacade(String facade) {
		this.facade = facade;
	}

	/**
	 * Adds a 'path' element value.
	 * 
	 * @param path
	 *            The 'path' element.
	 */
	public void addPath(Path path) {
		this.paths.add(path);
	}

	/**
	 * Do the execution.
	 */
	@Override
	public void execute() {
		this.setSecurityManager();

		if (this.validate()) {
			EasyAccept tester = new EasyAccept();
			Facade facadeObj = null;
			try {
				facadeObj = new JavaApplicationFacadeAdapter(
						this.cpDelegate.newInstance());
			} catch (Exception e) {
				throw new BuildException("Invalid Facade class: " + this.facade);
			}
			int statusCode = 0;
			ResultsHandler results = new ResultsHandler();
			statusCode = this.runForEachPath(statusCode, tester, facadeObj,
					results);
			if (statusCode != 0) {
				if (this.failureproperty != null) {
					this.getProject().setNewProperty(this.failureproperty,
							TRUE_VALUE);
				}
			}

			ResultOutputter out = this.prepareOutputter();
			out.printResult(results);
		}
		this.unsetSecurityManager();
	}

	private void unsetSecurityManager() {
		if (this.perm != null) {
			this.perm.restoreSecurityManager();
		}
	}

	private void setSecurityManager() {
		this.perm = new Permissions(true);
		Permissions.Permission allPerm = new Permissions.Permission();
		allPerm.setClass("java.security.AllPermission");
		allPerm.setName("AllPermission");
		this.perm.addConfiguredGrant(allPerm);
		this.perm.setSecurityManager();
	}

	/**
	 * Execute for each path
	 * 
	 * @param statusCode
	 *            The code expressing the execution correctness.
	 * @param tester
	 *            The object that runs the acceptance tests.
	 * @param facadeObj
	 *            The facade
	 * @param results
	 * @return An int expressing the execution correctness.
	 */
	private int runForEachPath(int statusCode, EasyAccept tester,
			Facade facadeObj, ResultsHandler results) {
		Variables variables = new VariablesImpl();

		for (Path path : this.paths) {
			String[] files = path.list();
			for (int k = 0; k < files.length; k++) {
				String file = files[k];
				try {
					ScriptResultsManager srm = tester.runAcceptanceTest(
							facadeObj, file, variables);
					if (srm.getNumberOfErrors() > 0) {
						statusCode = -1;
					}
					results.addResult(srm);
				} catch (QuitSignalException e1) {
					this.getProject().log(e1.getMessage());
					statusCode = 0;
				} catch (Exception e) {
					this.getProject().log(e.getMessage());
					statusCode = 1;
				}
			}
		}
		return statusCode;
	}

	private ResultOutputter prepareOutputter() {
		ResultOutputter out;
		if (this.directory == null) {
			out = new ConsoleResultOutputter();
		} else {
			out = new FileResultOutputter(this.directory);
		}

		// TODO refactor hard coded strings
		if (this.formatter.equals("txt")) {
			out.setFormatter(new TextResultFormatter());
		} else if (this.formatter.equals("xml")) {
			out.setFormatter(new XMLResultFormatter());
		}
		return out;
	}

	/**
	 * Validates the task:
	 * 
	 * 1. Checks if the facade was set and is a valid Java class.
	 * 
	 * 2. Checks if the path was set and has any file.
	 * 
	 * @return <code>true</code> when the task is able to execute.
	 *         <code>false</code> otherwise.
	 * @throws BuildException
	 *             When failonerror is <code>true</code> and the task is
	 *             invalid.
	 */
	private boolean validate() {

		if (this.facade == null) {
			if (this.failonerror) {
				throw new BuildException(NO_FACADE_SPECIFIED_MSG);
			} else {
				this.getProject().log(NO_FACADE_SPECIFIED_MSG);
				return false;
			}
		} else {
			if (!this.validateFacade(this.facade)) {
				return false;
			}
		}
		if (this.paths.size() < 1) {
			if (this.failonerror) {
				throw new BuildException(NO_FILES_FOUND_MSG);
			} else {
				this.getProject().log(NO_FILES_FOUND_MSG);
				return false;
			}
		} else {
			if (!this.validadePaths(this.paths)) {
				return false;
			}
		}
		if (this.formatter == null) {
			this.formatter = "txt";
		} else {
			if (!this.validateFormatter(this.formatter)) {
				if (this.failonerror) {
					throw new BuildException(INVALID_FORMATTER_MSG);
				} else {
					this.getProject().log(INVALID_FORMATTER_MSG);
					return false;
				}
			}
		}
		if (this.directory != null && !(new File(this.directory).isDirectory())) {
			if (this.failonerror) {
				throw new BuildException(NOT_A_DIR_MSG);
			} else {
				this.getProject().log(NOT_A_DIR_MSG);
				return false;
			}
		}

		return true;
	}

	private boolean validateFormatter(String formatter) {
		return formatter.equals("xml") || formatter.equals("txt");
	}

	/**
	 * Validates the paths. Checks if the paths specify any file.
	 * 
	 * @param paths
	 *            The Vector of paths.
	 * @return <code>true</code> when there's at least one file specified by the
	 *         paths. <code>false</code> otherwise.
	 * @throws BuildException
	 *             when there's no file specified and 'failonerror' attribute is
	 *             <code>true</code>.
	 */
	private boolean validadePaths(Collection<Path> paths) {
		int numberOfFiles = 0;

		for (Path path : paths) {
			numberOfFiles += path.list().length;
		}

		if (numberOfFiles == 0) {
			if (this.failonerror) {
				throw new BuildException(NO_FILES_FOUND_MSG);
			} else {
				this.getProject().log(NO_FILES_FOUND_MSG);
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates the facade. Checks whether the class exists or not. Throws a
	 * BuildException if it doesn't exist and the 'failonerror' attribute is
	 * <code>true</code>.
	 * 
	 * @param facadeClass
	 *            The facade class name.
	 * @return <code>true</code> case the class is valid. <code>false</code>
	 *         otherwise. Throws a
	 * @throws BuildException
	 *             if the Facade doesn't exist and the 'failonerror' attribute
	 *             is <code>true</code>.
	 */
	private boolean validateFacade(String facadeClass) {
		try {
			this.cpDelegate.setClassname(facadeClass);
			this.cpDelegate.newInstance();
		} catch (BuildException e) {
			if (this.failonerror) {
				throw new BuildException(THE_FACADE_CLASS_WAS_NOT_FOUND_MSG);
			} else {
				this.getProject().log(THE_FACADE_CLASS_WAS_NOT_FOUND_MSG);
				return false;
			}
		}
		return true;
	}
}
