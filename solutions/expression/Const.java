package expression;

public class Const implements Expression {
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public String toParenthesisedString() {
        return String.valueOf(value);
    }

    @Override
    public int evaluate(int variableValue) {
        return value;
    }
}
