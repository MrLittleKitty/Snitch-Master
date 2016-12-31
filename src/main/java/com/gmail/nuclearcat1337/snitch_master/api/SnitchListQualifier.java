package com.gmail.nuclearcat1337.snitch_master.api;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;

/**
 * Created by Mr_Little_Kitty on 7/20/2016.
 * A conditional expression for a SnitchList to accept a Snitch.
 */
public class SnitchListQualifier
{
    private static final String CULL_TIME_TOKEN = "cull_time";
    private static final String WORLD_TOKEN = "world";
    private static final String GROUP_TOKEN = "group";
    private static final String NAME_TOKEN = "name";
    private static final String ORIGIN_TOKEN = "origin";
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
    public SnitchListQualifier(String expression)
    {
        this.expression = expression;

        assert isSyntaxValid(expression);

        this.tokens = expression.split(" ");
    }

    /**
     * Changes this qualifier to qualify using the provided expression.
     * Returns true if the qualifier was updated.
     * Returns false otherwise.
     */
    public boolean updateQualifier(String expression)
    {
        if(!isSyntaxValid(expression))
            return false;

        this.expression = expression;
        this.tokens = expression.split(" ");

        return true;
    }

    /**
     * Returns true if the given Snitch meets the criteria specified by this qualifier's expression.
     * Returns false otherwise.
     */
    public boolean isQualified(Snitch snitch)
    {
        try
        {
            try
            {
                int i = 0;

                String left = tokens[i++];
                String operator = tokens[i++];
                String right = tokens[i++];

                boolean finalResult = evaluateTokens(left, right, operator, snitch);
                while (i < tokens.length)
                {
                    String token = tokens[i++];
                    if (token.equals(OR_OPERATOR) || token.equals(AND_OPERATOR))
                    {
                        if (i + 2 >= tokens.length)
                            throw new Exception("Syntax error: Statement contains a logical operator but no right operand");

                        left = tokens[i++];
                        operator = tokens[i++];
                        right = tokens[i++];

                        boolean result = evaluateTokens(left, right, operator, snitch);
                        if (token.equals(OR_OPERATOR))
                        {
                            finalResult = finalResult || result;
                            if(finalResult) //Short circuiting logic
                                return true;
                        }
                        else
                        {
                            finalResult = finalResult && result;
                            if(!finalResult) //Short circuiting logic
                                return false;
                        }
                    }
                }

                return finalResult;
            }
            catch (IndexOutOfBoundsException e)
            {
                throw new Exception("Syntax error: The statement contained did not contain required tokens.");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the expression that this qualifier uses to qualify Snitches.
     */
    public String toString()
    {
        return expression;
    }

    /**
     * Returns true if the syntax of the provided conditional expression is correct.
     * Returns false otherwise.
     */
    public static boolean isSyntaxValid(String expression)
    {
        String[] tokens = expression.split(" ");
        try
        {
            try
            {
                int i = 0;

                String left = tokens[i++];
                String operator = tokens[i++];
                String right = tokens[i++];

                boolean finalResult = checkTokens(left, right, operator);
                while (i < tokens.length && finalResult)
                {
                    String token = tokens[i++];
                    if (token.equals(OR_OPERATOR) || token.equals(AND_OPERATOR))
                    {
                        if (i + 2 >= tokens.length)
                            return false;

                        left = tokens[i++];
                        operator = tokens[i++];
                        right = tokens[i++];

                        finalResult = checkTokens(left, right, operator);
                    }
                }

                return finalResult;
            }
            catch (IndexOutOfBoundsException e)
            {
                return false;
            }
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private static boolean checkTokens(String left, String right, String operator)
    {
        //Check if the left side is either a string variable or a string literal
        if(isStringLiteral(left) || isStringVariable(left))
        {
            //If the operator is a not a valid string operator then throw an error
            if(!isEqualityOperator(operator))
                return false;

            //If the right hand argument is not a string operand then throw an error
            if(!isStringLiteral(right) && !isStringVariable(right))
                return false;

            return true;
        }
        else //If it wasnt any type of string then it is some type of number operation (or just invalid syntax)
        {
            //I guess right here is where im taking a stance that we wont allow things like: 100 == 100
            if(isDoubleVariable(left) || isDoubleVariable(right))
            {
                //Is the operator is not valid for doubles then throw an error
                if(!isComparisonToken(operator))
                    return false;

               return (isDoubleVariable(left) || isDoubleLiteral(left)) && (isDoubleVariable(right) || isDoubleLiteral(right));
            }
            else if(isIntegerVariable(left) || isIntegerVariable(right))
            {
                //Is the operator is not valid for doubles then throw an error
                if(!isComparisonToken(operator))
                    return false;

                return (isIntegerVariable(left) || isIntegerLiteral(left)) && (isIntegerVariable(right) || isIntegerLiteral(right));
            }
        }
        return false;
    }

    private static boolean evaluateTokens(String left, String right, String operator, Snitch snitch) throws Exception
    {
        if(isStringLiteral(left) || isStringVariable(left))
        {
            //If the operator is a not a valid string operator then throw an error
            if(!isEqualityOperator(operator))
                throw new Exception("Syntax error at expression: "+left+" "+operator+" "+right);

            //If the right hand argument is not a string operand then throw an error
            if(!isStringLiteral(right) && !isStringVariable(right))
                throw new Exception("Syntax error at expression: "+left+" "+operator+" "+right);

            return evaluateStringExpression(getStringValue(left,snitch),getStringValue(right,snitch),operator);
        }
        else
        {
            if(isDoubleVariable(left) || isDoubleVariable(right))
            {
                //Is the operator is not valid for doubles then throw an error
                if(!isComparisonToken(operator))
                    throw new Exception("Syntax error at expression: "+left+" "+operator+" "+right);

                if((isDoubleVariable(left) || isDoubleLiteral(left)) && (isDoubleVariable(right) || isDoubleLiteral(right)))
                    return evaluateDoubleComparisonExpression(getDoubleValue(left,snitch),getDoubleValue(right,snitch),operator);

                throw new Exception("Syntax error at expression: "+left+" "+operator+" "+right);
            }
            else if(isIntegerVariable(left) || isIntegerVariable(right))
            {
                //Is the operator is not valid for doubles then throw an error
                if(!isComparisonToken(operator))
                    throw new Exception("Syntax error at expression: "+left+" "+operator+" "+right);

                if((isIntegerVariable(left) || isIntegerLiteral(left)) && (isIntegerVariable(right) || isIntegerLiteral(right)))
                    return evaluateIntegerComparisonExpression(getIntegerValue(left,snitch),getIntegerValue(right,snitch),operator);

                throw new Exception("Syntax error at expression: "+left+" "+operator+" "+right);
            }
        }
        throw new Exception("Syntax error: Not valid operand types for expression: "+left+" "+operator+" ");
    }

//    private boolean computeTokens(String[] args, int beingIndex, int endIndex) throws Exception
//    {
//        String token = args[beingIndex];
////        if(token.startsWith("("))
////        {
////            //Remove the opening parenthesis
////            args[beingIndex] = token.substring(1,token.length());
////            Integer endingIndex = null;
////            for(int i = endIndex-1; i > beingIndex; i--)
////            {
////                if(args[i].endsWith(")"))
////                {
////                    args[i] = args[i].substring(0,args[i].length()-1);
////                    endingIndex = i;
////                    break;
////                }
////            }
////
////            if(endingIndex == null)
////                throw new Exception("Parsing exception, Invalid syntax starting at token: "+token);
////
////            computeTokens(args,beingIndex,endingIndex);
////        }
//
//        if(token.contains("'") || isStringVariable(token)) //Its a string literal or a string variable
//        {
//
//        }
//        else if(token.)
//
//    }

    private static double getDoubleValue(String token, Snitch snitch) throws Exception
    {
        if(token.equalsIgnoreCase(CULL_TIME_TOKEN))
            return snitch.getCullTime();
        else if(isDoubleLiteral(token))
            return Double.parseDouble(token);
        else
            throw new Exception("Syntax error: Token "+token+" is not a valid double value");
    }

    private static int getIntegerValue(String token, Snitch snitch) throws Exception
    {
        if(token.equalsIgnoreCase(X_TOKEN))
            return snitch.getLocation().getX();
        else if(token.equalsIgnoreCase(Y_TOKEN))
            return snitch.getLocation().getY();
        else if(token.equalsIgnoreCase(Z_TOKEN))
            return snitch.getLocation().getZ();
        else if(isIntegerLiteral(token))
            return Integer.parseInt(token);
        else
            throw new Exception("Syntax error: Token "+token+" is not a valid integer value");
    }

    private static String getStringValue(String token, Snitch snitch) throws Exception
    {
        if(isStringLiteral(token))
            return token.substring(1,token.length()-1);
        else if(token.equalsIgnoreCase(GROUP_TOKEN))
            return snitch.getGroupName();
        else if(token.equalsIgnoreCase(NAME_TOKEN))
            return snitch.getSnitchName();
        else if(token.equalsIgnoreCase(WORLD_TOKEN))
            return snitch.getLocation().getWorld();
        else if(token.equalsIgnoreCase(ORIGIN_TOKEN))
            return snitch.getOrigin();
        else
            throw new Exception("Syntax error: Token "+token+" is not a valid String value");
    }

    private static boolean isIntegerLiteral(String token)
    {
        try
        {
            Integer.parseInt(token);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    private static boolean isDoubleLiteral(String token)
    {
        try
        {
            Double.parseDouble(token);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    private static boolean isStringLiteral(String token)
    {
        return token.startsWith(STRING_LITERAL_CHAR) && token.endsWith(STRING_LITERAL_CHAR);
    }

//    private static boolean isVariable(String token)
//    {
//        return  token.equalsIgnoreCase(CULL_TIME_TOKEN) ||
//                token.equalsIgnoreCase(GROUP_TOKEN) ||
//                token.equalsIgnoreCase(NAME_TOKEN) ||
//                //token.equalsIgnoreCase("x") ||
//                //token.equalsIgnoreCase("y") ||
//                //token.equalsIgnoreCase("z") ||
//                token.equalsIgnoreCase(WORLD_TOKEN) ||
//                token.equalsIgnoreCase(ORIGIN_TOKEN);
//
//    }

    private static boolean isStringVariable(String token)
    {
        return  token.equalsIgnoreCase(WORLD_TOKEN) ||
                token.equalsIgnoreCase(GROUP_TOKEN) ||
                token.equalsIgnoreCase(NAME_TOKEN) ||
                token.equalsIgnoreCase(ORIGIN_TOKEN);
    }

    private static boolean isDoubleVariable(String token)
    {
        return  token.equalsIgnoreCase(CULL_TIME_TOKEN);
    }

    private static boolean isIntegerVariable(String token)
    {
        return token.equalsIgnoreCase(X_TOKEN) || token.equalsIgnoreCase(Y_TOKEN) || token.equalsIgnoreCase(Z_TOKEN);
    }

    private static boolean isComparisonToken(String token)
    {
        return  token.equals(EQUAL_OPERATOR) ||
                token.equals(NOT_EQUAL_OPERATOR) ||
                token.equals(LESS_THAN_OR_EQUAL_OPERATOR) ||
                token.equals(GREATER_THAN_OR_EQUAL_OPERATOR) ||
                token.equals(LESS_THAN_OPERATOR) ||
                token.equals(GREATER_THAN_OPERATOR);
    }

    private static boolean isEqualityOperator(String token)
    {
        return token.equals(EQUAL_OPERATOR) || token.equals(NOT_EQUAL_OPERATOR);
    }

    private static boolean evaluateStringExpression(String one, String two, String operator) throws Exception
    {
        if(operator.equals(EQUAL_OPERATOR))
            return one.equalsIgnoreCase(two);
        else if(operator.equals(NOT_EQUAL_OPERATOR))
            return !one.equalsIgnoreCase(two);
        else throw new Exception("Error doing String comparison on expression: "+one+" "+operator+" "+two);
    }

    private static boolean evaluateIntegerComparisonExpression(int one, int two, String operator) throws Exception
    {
        if(operator.equals(EQUAL_OPERATOR))
            return one == two;
        else if(operator.equals(NOT_EQUAL_OPERATOR))
            return one != two;
        else if(operator.equals(LESS_THAN_OR_EQUAL_OPERATOR))
            return one <= two;
        else if(operator.equals(GREATER_THAN_OR_EQUAL_OPERATOR))
            return one >= two;
        else if(operator.equals(LESS_THAN_OPERATOR))
            return one < two;
        else if(operator.equals(GREATER_THAN_OPERATOR))
            return one > two;
        else throw new Exception("Error doing integer comparison on expression: "+one+" "+operator+" "+two);
    }

    private static boolean evaluateDoubleComparisonExpression(double one, double two, String operator) throws Exception
    {
        if(operator.equals(EQUAL_OPERATOR))
            return one == two;
        else if(operator.equals(NOT_EQUAL_OPERATOR))
            return one != two;
        else if(operator.equals(LESS_THAN_OR_EQUAL_OPERATOR))
            return one <= two;
        else if(operator.equals(GREATER_THAN_OR_EQUAL_OPERATOR))
            return one >= two;
        else if(operator.equals(LESS_THAN_OPERATOR))
            return one < two;
        else if(operator.equals(GREATER_THAN_OPERATOR))
            return one > two;
        else throw new Exception("Error doing double comparison on expression: "+one+" "+operator+" "+two);
    }
}
