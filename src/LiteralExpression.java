import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class LiteralExpression implements Expression{

    private CompoundExpression _parent;
    Node _node;
    String _value;

    final Font DEFAULT_FONT = new Font("Cambria Math", 24.0);


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
            _node = new HBox(label);
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
