package expression;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BasicExpressionTest<I extends BasicExpressionTest.BasicTestInfo<?, ?>> {
    protected final List<I> testCases = new ArrayList<>();
    protected static class BasicTestInfo<F, E extends ToParenthesisedString> {
        protected final F evaluation;
        protected final String fullBraced;
        protected final Supplier<E> expressionSupplier;

        protected BasicTestInfo(
                F evaluation,
                String fullBraced,
                Supplier<E> expressionSupplier) {
            this.evaluation = evaluation;
            this.fullBraced = fullBraced;
            this.expressionSupplier = expressionSupplier;
        }

        public F evaluation() {
            return evaluation;
        }

        public String fullBraced() {
            return fullBraced;
        }

        public Supplier<E> expressionSupplier() {
            return expressionSupplier;
        }
    }


    @Test
    public void testParenthesisedString() {
        for (final BasicTestInfo<?, ?> test : testCases) {
            final ToParenthesisedString expression = test.expressionSupplier().get();
            Assert.assertEquals(
                    "Testing parenthesised string for expression " + expression,
                    test.fullBraced(),
                    expression.toParenthesisedString()
            );
        }
    }

    @Test
    public void testEquals() {
        for (int i = 0; i < testCases.size(); i++) {
            final BasicTestInfo<?, ?> test = testCases.get(i);
            final Object expression = test.expressionSupplier().get();
            final Object expressionCopy = test.expressionSupplier().get();

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
                final Object otherExpression = testCases.get(j).expressionSupplier().get();
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
