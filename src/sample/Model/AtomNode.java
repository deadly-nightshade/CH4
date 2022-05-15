package sample.Model;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class AtomNode extends Circle{
    private String atomType;
    Atom atom;

    public AtomNode(String atomType, String colour, String label) {
        this.atomType = atomType;

        this.setRadius(24);
        this.setFill(Color.web(colour).deriveColor(1, 1, 1, 0.5));
        this.setStroke(Color.BLACK);
    }

    public Atom getAtom() {
        return atom;
    }

    public String getAtomType() {
        return atomType;
    }
}
