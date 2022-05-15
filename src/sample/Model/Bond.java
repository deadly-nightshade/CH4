package sample.Model;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class Bond extends Line {
    private Atom atom1;
    private Atom atom2;
    private int type;

        public Bond (Atom atom1, int type) {
            if (type > 3 || type < 1) {
                return;
            }
//
//            startXProperty().bind(startX);
//            startYProperty().bind(startY);
//            endXProperty().bind(endX);
//            endYProperty().bind(endY);
            if (type == 1) {
                setStrokeWidth(3);
                getStrokeDashArray().setAll(10.0, 5.0);
                setStroke(Color.web("#7afbfd").deriveColor(0, 1, 1, 0.5));
            } else if (type == 2) {
                setStrokeWidth(5);
                getStrokeDashArray().setAll(7.0, 3.0);
                setStroke(Color.web("#02aeb1").deriveColor(0, 1, 1, 0.5));
            } else if (type == 3) {
                setStrokeWidth(7);
                getStrokeDashArray().setAll(5.0, 1.0);
                setStroke(Color.web("#004748").deriveColor(0, 1, 1, 0.5));
            }

            setStrokeLineCap(StrokeLineCap.ROUND);
//            setMouseTransparent(true);


            this.atom1 = atom1;
            this.type = type;

            this.atom1.addBond(this);
//            this.atom2 = atom2;
        }


//
//        stackPane = new StackPane();
//
//        if (type == 1) {
//            stackPane.getChildren().add(new Rectangle(39,9, Color.web("#f8b195")));
//        } else if (type == 2) {
//            stackPane.getChildren().add(new Rectangle(39,7, Color.web("#f67280")));
//            stackPane.getChildren().add(new Rectangle(39,7, Color.web("#f67280")));
//        } else {
//            stackPane.getChildren().add(new Rectangle(39,6, Color.web("#cbaacb")));
//            stackPane.getChildren().add(new Rectangle(39,6, Color.web("#cbaacb")));
//            stackPane.getChildren().add(new Rectangle(39,6, Color.web("#cbaacb")));
//        }
//
//        Pane a = (Pane) pane.getContent();
//        a.getChildren().add(stackPane);

    public Atom getAtom1() {
        return atom1;
    }

    public Atom getAtom2() {
        return atom2;
    }

    public void setAtom2(Atom atom2) {
            this.atom2 = atom2;
            atom2.addBond(this);
    }

    public Atom getOtherAtom(Atom atom) {
            if (atom1.equals(atom)) {
                return atom2;
            } else if (atom2.equals(atom)) {
                return atom1;
            }
            return null;
    }

    public int getType() {
        return type;
    }

}
