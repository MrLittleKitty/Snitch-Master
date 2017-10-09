package com.gmail.nuclearcat1337.snitch_master.api;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;

import java.util.Collection;

/**
 * A conditional expression for a SnitchList to accept a Snitch.
 */
public class SnitchListQualifier {
	public static final int MAX_QUALIFIER_TEXT_LENGTH = Integer.MAX_VALUE;

	private static final String CULL_TIME_TOKEN = "cull_time";
	private static final String WORLD_TOKEN = "world";
	private static final String GROUP_TOKEN = "group";
	private static final String NAME_TOKEN = "name";

	private static final String DEPRECATED_ORIGIN_TOKEN = "origin";
	private static final String TAG_TOKEN = "tag";


	private static final String X_TOKEN = "x";
	private static final String Y_TOKEN = "y";
	private static final String Z_TOKEN = "z";

	private static final String AND_OPERATOR = "&&";
	private static final String OR_OPERATOR = "||";

	private static final String EQUAL_OPERATOR = "==";
	private static final String NOT_EQUAL_OPERATOR = "!=";
	private static final String LESS_THAN_OPERATOR = "<";
	private static final String GREATER_THAN_OPERATOR = ">";
	private static final String LESS_THAN_OR_EQUAL_OPERATOR = "<=";
	private static final String GREATER_THAN_OR_EQUAL_OPERATOR = ">=";

	private static final String STRING_LITERAL_CHAR = "'";

	private String expression;
	private String[] tokens;

	/**
	 * Creates a new SnitchListQualifier with the given conditional expression.
	 * Throws an assertion exception if the given expression is not valid.
	 */
	public SnitchListQualifier(String expression) {
		this.expression = expression;

		assert isSyntaxValid(expression);

		this.tokens = expression.split(" ");
	}

	/**
	 * Changes this qualifier to qualify using the provided expression.
	 * Returns true if the qualifier was updated.
	 * Returns false otherwise.
	 */
	public boolean equalsString(String expression) {
		return expression.equalsIgnoreCase(this.expression);
	}

	/**
	 * Returns true if the given Snitch meets the criteria specified by this qualifier's expression.
	 * Returns false otherwise.
	 */
	public boolean isQualified(Snitch snitch) {
		try {
			try {
				int i = 0;

				String left = tokens[i++];
				String operator = tokens[i++];
				String right = tokens[i++];

				boolean finalResult = evaluateTokens(left, right, operator, snitch);
				while (i < tokens.length) {
					String token = tokens[i++];
					if (token.equals(OR_OPERATOR) || token.equals(AND_OPERATOR)) {
						if (i + 2 >= tokens.length) {
							throw new Exception("Syntax error: Statement contains a logical operator but no right operand");
						}

						left = tokens[i++];
						operator = tokens[i++];
						right = tokens[i++];

						boolean result = evaluateTokens(left, right, operator, snitch);
						if (token.equals(OR_OPERATOR)) {
							finalResult = finalResult || result;
						} else {
							finalResult = finalResult && result;
						}
					}
				}

				return finalResult;
			} catch (IndexOutOfBoundsException e) {
				throw new Exception("Syntax error: The statement contained did not contain required tokens.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the expression that this qualifier uses to qualify Snitches.
	 */
	public String toString() {
		return expression;
	}

	/**
	 * Returns true if the syntax of the provided conditional expression is correct.
	 * Returns false otherwise.
	 */
	public static boolean isSyntaxValid(String expression) {
		String[] tokens = expression.split(" ");
		try {
			try {
				int i = 0;

				String left = tokens[i++];
				String operator = tokens[i++];
				String right = tokens[i++];

				boolean finalResult = checkTokens(left, right, operator);
				while (i < tokens.length && finalResult) {
					String token = tokens[i++];
					if (token.equals(OR_OPERATOR) || token.equals(AND_OPERATOR)) {
						if (i + 2 >= tokens.length) {
							return false;
						}

						left = tokens[i++];
						operator = tokens[i++];
						right = tokens[i++];

						finalResult = checkTokens(left, right, operator);
					}
				}

				return finalResult;
			} catch (IndexOutOfBoundsException e) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean checkTokens(String left, String right, String operator) {
		if (isListVariable(left) || isListVariable(right)) {
			if (!isEqualityOperator(operator)) {
				return false;
			}

			//If one of the variables is a string list then the other needs to be a string literal
			if (isListVariable(left) && isStringLiteral(right)) {
				return true;
			}

			if (isListVariable(right) && isStringLiteral(left)) {
				return true;
			}

			return false;
		} else if (isStringLiteral(left) || isStringVariable(left)) {
			if (!isEqualityOperator(operator)) {
				return false;
			}

			if (!isStringLiteral(right) && !isStringVariable(right)) {
				return false;
			}

			return true;
		//I guess right here is where im taking a stance that we wont allow things like: 100 == 100
		} else if (isDoubleVariable(left) || isDoubleVariable(right)) {
			if (!isComparisonToken(operator)) {
				return false;
			}

			return (isDoubleVariable(left) || isDoubleLiteral(left)) && (isDoubleVariable(right) || isDoubleLiteral(right));
		} else if (isIntegerVariable(left) || isIntegerVariable(right)) {
			if (!isComparisonToken(operator)) {
				return false;
			}

			return (isIntegerVariable(left) || isIntegerLiteral(left)) && (isIntegerVariable(right) || isIntegerLiteral(right));
		}

		return false;
	}

	private static boolean evaluateTokens(String left, String right, String operator, Snitch snitch) throws Exception {
		if (isListVariable(left) || isListVariable(right)) {
			if (!isEqualityOperator(operator)) {
				throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
			}

			//If one of the variables is a string list then the other needs to be a string literal
			if (isListVariable(left) && isStringLiteral(right)) {
				return evaluateListExpression(getListValue(left, snitch), getStringValue(right, snitch), operator);
			}

			if (isListVariable(right) && isStringLiteral(left)) {
				return evaluateListExpression(getListValue(right, snitch), getStringValue(left, snitch), operator);
			}

			throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
		} else if (isStringLiteral(left) || isStringVariable(left)) {
			if (!isEqualityOperator(operator)) {
				throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
			}

			if (!isStringLiteral(right) && !isStringVariable(right)) {
				throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
			}

			return evaluateStringExpression(getStringValue(left, snitch), getStringValue(right, snitch), operator);
		} else if (isDoubleVariable(left) || isDoubleVariable(right)) {
			if (!isComparisonToken(operator)) {
				throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
			}

			if ((isDoubleVariable(left) || isDoubleLiteral(left)) && (isDoubleVariable(right) || isDoubleLiteral(right))) {
				return evaluateDoubleComparisonExpression(getDoubleValue(left, snitch), getDoubleValue(right, snitch), operator);
			}

			throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
		} else if (isIntegerVariable(left) || isIntegerVariable(right)) {
			if (!isComparisonToken(operator)) {
				throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
			}

			if ((isIntegerVariable(left) || isIntegerLiteral(left)) && (isIntegerVariable(right) || isIntegerLiteral(right))) {
				return evaluateIntegerComparisonExpression(getIntegerValue(left, snitch), getIntegerValue(right, snitch), operator);
			}

			throw new Exception("Syntax error at expression: " + left + " " + operator + " " + right);
		}

		throw new Exception("Syntax error: Not valid operand types for expression: " + left + " " + operator + " ");
	}

	private static Collection<String> getListValue(String token, Snitch snitch) throws Exception {
		if (token.equalsIgnoreCase(DEPRECATED_ORIGIN_TOKEN) || token.equalsIgnoreCase(TAG_TOKEN)) {
			return snitch.getTags();
		} else {
			throw new Exception("Syntax error: Token " + token + " is not a valid list value");
		}
	}

	private static double getDoubleValue(String token, Snitch snitch) throws Exception {
		if (token.equalsIgnoreCase(CULL_TIME_TOKEN)) {
			return snitch.getCullTime();
		} else if (isDoubleLiteral(token)) {
			return Double.parseDouble(token);
		} else {
			throw new Exception("Syntax error: Token " + token + " is not a valid double value");
		}
	}

	private static int getIntegerValue(String token, Snitch snitch) throws Exception {
		if (token.equalsIgnoreCase(X_TOKEN)) {
			return snitch.getLocation().getX();
		} else if (token.equalsIgnoreCase(Y_TOKEN)) {
			return snitch.getLocation().getY();
		} else if (token.equalsIgnoreCase(Z_TOKEN)) {
			return snitch.getLocation().getZ();
		} else if (isIntegerLiteral(token)) {
			return Integer.parseInt(token);
		} else {
			throw new Exception("Syntax error: Token " + token + " is not a valid integer value");
		}
	}

	private static String getStringValue(String token, Snitch snitch) throws Exception {
		if (isStringLiteral(token)) {
			return token.substring(1, token.length() - 1);
		} else if (token.equalsIgnoreCase(GROUP_TOKEN)) {
			return snitch.getGroupName();
		} else if (token.equalsIgnoreCase(NAME_TOKEN)) {
			return snitch.getName();
		} else if (token.equalsIgnoreCase(WORLD_TOKEN)) {
			return snitch.getLocation().getWorld();
		} else {
			throw new Exception("Syntax error: Token " + token + " is not a valid String value");
		}
	}

	private static boolean isIntegerLiteral(String token) {
		try {
			Integer.parseInt(token);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isDoubleLiteral(String token) {
		try {
			Double.parseDouble(token);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isStringLiteral(String token) {
		return token.startsWith(STRING_LITERAL_CHAR) && token.endsWith(STRING_LITERAL_CHAR);
	}

	private static boolean isListVariable(String token) {
		return token.equalsIgnoreCase(DEPRECATED_ORIGIN_TOKEN) || token.equalsIgnoreCase(TAG_TOKEN);
	}

	private static boolean isStringVariable(String token) {
		return token.equalsIgnoreCase(WORLD_TOKEN) || token.equalsIgnoreCase(GROUP_TOKEN) || token.equalsIgnoreCase(NAME_TOKEN); //|| token.equalsIgnoreCase(DEPRECATED_ORIGIN_TOKEN);
	}

	private static boolean isDoubleVariable(String token) {
		return token.equalsIgnoreCase(CULL_TIME_TOKEN);
	}

	private static boolean isIntegerVariable(String token) {
		return token.equalsIgnoreCase(X_TOKEN) || token.equalsIgnoreCase(Y_TOKEN) || token.equalsIgnoreCase(Z_TOKEN);
	}

	private static boolean isComparisonToken(String token) {
		return token.equals(EQUAL_OPERATOR) || token.equals(NOT_EQUAL_OPERATOR) || token.equals(LESS_THAN_OR_EQUAL_OPERATOR) || token.equals(GREATER_THAN_OR_EQUAL_OPERATOR) || token.equals(LESS_THAN_OPERATOR) || token.equals(GREATER_THAN_OPERATOR);
	}

	private static boolean isEqualityOperator(String token) {
		return token.equals(EQUAL_OPERATOR) || token.equals(NOT_EQUAL_OPERATOR);
	}

	private static boolean evaluateListExpression(Collection<String> list, String value, String operator) throws Exception {
		if (operator.equals(EQUAL_OPERATOR)) {
			return list.contains(value);
		} else if (operator.equals(NOT_EQUAL_OPERATOR)) {
			return !list.contains(value);
		} else {
			throw new Exception("Error doing list evaluation on expression: list " + operator + " " + value);
		}
	}

	private static boolean evaluateStringExpression(String one, String two, String operator) throws Exception {
		if (operator.equals(EQUAL_OPERATOR)) {
			return one.equalsIgnoreCase(two);
		} else if (operator.equals(NOT_EQUAL_OPERATOR)) {
			return !one.equalsIgnoreCase(two);
		} else {
			throw new Exception("Error doing String comparison on expression: " + one + " " + operator + " " + two);
		}
	}

	private static boolean evaluateIntegerComparisonExpression(int one, int two, String operator) throws Exception {
		if (operator.equals(EQUAL_OPERATOR)) {
			return one == two;
		} else if (operator.equals(NOT_EQUAL_OPERATOR)) {
			return one != two;
		} else if (operator.equals(LESS_THAN_OR_EQUAL_OPERATOR)) {
			return one <= two;
		} else if (operator.equals(GREATER_THAN_OR_EQUAL_OPERATOR)) {
			return one >= two;
		} else if (operator.equals(LESS_THAN_OPERATOR)) {
			return one < two;
		} else if (operator.equals(GREATER_THAN_OPERATOR)) {
			return one > two;
		} else {
			throw new Exception("Error doing integer comparison on expression: " + one + " " + operator + " " + two);
		}
	}

	private static boolean evaluateDoubleComparisonExpression(double one, double two, String operator) throws Exception {
		if (operator.equals(EQUAL_OPERATOR)) {
			return one == two;
		} else if (operator.equals(NOT_EQUAL_OPERATOR)) {
			return one != two;
		} else if (operator.equals(LESS_THAN_OR_EQUAL_OPERATOR)) {
			return one <= two;
		} else if (operator.equals(GREATER_THAN_OR_EQUAL_OPERATOR)) {
			return one >= two;
		} else if (operator.equals(LESS_THAN_OPERATOR)) {
			return one < two;
		} else if (operator.equals(GREATER_THAN_OPERATOR)) {
			return one > two;
		} else {
			throw new Exception("Error doing double comparison on expression: " + one + " " + operator + " " + two);
		}
	}
}
