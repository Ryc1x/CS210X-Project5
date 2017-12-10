import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
	    Expression _focus, _root;
		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
		    _focus = rootExpression_;
		    _root = rootExpression_;
		}

		public void handle (MouseEvent event) {
            final double sceneX = event.getSceneX();
            final double sceneY = event.getSceneY();
            Expression nextFocus;

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			    nextFocus =getNextFocusExpression(sceneX, sceneY, (AbstractCompoundExpression) _focus);
			    if (nextFocus != null){
                    ((Pane) _focus.getNode()).setBorder(Expression.NO_BORDER);
                    _focus = nextFocus;
                    ((Pane) _focus.getNode()).setBorder(Expression.RED_BORDER);
                }
                else{
                    ((Pane) _focus.getNode()).setBorder(Expression.NO_BORDER);
                    _focus = _root;

                }
			}
            else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            }
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            }
		}

		private Expression getNextFocusExpression(double mouseX, double mouseY, AbstractCompoundExpression expression){
            Bounds bound = expression.getNode().localToScene(expression.getNode().getBoundsInLocal());
            double minX, maxX, minY, maxY;
            minY = bound.getMinY();
            maxY = bound.getMaxY();
            System.out.println("mouseX " + mouseX + " | mouseY " + mouseY);
            System.out.println("minY " + minY + " | maxY " + maxY);
            System.out.println("layoutX " + expression.getNode().getLayoutX() + "layoutY " + expression.getNode().getLayoutY());


            if (mouseY < minY || mouseY > maxY)
                return null;
		    for (Expression expr: expression._children) {
                bound = expr.getNode().localToScene(expr.getNode().getBoundsInLocal());
                minX = bound.getMinX();
                maxX = bound.getMaxX();
                if (mouseX > minX && mouseX < maxX)
                    return expr;
            }
		    return null;
        }
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(e -> {
            // Try to parse the expression
            try {
                // Success! Add the expression's Node to the expressionPane
                final Expression expression = expressionParser.parse(textField.getText(), true);
                System.out.println(expression.convertToString(0));
                expressionPane.getChildren().clear();
                expressionPane.getChildren().add(expression.getNode());
                expression.getNode().setLayoutX(WINDOW_WIDTH/4);
                expression.getNode().setLayoutY(WINDOW_HEIGHT/3);

                // If the parsed expression is a CompoundExpression, then register some callbacks
                if (expression instanceof CompoundExpression) {
                    ((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
                    final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
                    expressionPane.setOnMousePressed(eventHandler);
                    expressionPane.setOnMouseDragged(eventHandler);
                    expressionPane.setOnMouseReleased(eventHandler);
                }
            } catch (ExpressionParseException epe) {
                // If we can't parse the expression, then mark it in red
                textField.setStyle("-fx-text-fill: red");
            }
        });
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}
