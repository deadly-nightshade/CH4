package sample.Model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
public class NodeGestures {

    private DragContext nodeDragContext = new DragContext();

    CanvasPane canvas;

    public NodeGestures( CanvasPane canvas) {
        this.canvas = canvas;

    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {
            // left mouse button => dragging
            if( event.isPrimaryButtonDown()) {

            nodeDragContext.mouseAnchorX = event.getSceneX();
            nodeDragContext.mouseAnchorY = event.getSceneY();

            Node node = (Node) event.getSource();

            nodeDragContext.translateAnchorX = node.getTranslateX();
            nodeDragContext.translateAnchorY = node.getTranslateY();
            }
        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if( !event.isPrimaryButtonDown())
                return;

            double scale = canvas.getScale();

            AtomNode node = (AtomNode) event.getSource();

            node.setTranslateX(nodeDragContext.translateAnchorX + (( event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + (( event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));
//
//            node.setLayoutX(node.getLayoutX() + node.getTranslateX());
//
//            node.setTranslateX(0);
//            node.setLayoutY(node.getLayoutY() + node.getTranslateY());
//            node.setTranslateY(0);

//            for (Bond e: node.getAtom().bondsList) {
//                e.setTranslateX(nodeDragContext.translateAnchorX + (( event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
//                e.setTranslateY(nodeDragContext.translateAnchorY + (( event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));
//            }
            event.consume();

        }
    };
}
