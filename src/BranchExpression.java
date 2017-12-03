import java.util.LinkedList;
import java.util.List;

public class BranchExpression extends AbstractCompoundExpression implements CompoundExpression {

    public BranchExpression (String str){
        super(str);
    }

    /**
     * Recursively flattens the expression as much as possible
     * throughout the entire tree. Specifically, in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    @Override
    public void flatten() {
        List<Expression> newChildren = new LinkedList<>(_children);
        for (Expression expr: _children){
            expr.flatten();
            if (expr.getClass().equals(this.getClass())) {
                int idx = newChildren.indexOf(expr);
                // set the parent of children expressions of the expression to be the parent of the expression
                for (Expression childrenToBeFlattened: ((BranchExpression) expr)._children) {
                    childrenToBeFlattened.setParent(this);
                }
                // add children of expr to this Expression
                newChildren.remove(expr);
                newChildren.addAll(idx, ((BranchExpression) expr)._children);
            }
        }
        _children = newChildren;
    }
}
