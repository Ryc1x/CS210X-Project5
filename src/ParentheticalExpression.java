import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ParentheticalExpression extends AbstractCompoundExpression{

    /**
     * Create the Expression with value "()"
     */
    public ParentheticalExpression () {
        super("()");
    }

    @Override
    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
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
