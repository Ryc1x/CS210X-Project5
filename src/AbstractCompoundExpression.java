import java.util.LinkedList;
import java.util.List;

abstract public class AbstractCompoundExpression extends LiteralExpression implements CompoundExpression{

    List<Expression> _children = new LinkedList<>();

    public AbstractCompoundExpression (String str){
        super(str);
    }

    /**
     * Creates a String representation by recursively printing out (using indentation) the
     * tree represented by this expression, starting at the specified indentation level.
     * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
     * @return a String representation of the expression tree.
     */
    @Override
    public String convertToString(int indentLevel) {
        StringBuffer sb = new StringBuffer("");
        indent(sb,indentLevel);
        sb.append(_value);
        for (Expression expr: _children){
            sb.append("\n");
            sb.append(expr.convertToString(indentLevel + 1));
        }
        return sb.toString();
    }

    @Override
    public void flatten() {
        List<Expression> newChildren = new LinkedList<>(_children);
        for (Expression expr: _children){
            expr.flatten();
            if (expr.getClass().equals(this.getClass())) { // todo fix for parenths and literal expressions
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

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    @Override
    public Expression deepCopy() {
        //todo check if the method is implemented correctly
        CompoundExpression copy = new BranchExpression(_value);
        for (Expression expr: _children) {
            Expression child = expr.deepCopy();
            copy.addSubexpression(child);
            child.setParent(copy);
        }
        return copy;
    }


    /**
     * Adds the specified expression as a child.
     * @param subexpression the child expression to add
     */
    public void addSubexpression (Expression subexpression){
        _children.add(subexpression);
    }
}
