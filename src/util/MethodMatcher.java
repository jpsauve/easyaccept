package util;

import java.lang.reflect.Method;

import easyaccept.EasyAcceptException;

public class MethodMatcher {
	/**
	 * Try to convert the string to the expected method type. parsedLine
	 * contains the arguments values and parameters[i] contains argumet expected
	 * type.
	 */
	public static  boolean methodMatch(Method method, ParsedLine parsedLine)
			throws EasyAcceptException, ConverterException {

		Class[] parameters = method.getParameterTypes();
		assert parsedLine.numberOfParameters() > 0;
		if (!method.getName().equals(
				parsedLine.getParameter(0).getValueAsString())) {
			return false;
		}
		if (parameters.length != parsedLine.numberOfParameters() - 1) {
			return false;
		}
		ParameterTypeConverter.convertParam(parameters, parsedLine
				.getCommandArgs());
		return true;
	}
}
