import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
	    Pane _pane;
	    Expression _focus, _root, _copy;
	    Node _copiedNode;
	    List<Double> _possibleXValues;
	    HashMap<Double, Expression> _possibleConfigs;
        double _lastX, _lastY;
        boolean _isDragging;

	    MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
		    _focus = rootExpression_;
		    _root = rootExpression_;
		    _pane = pane_;
		}

		public void handle (MouseEvent event) {
            final double sceneX = event.getSceneX();
            final double sceneY = event.getSceneY();
            Expression nextFocus;

            // debugging:
            System.out.println("mouseX " + sceneX + " | mouseY " + sceneY);

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (_focus != _root) {
                    _focus.changeColor(Expression.GHOST_COLOR);
                    _copy = _focus.deepCopy();
                    _copiedNode = _copy.getNode();
                    _copiedNode.setLayoutX(_focus.getX());
                    _copiedNode.setLayoutY(_focus.getY());
                    _pane.getChildren().add(_copiedNode);

                    // todo: solve click-drag lvl issue
                }
			}
            else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && _copiedNode != null) {
                // helper: return the expression of nearest x

                _copiedNode.setTranslateX(_copiedNode.getTranslateX() + (sceneX - _lastX));
                _copiedNode.setTranslateY(_copiedNode.getTranslateY() + (sceneY - _lastY));
            }
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (_focus instanceof CompoundExpression)
                    nextFocus = getNextFocusExpression(sceneX, sceneY, (AbstractCompoundExpression) _focus);
                else
                    nextFocus = null;

                if (nextFocus != null) {
                    _focus.removeRedBorder();
                    _focus = nextFocus;
                    _focus.addRedBorder();
                } else {
                    _focus.removeRedBorder();
                    _focus = _root;
                }

			    _pane.getChildren().remove(_copiedNode);
                _focus.changeColor(Color.BLACK);
            }


            _lastX = sceneX;
            _lastY = sceneY;
		}

		private Expression getNextFocusExpression(double mouseX, double mouseY, AbstractCompoundExpression expression){
            Bounds bound = expression.getNode().localToScene(expression.getNode().getBoundsInLocal());
            double minX, maxX, minY, maxY;
            minY = bound.getMinY();
            maxY = bound.getMaxY();
            //System.out.println("minY " + minY + " | maxY " + maxY);
            //System.out.println("layoutX " + expression.getNode().getLayoutX() + "layoutY " + expression.getNode().getLayoutY());


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

        private HashMap<Double, Expression> generatePossibleExpressions (CompoundExpression parent) {
            HashMap<Double, Expression> possibleExpressions = new HashMap<>();
            for (int i = 0; i < parent.getChildren().size(); i++){
                CompoundExpression newParent = (CompoundExpression) parent.deepCopy();
                newParent.getChildren().remove(_focus);
                newParent.getChildren().add(i,_focus);
                newParent.clearNode();
                possibleExpressions.put(parent.getChildren().get(i).getX(), newParent);
            }
            return possibleExpressions;
        }

        private void generatePossibleXValues (CompoundExpression parent){
	        double initialX = parent.getX();
	        int idxOfFocus = ((HBox) parent.getNode()).getChildren().indexOf(_focus.getNode());
	        CompoundExpression modifiedParent = (CompoundExpression) parent.deepCopy();
            HBox modifiedNode = (HBox) modifiedParent.getNode();
	        modifiedNode.getChildren().remove(idxOfFocus);
            modifiedNode.getChildren().add(_focus.getNode()); // last possible position of the node.
            for (Node node : modifiedNode.getChildren()) {
                _possibleXValues.add(initialX + node.getLayoutX());
            }
            _possibleXValues.forEach(System.out::print);
        }

        private double getNearestConfigXValue (double mouseX) {
	        double min = Double.MAX_VALUE;
            for (double x: _possibleConfigs.keySet()) {
                min = Math.min(Math.abs(mouseX-x), min);
            }
            return min;
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
