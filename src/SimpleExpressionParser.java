/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {

	/**
	 * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
	 * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @param withJavaFXControls you can just ignore this variable for R1
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		str = str.replaceAll(" ", "");
		Expression expression = parseE(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		return expression;
	}

    /**
     * Attempt to parse the expression based on E grammar
     * @param str the string need to be parsed
     * @return parsed Expression
     */
	protected Expression parseE (String str) {
        // E := A | X
	    Expression expression;
        
	    // Fist try A
        expression = parseA(str);
        if (expression != null)
            return expression;

		// Next try X
	    expression = parseX(str);
	    if (expression != null)
	        return expression;

	    // Else it is not a valid expression
        return null;
	}

    /**
     * Attempt to parse the expression based on A grammar
     * @param str the string need to be parsed
     * @return parsed Expression
     */
    private  Expression parseA (String str){
        // A := A+M | M
        Expression expression1, expression2;
        
        // Fist try A+M
        int indexOfPlus = str.indexOf("+");
        while (indexOfPlus >= 0) { // try each +
            expression1 = parseA(str.substring(0, indexOfPlus));
            expression2 = parseM(str.substring(indexOfPlus+1));
            if (expression1 != null /* before + */ && expression2 != null /* after + */) {
                CompoundExpression additiveExpression = new AdditiveExpression();
                additiveExpression.addSubexpression(expression1);
                additiveExpression.addSubexpression(expression2);
                expression1.setParent(additiveExpression);
                expression2.setParent(additiveExpression);
                return additiveExpression;
            }
            indexOfPlus = str.indexOf('+', indexOfPlus+1);
        }
        
        // Next try M
        expression1 = parseM(str);
        if (expression1 != null)
            return expression1;
        
        // Else it is not a valid expression
        return null;
    }

    /**
     * Attempt to parse the expression based on M grammar
     * @param str the string need to be parsed
     * @return parsed Expression
     */
    private Expression parseM(String str){
        // M := M*M | X
        Expression expression1, expression2;

        // Fist try M*M
        int indexOfTimes = str.indexOf(str.contains("·")?"·":"*");
        while (indexOfTimes >= 0) { // try each ·
            expression1 = parseM(str.substring(0, indexOfTimes));
            expression2 = parseM(str.substring(indexOfTimes+1));
            if (expression1 != null /* before · */ && expression2 != null /* after · */) {
                CompoundExpression multiplicativeExpression = new MultiplicativeExpression();
                multiplicativeExpression.addSubexpression(expression1);
                multiplicativeExpression.addSubexpression(expression2);
                expression1.setParent(multiplicativeExpression);
                expression2.setParent(multiplicativeExpression);
                return multiplicativeExpression;
            }
            indexOfTimes = str.indexOf(str.contains("·")?'·':'*', indexOfTimes+1);
        }

        // Next try X
        expression1 = parseX(str);
        if (expression1 != null)
            return expression1;

        // Else it is not a valid expression
        return null;
    }

    /**
     * Attempt to parse the expression based on X grammar
     * @param str the string need to be parsed
     * @return parsed Expression
     */
    private Expression parseX(String str){
        // X := (E) | L
        Expression expression;

        // Fist try (E)
        final int length = str.length();
        if (str.matches("^\\(.*\\)$")) {
            // if the string is wrapped with '(' and ')'
            expression = parseE(str.substring(1, length-1));
            if (expression != null) {
                CompoundExpression parentheticalExpression = new ParentheticalExpression();
                parentheticalExpression.addSubexpression(expression);
                expression.setParent(parentheticalExpression);
                return parentheticalExpression;
            }
        }

        // Next try L
        expression = parseL(str);
        if (expression != null)
            return expression;

        // Else it is not a valid expression
        return null;
    }

    /**
     * Attempt to parse the expression based on L grammar
     * @param str the string need to be parsed
     * @return parsed Expression
     */
    private Expression parseL(String str){
        // L := [0-9]+ | [a-z]
        Expression expression;

        // Try [0-9]+ | [a-z]
        if (str.matches("^\\d+$") || str.matches("^[a-z]$")){
            expression = new LiteralExpression(str);
            return expression;
        }

        // Else it is not a valid expression
        return null;
    }
}
