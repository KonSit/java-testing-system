package expression;

public class Multiply extends BinaryExpression{

    public Multiply(Expression leftExpression, Expression rightExpression) {
        super(leftExpression, rightExpression);
    }

    @Override
    public int evaluate(int variableValue) {
        return leftExpression.evaluate(variableValue) * rightExpression.evaluate(variableValue);
    }

    @Override
    public String operationSymbol() {
        return "*";
    }
}