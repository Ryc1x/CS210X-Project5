import java.util.LinkedList;

public class Test {
    public static void main(String... args){
        CompoundExpression[] compoundExpressions = new CompoundExpression[] {new BranchExpression("aaa"), new BranchExpression("bbb"), new BranchExpression("ccc")};
        Expression[] expressions = new Expression[] {new LiteralExpression("a"), new LiteralExpression("b"), new LiteralExpression("c"), new LiteralExpression("d")};
        compoundExpressions[0].addSubexpression(compoundExpressions[1]);
        compoundExpressions[0].addSubexpression(compoundExpressions[2]);
        compoundExpressions[1].addSubexpression(expressions[0]);
        compoundExpressions[1].addSubexpression(expressions[1]);
        compoundExpressions[2].addSubexpression(expressions[2]);
        compoundExpressions[2].addSubexpression(expressions[3]);

        System.out.println(compoundExpressions[0].convertToString(0));
        System.out.println(compoundExpressions[1].convertToString(0));
        System.out.println(compoundExpressions[2].convertToString(0));
        System.out.println(expressions[0].convertToString(0));
        System.out.println(expressions[1].convertToString(0));
        System.out.println(expressions[2].convertToString(0));
        System.out.println(expressions[3].convertToString(0));


        LinkedList<String> strings = new LinkedList<>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        strings.add("e");
        for (String str: strings){
            // java.util.ConcurrentModificationException
            System.out.println(str);
            if (str.equals("c")){
                strings.add(1,"000");
                strings.add(1,"000");
            }
        }

    }
}
