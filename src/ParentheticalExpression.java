import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ParentheticalExpression extends AbstractCompoundExpression {

    /**
     * Create the Expression with value "()"
     */
    public ParentheticalExpression() {
        super("()");
    }


    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
    @Override
    public Expression deepCopy() {
        CompoundExpression copy = new ParentheticalExpression();
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
            Label label = new Label("(");
            label.setFont(DEFAULT_FONT);
            box.getChildren().add(label);
            for (Expression expr : _children) {
                box.getChildren().add(expr.getNode());
            }
            label = new Label(")");
            label.setFont(DEFAULT_FONT);
            box.getChildren().add(label);
            _node = box;
        }
        return _node;
    }
}
