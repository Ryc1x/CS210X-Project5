import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.LinkedList;
import java.util.List;

public class BranchExpression extends AbstractCompoundExpression implements CompoundExpression {

    /**
     * Create the Expression with the given value
     * @param str the value of Expression
     */
    public BranchExpression (String str){
        super(str);
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    @Override
    public Expression deepCopy() {
        CompoundExpression copy = new BranchExpression(_value);
        for (Expression expr: _children) {
            Expression child = expr.deepCopy();
            copy.addSubexpression(child);
            child.setParent(copy);
        }
        return copy;
    }

    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
    @Override
    public Node getNode (){
        if (_node == null) {
            HBox box = new HBox();
            Label label = new Label();
            label.setFont(DEFAULT_FONT);
            box.getChildren().add(_children.get(0).getNode()); // add the first LiteralExpression
            for (int i = 1; i < _children.size(); i++) {
                // put "+" or "*" between numbers/letters
                label = new Label(_value);
                label.setFont(DEFAULT_FONT);
                box.getChildren().add(label);
                box.getChildren().add(_children.get(i).getNode());
            }
            _node = box;
        }
        return _node;
    }

    /**
     * Update the JavaFX node according to the Expression structure
     * @param focus the focus need to be updated
     */
    public void updateNode (Expression focus, int configIndex, int lastConfigIndex) {
        // update the expression tree first
        _children.remove(lastConfigIndex);
        _children.add(configIndex, focus);

        // remove all signs - easier to modify the nodes
        List<Node> nodes = ((HBox)_node).getChildren();
        List<Node> signs = new LinkedList<>();
        for (Node node : nodes) {
            // check if a node is "+" or "·"
            if (node instanceof Label){
                if (((Label) node).getText().matches("^\\+|·$"))
                    signs.add(node);
            }
        }
        nodes.removeAll(signs);

        // change the place of the focus node
        nodes.remove(focus.getNode());
        nodes.add(configIndex, focus.getNode());

        // add all signs back to the nodes
        for (int i = nodes.size(); i > 1 ; i--) {
            Label label = new Label(_value);
            label.setFont(DEFAULT_FONT);
            nodes.add(i-1, label);
        }
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
