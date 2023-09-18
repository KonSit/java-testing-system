package expression;

/**
 * Computable integral expression depending on one integral variable.
 * @author Ivanov Timofey
 */
public interface Expression extends ToParenthesisedString {
    /**
     * Evaluates an expression.
     *
     * Variables with different names considered the same.
     * @param variableValue variable value.
     * @return expression computation result.
     */
    int evaluate(int variableValue);
}
