package easyaccept;

/**
 * Exception for EasyAccept Main class syntax errors.
 */
public class EasyAcceptSyntaxException extends EasyAcceptException {
	private static final String EASY_ACCEPT_SYNTAX = "EasyAccept Syntax: java easyaccept.EasyAccept facadeClassName testFile [...]";

	/**
	 * Exception constructor.
	 */
	public EasyAcceptSyntaxException() {
		super(EASY_ACCEPT_SYNTAX);
	}
}
