package expression.parser;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParserErrorTest extends ParserTest {
    private void checkError(final String data, final String desc) {
        try {
            parse(data);
            Assert.fail("Parsing \"\"\"" + data + "\"\"\" is expected to throw IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            System.err.println(desc + " [" + data + "]: " + e.getMessage());
        }
    }

    @Test
    public void testError() {
        checkError("x + y min", "no right argument");
        checkError("x +  min z", "no middle argument");
        checkError(" + y min z", "no left argument");

        checkError("1 + (x + y min) * 2", "no right argument (in parentheses)");
        checkError("1 + (x +  min z) * 2", "no middle argument (in parentheses)");
        checkError("1 + ( + y min z) * 2", "no left argument (in parentheses)");

        checkError("1 + 2)", "no opening parenthesis");
        checkError("(1 + 2", "no closing parenthesis");

        checkError("$ x + y", "unknown symbol at the start");
        checkError("x % + y", "unknown symbol at the middle 1");
        checkError("x + # y", "unknown symbol at the middle 2");
        checkError("x + y `", "unknown symbol at the end");

        checkError(Long.toString(Integer.MAX_VALUE + 1L), "constant overflow up");
        checkError(Long.toString(Integer.MIN_VALUE - 1L), "constant overflow down");

        checkError("min", "bare operator 1");
        checkError("+", "bare operator 2");

        checkError("()", "empty parenthesis 1");
        checkError("(())", "empty parenthesis 2");

        checkError("( max )", "bare operator in parenthesis 1");
        checkError("( * )", "bare operator in parenthesis 2");

        checkError("10 20", "number following number");
        checkError("z 0", "number following variable");
        checkError("5 y", "variable following number");
        checkError("x y", "variable following variable");

        checkError("x * min y", "operator following operator 1");
        checkError("xmaxminz", "operator following operator 2");

        checkError("x m      y", "underwritten min/max 1");
        checkError("x mi     y", "underwritten min 2");
        checkError("x m n    y", "underwritten min 3");
        checkError("x ma     y", "underwritten max 2");
        checkError("x m x    y", "underwritten max 3");
    }
}
