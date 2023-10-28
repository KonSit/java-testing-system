package expression;

import java.util.Objects;

public abstract class BinaryExpression implements Expression {
    Expression leftExpression;
    Expression rightExpression;
    public  BinaryExpression (Expression leftExpression, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
    public abstract int evaluate(int variableValue);
    public abstract String operationSymbol();

    public boolean equals(Object obj) {
        return (obj instanceof Expression && (((Expression) obj).toParenthesisedString()).equals(toParenthesisedString()));
    }

    public String toParenthesisedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(leftExpression.toParenthesisedString());
        sb.append(" ");
        sb.append(operationSymbol());
        sb.append(" ");
        sb.append(rightExpression.toParenthesisedString());
        sb.append(")");
        return sb.toString();
    }
}
