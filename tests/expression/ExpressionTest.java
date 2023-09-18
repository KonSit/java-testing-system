package expression;

import base.expected.Expected;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExpressionTest {
    private record TestInfo(
            Function<Integer, Integer> evaluation,
            String fullBraced,
            Supplier<Expression> expressionSupplier) {
    }

    private final List<TestInfo> testCases;

    public ExpressionTest() {
        //noinspection PointlessArithmeticExpression
        testCases = List.of(
                new TestInfo(x -> 10, "10", () -> new Const(10)),
                new TestInfo(x -> -10, "-10", () -> new Const(-10)),
                new TestInfo(x -> 10, "-(-10)", () -> new Negate(new Const(-10))),
                new TestInfo(x -> 10, "-(-(10))", () -> new Negate(new Negate(new Const(10)))),
                new TestInfo(x -> x, "x", () -> new Variable("x")),
                new TestInfo(x -> -x, "-(x)", () -> new Negate(new Variable("x"))),
                new TestInfo(x -> x, "-(-(x))", () -> new Negate(new Negate(new Variable("x")))),
                new TestInfo(x -> x + 3, "(x + 3)",
                        () -> new Add(
                                new Variable("x"),
                                new Const(3)
                        )),
                new TestInfo(x -> 3 + x, "(3 + x)",
                        () -> new Add(
                                new Const(3),
                                new Variable("x")
                        )),
                new TestInfo(x -> x - 1, "(x - 1)",
                        () -> new Subtract(
                                new Variable("x"),
                                new Const(1)
                        )),
                new TestInfo(x -> 5 - x, "(5 - x)",
                        () -> new Subtract(
                                new Const(5),
                                new Variable("x")
                        )),
                new TestInfo(x -> 7 * x, "(7 * x)",
                        () -> new Multiply(
                                new Const(7),
                                new Variable("x")
                        )),
                new TestInfo(x -> 1 / x, "(1 / x)",
                        () -> new Divide(
                                new Const(1),
                                new Variable("x")
                        )),
                new TestInfo(x -> 1 + 2 + 3, "((1 + 2) + 3)",
                        () -> new Add(
                                new Add(
                                        new Const(1),
                                        new Const(2)
                                ),
                                new Const(3)
                        )),
                new TestInfo(x -> 1 + 2 + 3, "(1 + (2 + 3))",
                        () -> new Add(
                                new Const(1),
                                new Add(
                                        new Const(2),
                                        new Const(3)
                                )
                        )),
                new TestInfo(x -> (1 - 2) - 3, "((1 - 2) - 3)",
                        () -> new Subtract(
                                new Subtract(
                                        new Const(1),
                                        new Const(2)
                                ),
                                new Const(3)
                        )),
                new TestInfo(x -> 1 - (2 - 3), "(1 - (2 - 3))",
                        () -> new Subtract(
                                new Const(1),
                                new Subtract(
                                        new Const(2),
                                        new Const(3)
                                )
                        )),
                new TestInfo(x -> 2 * 3 * 4, "((2 * 3) * 4)",
                        () -> new Multiply(
                                new Multiply(
                                        new Const(2),
                                        new Const(3)
                                ),
                                new Const(4)
                        )),
                new TestInfo(x -> 2 * (3 * 4), "(2 * (3 * 4))",
                        () -> new Multiply(
                                new Const(2),
                                new Multiply(
                                        new Const(3),
                                        new Const(4)
                                )
                        )),
                new TestInfo(x -> (15 / 3) / 2, "((15 / 3) / 2)",
                        () -> new Divide(
                                new Divide(
                                        new Const(15),
                                        new Const(3)
                                ),
                                new Const(2)
                        )),
                new TestInfo(x -> 15 / (3 / 2), "(15 / (3 / 2))",
                        () -> new Divide(
                                new Const(15),
                                new Divide(
                                        new Const(3),
                                        new Const(2)
                                )
                        ))
        );
    }

    @Test
    public void testValues() {
        for (final TestInfo test : testCases) {
            final Expression expression = test.expressionSupplier.get();
            for (int i = -10; i <= 10; i++) {
                testExpressionValue(test, expression, i);
            }
            for (int i = 0; i <= 3; i++) {
                testExpressionValue(test, expression, Integer.MAX_VALUE - i);
            }
            for (int i = 0; i <= 3; i++) {
                testExpressionValue(test, expression, Integer.MIN_VALUE + i);
            }
        }
    }

    private void testExpressionValue(final TestInfo test, final Expression expression, final int arg) {
        Assert.assertEquals(
                "Evaluating expression " + expression + " at point " + arg,
                Expected.tryCall(() -> test.evaluation.apply(arg)),
                Expected.tryCall(() -> expression.evaluate(arg))
        );
    }

    @Test
    public void testParenthesisedString() {
        for (final TestInfo test : testCases) {
            final Expression expression = test.expressionSupplier.get();
            Assert.assertEquals(
                    "Testing parenthesised string for expression " + expression,
                    test.fullBraced,
                    expression.toParenthesisedString()
            );
        }
    }

    @Test
    public void testEquals() {
        for (int i = 0; i < testCases.size(); i++) {
            final TestInfo test = testCases.get(i);
            final Expression expression = test.expressionSupplier.get();
            final Expression expressionCopy = test.expressionSupplier.get();

            Assert.assertEquals(
                    "Testing equality of " + expression + " and " + expressionCopy,
                    expression,
                    expressionCopy
            );
            Assert.assertEquals(
                    "Testing equality of " + expressionCopy + " and " + expression,
                    expressionCopy,
                    expression
            );
            Assert.assertEquals(
                    "Testing hashCode equality for " + expression + " and " + expression,
                    expression.hashCode(),
                    expressionCopy.hashCode()
            );
            for (int j = 0; j < i; j++) {
                final Expression otherExpression = testCases.get(j).expressionSupplier.get();
                Assert.assertNotEquals("Testing not equality of " + expression + " and " + otherExpression,
                        expression,
                        otherExpression);
                Assert.assertNotEquals("Testing not equality of " + otherExpression + " and " + expression,
                        otherExpression,
                        expression);
            }
        }
    }
}
