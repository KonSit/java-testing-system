package expression;

public class Divide extends BinaryExpression{

    public Divide(Expression leftExpression, Expression rightExpression) {
        super(leftExpression, rightExpression);
    }

    @Override
    public int evaluate(int variableValue) {
        return leftExpression.evaluate(variableValue) / rightExpression.evaluate(variableValue);
    }

    @Override
    public String operationSymbol() {
        return "/";
    }
}