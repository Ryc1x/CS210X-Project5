import java.util.LinkedList;
import java.util.List;

public class BranchExpression extends AbstractCompoundExpression implements CompoundExpression {

    public BranchExpression (String str){
        super(str);
    }

    @Override
    public void flatten() {
        // todo: need to test the correctness of the method
        List<Expression> newChildren = new LinkedList<>(_children);
        for (Expression expr: _children){
            expr.flatten();
            if (expr.getClass().equals(this.getClass())) {
                int idx = newChildren.indexOf(expr);
                // set the parent of children expressions of the expression to be the parent of the expression
                for (Expression childrenToBeFlattened: ((BranchExpression) expr)._children) {
                    childrenToBeFlattened.setParent(this);
                }
                newChildren.remove(expr);
                newChildren.addAll(idx, ((BranchExpression) expr)._children);
            }
        }
    }
}
