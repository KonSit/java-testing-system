package expression;

public class Main {
    public static void main(String[] args) {
        Divide ex1 = new Divide(new Add(new Const(7), new Const(17)), new Const(4));
        Divide ex2 = new Divide(new Add(new Const(7), new Const(17)), new Const(4));
        System.out.println(ex1.toParenthesisedString());
        System.out.println(ex1.evaluate(1));
        System.out.println(ex2.toParenthesisedString());
        System.out.println(ex2.evaluate(1));
        System.out.println(ex1.equals(ex2));
    }
}
