import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

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
        Expression.indent(sb,indentLevel);
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
     * Set the JavaFX node with a red border.
     */
    public void addRedBorder (){
        ((HBox)_node).setBorder(RED_BORDER);
    }

    /**
     * Remove the red border of the JvavaFX node.
     */
    public void removeRedBorder (){
        ((HBox)_node).setBorder(NO_BORDER);
    }

    /**
     * Change the color of the node
     * @param color the color to be assigned to the node
     */
    public void changeColor (Color color){
        for (Node child: ((HBox) _node).getChildren()) {
            recursiveChangeColor(child, color);
        }
    }

    /**
     * Recursively change the color of the node
     * @param node the node need to be changing color
     * @param color the color to be assigned to the node
     */
    private void recursiveChangeColor (Node node, Color color) {
        if (node instanceof Label)
            ((Label) node).setTextFill(color);
        else {
            for (Node child: ((HBox) node).getChildren()) {
                recursiveChangeColor(child, color);
            }
        }
    }

    /**
     * Adds the specified expression as a child.
     * @param subexpression the child expression to add
     */
    public void addSubexpression (Expression subexpression){
        _children.add(subexpression);
    }

    /**
     * Return the children of the expression
     */
    public List<Expression> getChildren () {
        return _children;
    }
}
