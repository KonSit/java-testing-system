package expression;

public class Add extends BinaryExpression {

    public Add(Expression leftExpression, Expression rightExpression) {
        super(leftExpression, rightExpression);
    }

    @Override
    public int evaluate(int variableValue) {
        return leftExpression.evaluate(variableValue) + rightExpression.evaluate(variableValue);
    }

    @Override
    public String operationSymbol() {
        return "+";
    }
}
