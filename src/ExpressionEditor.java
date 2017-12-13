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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
	    private Pane _pane;
        private Expression _focus, _root, _copy;
        private Node _copiedNode;
        private List<Double> _possibleXValues = new LinkedList<>();
        private double _lastX, _lastY;
        private int _configIndex, _lastConfigIndex;
        private boolean _isDragging, _focusCleared;

	    MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
	        _focus = rootExpression_;
		    _root = rootExpression_;
		    _pane = pane_;
		}

		public void handle (MouseEvent event) {
            final double sceneX = event.getSceneX();
            final double sceneY = event.getSceneY();
            Expression nextFocus;

			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (_focus != _root) {
                    // get a copy of the _focus for dragging
                    setCopy();
                    _configIndex = _focus.getParent().getChildren().indexOf(_focus);
                    _lastConfigIndex = _configIndex;
                    if (!checkOnNode(_focus.getNode(), sceneX, sceneY))
                        clearFocus(); // clear focus if mouse is not pressed on the focus
                }
                _isDragging = false;
			}
            else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
                if (_focus != _root && _copiedNode != null){
                    // if there is a focus, and we are trying to drag the node
                    _configIndex = getNearestConfigIndex();
                    if (_configIndex != _lastConfigIndex) { // detect an update in config
                        updateConfig();
                        _lastConfigIndex = _configIndex;
                    }
                    // update the copied node's coordinate
                    _copiedNode.setTranslateX(_copiedNode.getTranslateX() + (sceneX - _lastX));
                    _copiedNode.setTranslateY(_copiedNode.getTranslateY() + (sceneY - _lastY));
                }
                _isDragging = true;
            }
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			    if (_copiedNode != null) { // if there is a copied node, remove it and reset the color of the focus
                    _pane.getChildren().remove(_copiedNode);
                    _focus.changeColor(Color.BLACK);
                    if (_isDragging) // print out the expression after a "drop"
                        System.out.println(_root.convertToString(0));
                }
                if (_lastX == sceneX && _lastY == sceneY && !_isDragging && !_focusCleared){
                    // if this is a mouse click, try to find the next focus
                    nextFocus = getNextFocusExpression(sceneX, sceneY, _focus);
                    if (nextFocus != null)  // if there is a next focus, dive into it
                        setFocus(nextFocus);
                    else
                        clearFocus();
                }
                _focusCleared = false;
            }

            // update last config, x, and y.
            _lastConfigIndex = _configIndex;
            _lastX = sceneX;
            _lastY = sceneY;
		}

        /**
         * Set next focus expression to focus
         * @param nextFocus the next focus
         */
		private void setFocus (Expression nextFocus) {
            _focus.removeRedBorder();
            _focus = nextFocus;
            _focus.addRedBorder();
            generatePossibleXValues(_focus.getParent());
        }

        /**
         * Clear the focus and set the focus back to the root
         * (but don't show the border on the Application)
         */
        private void clearFocus () {
            _focus.removeRedBorder();
            _focus = _root;
            _possibleXValues.clear();
            _focusCleared = true;
        }

        /**
         * Set a copy of the current focus for dragging,
         * and change the color of focus to GHOST_COLOR
         */
		private void setCopy (){
            _focus.changeColor(Expression.GHOST_COLOR);
            _copy = _focus.deepCopy();
            _copiedNode = _copy.getNode();
            _copiedNode.setLayoutX(_focus.getX());
            _copiedNode.setLayoutY(_focus.getY());
            _pane.getChildren().add(_copiedNode);
        }

        /**
         * Try to get the next focus
         * @param mouseX the x axis of the mouse click
         * @param mouseY the y axis of the mouse click
         * @param expression the current focus expression
         * @return the next focus expression (null if can't find one)
         */
		private Expression getNextFocusExpression(double mouseX, double mouseY, Expression expression){
            if (expression instanceof CompoundExpression){
                for (Expression expr: ((CompoundExpression) expression).getChildren()) {
                    if (checkOnNode(expr.getNode(), mouseX, mouseY))
                        return expr;
                }
            }
            return null;
        }

        /**
         * Generate possible x axis of focus for all possible configurations
         * @param parent the parent of the current focus
         */
        private void generatePossibleXValues (CompoundExpression parent){
	        _possibleXValues.clear();
	        int configurations = parent.getChildren().size();
	        int indexOfFocus = parent.getChildren().indexOf(_focus);

            for (int i = 0; i < configurations; i++) {
                if (i <= indexOfFocus){
                    _possibleXValues.add(parent.getChildren().get(i).getX());
                }
                else {
                    _possibleXValues.add(parent.getChildren().get(i).getX() - _focus.getNode().getLayoutBounds().getWidth()
                            + parent.getChildren().get(i).getNode().getLayoutBounds().getWidth());
                }
            }
	    }

        /**
         * Return the nearest configuration index based on moving copied node
         * @return the nearest configuration index based on moving copied node
         */
        private int getNearestConfigIndex () {
	        int index = 0;
	        double distance;
	        double min = Double.MAX_VALUE;
            for (int i = 0; i < _possibleXValues.size(); i++) {
                distance = Math.abs(_copy.getX() + _copiedNode.getTranslateX() - _possibleXValues.get(i));
                if (distance < min) {
                    min = distance;
                    index = i;
                }
            }
            return index;
        }

        /**
         * Updates the configuration based on the changing in configuration index
         */
        private void updateConfig () {
	        CompoundExpression parentExpression = _focus.getParent();
            if (parentExpression instanceof BranchExpression)
                ((BranchExpression) parentExpression).updateNode(_focus, _configIndex, _lastConfigIndex);
        }

        /**
         * Check if the given x and y coordinate is on the given node
         * @param node the node need to be checked
         * @param mouseX the x coordinate
         * @param mouseY the y coordinate
         * @return whether the given x and y coordinate is on the given node
         */
        private boolean checkOnNode (Node node, double mouseX, double mouseY){
            Bounds bound = node.localToScene(node.getBoundsInLocal());
            double minX, maxX, minY, maxY;
            minY = bound.getMinY();
            maxY = bound.getMaxY();
            minX = bound.getMinX();
            maxX = bound.getMaxX();

            return mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY;
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
