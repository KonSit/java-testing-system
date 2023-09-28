package expression;

import base.expected.Expected;
import base.function.TernaryOperator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class TripleExpressionTest extends BasicExpressionTest<TripleExpressionTest.TestInfo> {

    public TripleExpressionTest() {
        testCases.add(new TestInfo((x, y, z) -> z, "z", () -> new Variable("z")));
        testCases.add(new TestInfo((x, y, z) -> y, "y", () -> new Variable("y")));
        testCases.add(new TestInfo((x, y, z) -> x, "x", () -> new Variable("x")));

        testCases.add(new TestInfo((x, y, z) -> x / y + z, "((x / y) + z)", () ->
                new Add(
                        new Divide(
                                new Variable("x"),
                                new Variable("y")
                        ),
                        new Variable("z")
                )));

        testCases.add(new TestInfo((x, y, z) -> -(x + 3 - z / (-(y + 15) * (x + z - 7))), "-(((x + 3) - (z / (-((y + 15)) * (x + (z - 7))))))", () ->
                new Negate(
                        new Subtract(
                                new Add(
                                        new Variable("x"),
                                        new Const(3)
                                ),
                                new Divide(
                                        new Variable("z"),
                                        new Multiply(
                                                new Negate(
                                                        new Add(
                                                                new Variable("y"),
                                                                new Const(15)
                                                        )
                                                ),
                                                new Add(
                                                        new Variable("x"),
                                                        new Subtract(
                                                                new Variable("z"),
                                                                new Const(7)
                                                        )
                                                )
                                        )
                                )
                        ))));
        //noinspection PointlessArithmeticExpression
        testCases.add(new TestInfo((x, y, z) -> -((y + x) * -(-(y + z) + ((z + z) * z)) - ((3 + (8 * x)) / (z / z))), "-((((y + x) * -((-((y + z)) + ((z + z) * z)))) - ((-(-3) + (8 * x)) / (z / z))))", () ->
                new Negate(
                        new Subtract(
                                new Multiply(
                                        new Add(
                                                new Variable("y"),
                                                new Variable("x")
                                        ),
                                        new Negate(
                                                new Add(
                                                        new Negate(
                                                                new Add(
                                                                        new Variable("y"),
                                                                        new Variable("z")
                                                                )
                                                        ),
                                                        new Multiply(
                                                                new Add(
                                                                        new Variable("z"),
                                                                        new Variable("z")
                                                                ),
                                                                new Variable("z")
                                                        )
                                                )
                                        )
                                ),
                                new Divide(
                                        new Add(
                                                new Negate(
                                                        new Const(-3)
                                                ),
                                                new Multiply(
                                                        new Const(8),
                                                        new Variable("x")
                                                )
                                        ),
                                        new Divide(
                                                new Variable("z"),
                                                new Variable("z")
                                        )
                                )
                        )
                )));
        testCases.add(new TestInfo((x, y, z) -> x + ((y + z - x) / (-x - 6 + 100) * z), "(x + ((((y + z) + -(x)) / -((x - (-6 - -100)))) * z))", () ->
                new Add(
                        new Variable("x"),
                        new Multiply(
                                new Divide(
                                        new Add(
                                                new Add(
                                                        new Variable("y"),
                                                        new Variable("z")
                                                ),
                                                new Negate(
                                                        new Variable("x")
                                                )
                                        ),
                                        new Negate(
                                                new Subtract(
                                                        new Variable("x"),
                                                        new Subtract(
                                                                new Const(-6),
                                                                new Const(-100)
                                                        )
                                                )
                                        )
                                ),
                                new Variable("z")
                        )
                )
        ));
        //noinspection UnnecessaryUnaryMinus
        testCases.add(new TestInfo((x, y, z) -> z / (10 + x) * -1 + -x / (-2 - y - 14) + (y + (x + -z)), "((((z / (10 + x)) * -1) + (-(x) / ((-2 - y) - 14))) + (y + (x + -(z))))", () ->
                new Add(
                        new Add(
                                new Multiply(
                                        new Divide(
                                                new Variable("z"),
                                                new Add(
                                                        new Const(10),
                                                        new Variable("x")
                                                )
                                        ),
                                        new Const(-1)
                                ),
                                new Divide(
                                        new Negate(
                                                new Variable("x")),
                                        new Subtract(
                                                new Subtract(
                                                        new Const(-2),
                                                        new Variable("y")),
                                                new Const(14)
                                        )
                                )
                        ),
                        new Add(
                                new Variable("y"),
                                new Add(
                                        new Variable("x"),
                                        new Negate(
                                                new Variable("z")
                                        )
                                )
                        )
                )
        ));
    }

    private static final List<Integer> checkRange = IntStream.concat(
            IntStream.rangeClosed(-10, 10),
            IntStream.concat(
                    IntStream.rangeClosed(Integer.MIN_VALUE, Integer.MIN_VALUE + 3),
                    IntStream.rangeClosed(Integer.MAX_VALUE - 3, Integer.MAX_VALUE)
            )
    ).boxed().toList();

    @Test
    public void testTripleValues() {
        for (final TestInfo test : testCases) {
            final TripleExpression expression = test.expressionSupplier().get();
            for (int i : checkRange) {
                for (int j : checkRange) {
                    for (int k : checkRange) {
                        testExpressionValue(test, expression, i, j, k);
                    }
                }
            }
        }
    }

    private void testExpressionValue(final TestInfo test, final TripleExpression expression, final int x, final int y, final int z) {
        Assert.assertEquals(
                "Evaluating triple-expression " + expression + " at point (" + x + ", " + y + ", " + z + ")",
                Expected.tryCall(() -> test.evaluation().apply(x, y, z)),
                Expected.tryCall(() -> expression.evaluate(x, y, z))
        );
    }

    static class TestInfo extends BasicTestInfo<TernaryOperator<Integer>, TripleExpression> {
        protected TestInfo(final TernaryOperator<Integer> evaluation,
                           final String fullBraced,
                           final Supplier<TripleExpression> expressionSupplier) {
            super(evaluation, fullBraced, expressionSupplier);
        }

        public UnaryOperator<Integer> simpleEvaluation() {
            return x -> evaluation.apply(x, x, x);
        }
    }
}
