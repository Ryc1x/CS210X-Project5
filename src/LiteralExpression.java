import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class LiteralExpression implements Expression{

    private CompoundExpression _parent;
    Node _node;
    String _value;

    /**
     * Create the Expression with given value
     * @param str the value of Expression
     */
    public LiteralExpression (String str){
        _value = str;
    }

    /**
     * Returns the expression's parent.
     * @return the expression's parent
     */
    public CompoundExpression getParent (){
        return _parent;
    }

    /**
     * Sets the parent be the specified expression.
     * @param parent the CompoundExpression that should be the parent of the target object
     */
    public void setParent (CompoundExpression parent){
        _parent = parent;
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    public Expression deepCopy (){
        return new LiteralExpression(_value);
    }

    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
    public Node getNode (){
        if (_node == null){
            Label label = new Label(_value);
            label.setFont(DEFAULT_FONT);
            _node = label;
        }
        return _node;
    }

    /**
     * Recursively flattens the expression as much as possible
     * throughout the entire tree. Specifically, in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    public void flatten (){
        // Don't need to do anything
    }

    /**
     * Return the absolute x coordinate of the node.
     * @return the absolute x coordinate of the node.
     */
    public double getX (){
        if (_parent != null)
            return _node.getLayoutX() + _parent.getX();
        else
            return _node.getLayoutX() ;
    }

    /**
     * Return the absolute x coordinate of the node.
     * @return the absolute x coordinate of the node.
     */
    public double getY (){
        if (_parent != null)
            return _node.getLayoutY() + _parent.getY();
        else
            return _node.getLayoutY();
    }

    /**
     * Set the JavaFX node with a red border.
     */
    public void addRedBorder (){
        ((Label)_node).setBorder(RED_BORDER);
    }

    /**
     * Remove the red border of the JvavaFX node.
     */
    public void removeRedBorder (){
        ((Label)_node).setBorder(NO_BORDER);
    }

    /**
     * Change the color of the node
     * @param color the color to be assigned to the node
     */
    public void changeColor (Color color){
        ((Label) _node).setTextFill(color);
    }

    /**
     * Creates a String representation by recursively printing out (using indentation) the
     * tree represented by this expression, starting at the specified indentation level.
     * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
     * @return a String representation of the expression tree.
     */
    public String convertToString (int indentLevel){
        StringBuffer sb = new StringBuffer("");
        Expression.indent(sb,indentLevel);
        sb.append(_value);
        sb.append("\n");
        return sb.toString();
    }
}
