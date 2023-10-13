package expression.parser;

import expression.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ParserTest {
    private static final String whitespaces = IntStream.rangeClosed(0, Character.MAX_VALUE)
            .filter(Character::isWhitespace)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    private static final Pattern tokenPattern = Pattern.compile("max|min|x|y|z|-?\\d++|\\*|/|\\+|-|\\(|\\)");
    private final Random random = new Random(8572957661108402L);

    private static TripleExpression parse(final String data) {
        return new ArithmeticParser(new StringCharSource(data)).parse();
    }

    private static TripleExpression parseExpr(final String expr) {
        // Yes, do it, copy this!!
        // final List<String> tok = tokenPattern.matcher(expr).results().map(MatchResult::group).toList();
        final Deque<TripleExpression> res = new ArrayDeque<>();
        final Deque<String> op = new ArrayDeque<>();
        final boolean[] p = new boolean[]{true};
        final Map<String, Integer> pr = Map.of(
                "(", 0,
                ")", 1,
                "min", 2,
                "max", 2,
                "+", 3,
                "-", 3,
                "*", 4,
                "/", 4,
                "@", 100
        );
        final Consumer<String> drop = i -> {
            while (!op.isEmpty() && pr.get(op.getLast()) >= pr.get(i)) {
                switch (op.pollLast()) {
                    case "min" -> {
                        final TripleExpression r = res.pollLast(), l = res.pollLast();
                        res.addLast(new Min(l, r));
                    }
                    case "max" -> {
                        final TripleExpression r = res.pollLast(), l = res.pollLast();
                        res.addLast(new Max(l, r));
                    }
                    case "+" -> {
                        final TripleExpression r = res.pollLast(), l = res.pollLast();
                        res.addLast(new Add(l, r));
                    }
                    case "-" -> {
                        final TripleExpression r = res.pollLast(), l = res.pollLast();
                        res.addLast(new Subtract(l, r));
                    }
                    case "*" -> {
                        final TripleExpression r = res.pollLast(), l = res.pollLast();
                        res.addLast(new Multiply(l, r));
                    }
                    case "/" -> {
                        final TripleExpression r = res.pollLast(), l = res.pollLast();
                        res.addLast(new Divide(l, r));
                    }
                    case "@" -> {
                        final TripleExpression r = res.pollLast();
                        res.addLast(new Negate(r));
                    }
                    default -> throw new AssertionError("TEST ERROR: Unknown op");
                }
            }
        };
        tokenPattern.matcher(expr).results().map(MatchResult::group).forEachOrdered(t -> {
            if (p[0]) {
                switch (t) {
                    case "x", "y", "z" -> {
                        res.add(new Variable(t));
                        p[0] = !p[0];
                    }
                    case "(" -> op.add("(");
                    case "-" -> op.add("@");
                    default -> {
                        res.add(new Const(Integer.parseInt(t)));
                        p[0] = !p[0];
                    }
                }
            } else {
                drop.accept(t);
                switch (t) {
                    case ")" -> op.pollLast();
                    default -> {
                        op.addLast(t);
                        p[0] = !p[0];
                    }
                }
            }
        });
        drop.accept(")");
        return res.pollLast();
    }

    private static void checkParse(final TripleExpression expected, final String data) {
        System.err.print("Parsing \"\"\"");
        for (int i = 0; i < data.length(); i++) {
            final int type = Character.getType(data.charAt(i));
            if (type == Character.CONTROL || type == Character.FORMAT || type == Character.PRIVATE_USE || type == Character.SURROGATE
                    || type == Character.LINE_SEPARATOR || type == Character.PARAGRAPH_SEPARATOR || type == Character.SPACE_SEPARATOR) {
                System.err.print(switch (data.charAt(i)) {
                    case ' ' -> " ";
                    case '\n' -> "\\n";
                    case '\r' -> "\\r";
                    case '\0' -> "\\0";
                    case '\t' -> "\\t";
                    case '\"' -> "\\\"";
                    case '\'' -> "\\'";
                    case '\f' -> "\\f";
                    case '\b' -> "\\b";
                    default -> {
                        final String hexStr = Integer.toHexString(data.charAt(i)).toUpperCase();
                        yield "\\u" + "0".repeat(4 - hexStr.length()) + hexStr;
                    }
                });
            } else {
                System.err.print(data.charAt(i));
            }
        }
        System.err.println("\"\"\"...");
        Assert.assertEquals(expected, parse(data));
    }

    private static void checkParse(final String data) {
        checkParse(parseExpr(data), data);
    }

    private String randomWhitespaces(final int count) {
        return random.ints(count, 0, whitespaces.length())
                .map(whitespaces::charAt)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    public void testInteger() {
        checkParse(new Const(123), "123");
        checkParse(new Const(0), "0");
        checkParse(new Const(0), "00000");
        checkParse(new Const(0), "-0");
        checkParse(new Const(123), "00123");
        checkParse(new Const(2147483647), "02147483647");
        checkParse(new Const(-1), "-001");
        checkParse(new Const(-2147483648), "-002147483648");

        checkParse(new Const(-47), randomWhitespaces(3) + "-47" + randomWhitespaces(3));
        checkParse(new Const(2147483647), randomWhitespaces(5) + "02147483647");
    }

    @Test
    public void testVariable() {
        checkParse(new Variable("x"), "x");
        checkParse(new Variable("y"), "(y)");
        checkParse(new Variable("z"), "(((z)))");

        checkParse(new Variable("x"), "(" + randomWhitespaces(3) + "x)");
        checkParse(new Variable("y"), "((" + randomWhitespaces(3) + "(y)" + randomWhitespaces(1) + "))");
        checkParse(new Variable("z"), "((z)" + randomWhitespaces(4) + ")");
    }

    @Test
    public void testSimple() {
        checkParse(new Add(new Variable("x"), new Const(7)), "x + 7");
        checkParse(new Subtract(new Const(2), new Variable("y")), "2-y");
        checkParse(new Multiply(new Const(7), new Variable("z")), " \t 7*\t\tz");
        checkParse(new Divide(new Variable("x"), new Negate(new Variable("z"))), " x/-z ");
        checkParse(new Min(new Variable("y"), new Variable("z")), " ( y min z ) ");
        checkParse(new Max(new Const(15), new Const(-3)), "15max-3");

        checkParse(
                new Add(
                        new Variable("x"),
                        new Multiply(
                                new Variable("y"),
                                new Variable("z")
                        )
                ),
                "x+y*z"
        );
        checkParse(
                new Min(
                        new Variable("x"),
                        new Subtract(
                                new Variable("y"),
                                new Negate(
                                        new Variable("z")
                                )
                        )
                ),
                "xminy--z"
        );
        checkParse(
                new Max(
                        new Divide(
                                new Variable("y"),
                                new Variable("z")
                        ),
                        new Variable("x")
                ),
                "y/zmaxx"
        );
    }

    private String generateExpr(double toOp, double toBr) {
        StringBuilder sb = new StringBuilder(randomWhitespaces(3));
        generateE(sb, toOp, toBr);
        sb.append(randomWhitespaces(3));
        return sb.toString();
    }

    private void generateE(StringBuilder sb, double toOp, double toBr) {
        double rnd = random.nextDouble(0, 1);
        if (rnd < toOp) {
            generateE(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append("min");
            sb.append(randomWhitespaces(3));
            generateM(sb, toOp, toBr);
        } else if (rnd < toOp * 2) {
            generateE(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append("max");
            sb.append(randomWhitespaces(3));
            generateM(sb, toOp, toBr);
        } else {
            generateM(sb, toOp, toBr);
        }
    }

    private void generateM(StringBuilder sb, double toOp, double toBr) {
        double rnd = random.nextDouble(0, 1);
        if (rnd < toOp) {
            generateM(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append("+");
            sb.append(randomWhitespaces(3));
            generateS(sb, toOp, toBr);
        } else if (rnd < toOp * 2) {
            generateM(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append("-");
            sb.append(randomWhitespaces(3));
            generateS(sb, toOp, toBr);
        } else {
            generateS(sb, toOp, toBr);
        }
    }

    private void generateS(StringBuilder sb, double toOp, double toBr) {
        double rnd = random.nextDouble(0, 1);
        if (rnd < toOp) {
            generateS(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append("*");
            sb.append(randomWhitespaces(3));
            generateA(sb, toOp, toBr);
        } else if (rnd < toOp * 2) {
            generateS(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append("/");
            sb.append(randomWhitespaces(3));
            generateA(sb, toOp, toBr);
        } else {
            generateA(sb, toOp, toBr);
        }
    }

    private void generateA(StringBuilder sb, double toOp, double toBr) {
        double rnd = random.nextDouble(0, 1);
        if (rnd < toBr) {
            sb.append('(');
            sb.append(randomWhitespaces(3));
            generateE(sb, toOp, toBr);
            sb.append(randomWhitespaces(3));
            sb.append(')');
        } else if (rnd < (1 - toOp) / 2) {
            sb.append(random.nextInt());
        } else {
            sb.append(switch (random.nextInt(3)) {
                case 0 -> "x";
                case 1 -> "y";
                default -> "z";
            });
        }
    }

    @Test
    public void testRandom1() {
        for (int i = 0; i < 5000; i++) {
            checkParse(generateExpr(.05, .05));
        }
    }

    @Test
    public void testRandom2() {
        for (int i = 0; i < 5000; i++) {
            checkParse(generateExpr(.15, .2));
        }
    }

    @Test
    public void testRandom3() {
        for (int i = 0; i < 5000; i++) {
            checkParse(generateExpr(.3, .05));
        }
    }

    @Test
    public void testRandom4() {
        for (int i = 0; i < 5000; i++) {
            checkParse(generateExpr(.1, .35));
        }
    }

}
