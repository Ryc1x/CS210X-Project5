import javafx.scene.text.Font.*;

import java.util.LinkedList;
import java.util.List;

public class Test{
    // This class is used to test the functionality of other classes
    public static void main(String... args) throws ExpressionParseException{
        ExpressionParser parser = new SimpleExpressionParser();
        Expression expr = parser.parse("((4+74)*x+(63+a)*y+1+2+3+4+5*(6*7*8*9+(((e)))))",false);
        System.out.println(expr.convertToString(0));

        //System.out.println(expr.deepCopy().convertToString(0));
        //System.out.println(expr.getNode());

        List<String> list = new LinkedList<>();
        list.add("+");
        list.add(1,"·");


        list.forEach(str -> System.out.println(str.matches("^\\+|·$")));

    }
}
