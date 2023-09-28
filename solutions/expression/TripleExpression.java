package expression;

/**
 * Computable integral expression depending on three integral variables: "x", "y" and "z".
 *
 * No other variable names should be supported.
 * @author Ivanov Timofey
 */
public interface TripleExpression extends ToParenthesisedString {
    /**
     * Evaluates an expression.
     *
     * @param xValue "x" variable value.
     * @param yValue "y" variable value.
     * @param zValue "z" variable value.
     * @return expression computation result.
     *
     */
    int evaluate(int xValue, int yValue, int zValue);
}
