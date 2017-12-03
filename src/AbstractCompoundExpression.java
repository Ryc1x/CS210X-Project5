import java.util.LinkedList;
import java.util.List;

abstract public class AbstractCompoundExpression extends LiteralExpression implements CompoundExpression{

    List<Expression> _children = new LinkedList<>();

    /**
     * Create the Expression with the given value
     * @param str the value of Expression
     */
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
        sb.append("\n");
        for (Expression expr: _children){
            sb.append(expr.convertToString(indentLevel + 1));
        }
        return sb.toString();
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
        for (Expression expr: _children) {
            expr.flatten();
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
