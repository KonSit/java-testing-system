package expression;

public class Variable implements Expression{
    String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int evaluate(int variableValue) {
        return variableValue;
    }

    @Override
    public String toParenthesisedString() {
        return name;
    }
}
