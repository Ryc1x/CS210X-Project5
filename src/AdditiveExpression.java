import java.util.LinkedList;
import java.util.List;

public class AdditiveExpression extends BranchExpression {

    public AdditiveExpression (){
        super("+");
    }

    @Override
    public void flatten() {
        List<Expression> newChildren = new LinkedList<>(_children);
        for (Expression expr: _children){
            expr.flatten();
            if (expr instanceof AdditiveExpression) {
                int idx = newChildren.indexOf(expr);
                // set the parent of children expressions of the expression to be the parent of the expression
                for (Expression childrenToBeFlattened: ((BranchExpression) expr)._children) {
                    childrenToBeFlattened.setParent(expr.getParent());
                }
                newChildren.remove(expr);
                newChildren.addAll(idx, ((BranchExpression) expr)._children);
            }
        }
    }
}
