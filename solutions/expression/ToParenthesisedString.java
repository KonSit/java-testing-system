package expression;

/**
 * Common interface for all expressions convertable to full-bracket form.
 * @see Expression
 * @author Ivanov Timofey
 */
public interface ToParenthesisedString {
    /**
     * Convert an expression into a full-bracket form. All brackets is round.
     * @return string containing full-bracket form.
     */
    default String toParenthesisedString() {
        return toString();
    }
}
