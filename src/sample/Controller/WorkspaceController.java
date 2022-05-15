package sample.Controller;


// todo make it clear its for organic molecules...
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.web.WebView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import sample.Model.*;
import sample.Run;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class WorkspaceController implements Initializable {
    @FXML private BorderPane borderPane;
    @FXML private MenuBar menuBar;
    @FXML private TabPane tabPane;

    @FXML private TextArea IUPACnameTA;

    // menu
//    @FXML private MenuItem openMenuItem;
//    @FXML private MenuItem saveMenuItem;
//    @FXML private MenuItem quitMenuItem;
    @FXML private MenuItem lightMenuItem;
    @FXML private MenuItem darkMenuItem;
    @FXML private TextField searchTf;
    @FXML private ImageView searchImgView;
//    @FXML private MenuItem searchMenuItem;
//    @FXML private MenuItem aboutProgramMenuItem;

    // Lines in Legend(Bond)
    @FXML private Line singleBondLine;
    @FXML private Line doubleBondLine;
    @FXML private Line tripleBondLine;

    // experiment
    @FXML private Slider heatSlider;

    @FXML private Label amuLabel;
    @FXML private TextFlow formulaTextFlow;
    // @FXML private TextArea nomenclatureTA;

    // functional group checkboxes
    @FXML private Label alkaneLabel;
    @FXML private Label alkeneLabel;
    @FXML private Label alkyneLabel;
    @FXML private Label alcoholLabel;
    @FXML private Label etherLabel;
    @FXML private Label aldehydeLabel;
    @FXML private Label ketoneLabel;
    @FXML private Label acidLabel;
    @FXML private Label esterLabel;
    @FXML private Label amineLabel;
    @FXML private Label amideLabel;
    @FXML private Label nitrileLabel;

    // reaction checkboxes
    @FXML private CheckBox neutralisationCheckBox;
    @FXML private CheckBox oxidationCheckBox;
    @FXML private CheckBox additionCheckBox;
    @FXML private CheckBox substitutionCheckBox;
    @FXML private CheckBox condensationCheckBox;
    @FXML private CheckBox dehydrationCheckBox;
    @FXML private CheckBox esterificationCheckBox;

    // properties checkboxes + radiobuttons
    @FXML private CheckBox ldfCheckBox;
    @FXML private CheckBox hbCheckBox;
    @FXML private CheckBox ddfCheckBox;
    @FXML private RadioButton h2oYesRadioButton;
    @FXML private RadioButton h2oNoRadioButton;
    @FXML private RadioButton organicYesRadioButton;
    @FXML private RadioButton organicNoRadioButton;

    private ContextMenu atomContextMenu;
    private ContextMenu hydrogenContextMenu;
    private ContextMenu oxygenContextMenu;

    private ContextMenu bondContextMenu;

    private CanvasPane canvas;
    private NodeGestures nodeGestures;
    private SceneGestures sceneGestures;

    private Label[] funcGroupLabelList;
    private CheckBox[] reactionsCheckBoxList;
    private CheckBox[] imfCheckBoxList;
    private RadioButton[] solubilityRadioButtonList;

    private String carbonInfo = "Carbon occurs in all known organic life and is the basis of organic chemistry. In most stable compounds of carbon, carbon obeys the octet rule and is tetravalent, meaning that a carbon atom forms a total of four covalent bonds. Carbon forms a vast number of compounds, more than any other element, with almost ten million compounds described to date. A tally of unique compounds shows that more contain carbon than do not.";
    private String hydrogenInfo = "Hydrogen is the lightest element. Hydrogen is nonmetallic, except at extremely high pressures, and readily forms a single covalent bond with most nonmetallic elements, forming compounds such as water and nearly all organic compounds. Hydrogen forms a vast array of compounds with carbon called hydrocarbons. Millions of hydrocarbons are known, and they are usually formed by complicated pathways that seldom involve elemental hydrogen.";
    private String nitrogenInfo = "Nitrogen is one of the most important elements in organic chemistry. In organic compounds, nitrogen is usually trivalent, forming a total of three covalent bonds. Nitrogen is an essential component of nucleic acids, amino acids and thus proteins, and the energy-carrying molecule adenosine triphosphate and is thus vital to all life on Earth.";
    private String oxygenInfo = "Oxygen is an oxidizing agent that readily forms oxides with most elements as well as with other compounds. There are many important organic solvents, and the element oxygen element is found in almost all biomolecules that are important to or generated by) life.";

    private Molecule molecule;

    private File currentFile;
    boolean isSample;
    private Run run;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        run = new Run();
        // setting menubar
        lightMenuItem.setDisable(false);
        darkMenuItem.setDisable(false);

        // set slider

        heatSlider.setMin(0);
        heatSlider.setMax(100);
        heatSlider.setShowTickLabels(true);
        heatSlider.setShowTickMarks(true);
        heatSlider.setMajorTickUnit(50);
        heatSlider.setMinorTickCount(5);
        heatSlider.setBlockIncrement(10);

        heatSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                handleHeat();
            }
        });

        try {
            searchImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/search.png")));
        }
        catch (IOException e) {}

        searchTf.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    handleSearch();
                    searchTf.setText("");
                }
            }
        });

        searchTf.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
        });

        // bond context menu
        bondContextMenu = new ContextMenu();
        MenuItem delete
                = new MenuItem("Delete");

        delete.setOnAction(event -> {

            MenuItem source = (MenuItem) event.getTarget();
            ContextMenu popup = source.getParentPopup();
            if (popup != null) {

                Bond bond = (Bond) popup.getOwnerNode();
                if (bond.getAtom1() != null) {
                    bond.getAtom1().deleteBond(bond);
                }
                if (bond.getAtom2() != null) {
                    bond.getAtom2().deleteBond(bond);
                }
                molecule.deleteBond(bond);
                canvas.getChildren().remove(bond);
            }
        });

        bondContextMenu.getItems().add(delete);


        // atom context menu
        atomContextMenu = new ContextMenu();
        hydrogenContextMenu = new ContextMenu();
        oxygenContextMenu = new ContextMenu();

//        Menu item1 = new Menu("Create Bond");
        MenuItem singleBond = new MenuItem("Create Single Bond");
        MenuItem doubleBond = new MenuItem("Create Double Bond");
        MenuItem tripleBond = new MenuItem("Create Triple Bond");

        MenuItem hydrogenSingleBond = new MenuItem("Create Single Bond");
        MenuItem oxygenSingleBond = new MenuItem("Create Single Bond");
        MenuItem oxygenDoubleBond = new MenuItem("Create Double Bond");


        EventHandler<ActionEvent> createBond = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                MenuItem source = (MenuItem) event.getTarget();
                ContextMenu popup = source.getParentPopup();
                if (popup != null) {

                    AtomNode atomNode = (AtomNode) popup.getOwnerNode();
                    Atom atom = atomNode.getAtom();

                    // checking if atom has the space
                    if (atom.getNoOfBonds() != atom.getMaxNoOfBonds()) {

                        Bond bond = null;
                        if (source.getText().equals("Create Single Bond") && atom.getNoOfBonds() + 1 <= atom.getMaxNoOfBonds()) {
                            bond = new Bond(atom, 1);

                        } else if (source.getText().equals("Create Double Bond") && atom.getNoOfBonds() + 2 <= atom.getMaxNoOfBonds()) {
                            bond = new Bond(atom, 2);
                        } else if (source.getText().equals("Create Triple Bond") && atom.getNoOfBonds() + 3 <= atom.getMaxNoOfBonds()) {
                            bond = new Bond(atom, 3);
                        } else {
                            createAlertError("The atom does not have enough electrons to form that bond!", "Please try a bond with lower multiplicity.");
                            return;
                        }

                        bond.startXProperty().bind(atomNode.translateXProperty());
                        bond.startYProperty().bind(atomNode.translateYProperty());

                        bond.setEndX(bond.getStartX() + 50);
                        bond.setEndY(bond.getStartY());

                        molecule.addBond(bond);
                        canvas.getChildren().add(bond);

                        bond.setCursor(Cursor.CLOSED_HAND);

                        Bond finalBond = bond;
                        bond.setOnContextMenuRequested(e -> {
                            bondContextMenu.show(finalBond, Side.RIGHT, 0, 0);
                        });

                        bond.setOnMousePressed(e -> {
                            finalBond.setEndX(e.getX());
                            finalBond.setEndY(e.getY());
                        });

                        bond.setOnMouseDragged(e -> {
                            finalBond.setEndX(e.getX());
                            finalBond.setEndY(e.getY());
                        });

                        bond.setOnMouseReleased(e -> {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    for (AtomNode a : molecule.getAtomNodeList()) {

                                        //(x - center_x)^2 + (y - center_y)^2 < radius^2 if a point is in a circle
                                        if (!atomNode.equals(a) && Math.pow(e.getX() - a.getTranslateX(), 2) + Math.pow(e.getY() - a.getTranslateY(), 2) <=
                                                Math.pow(a.getRadius(), 2)) {

                                            // if its not the own atom AND if the point is in another atom's circle

                                            for (Bond b : atomNode.getAtom().bondsList) {
                                                // for every bond

                                                // check if there is an existing bond between the 2 atoms
                                                if (b.getAtom1() != null && b.getAtom2() != null) {
                                                    if (b.getAtom1().equals(a.getAtom()) || b.getAtom2().equals(a.getAtom())) {
                                                        Platform.runLater(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                finalBond.getAtom1().deleteBond(finalBond);
                                                                molecule.deleteBond(finalBond);
                                                                canvas.getChildren().remove(finalBond);
                                                                createAlertError("A bond between these two atoms already exist!", "You can you can choose if the bond is single, double or triple.");
                                                            }
                                                        });
                                                        return;
                                                    }
                                                }
                                            }


                                            // check if the atom the bond lands on has enough space
                                            if (a.getAtom().getNoOfBonds() + finalBond.getType() > a.getAtom().getMaxNoOfBonds()) {

                                                Platform.runLater(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        createAlertError("The atom the bond is dragged to does not have enough electrons to form that bond!", "Please try a bond with lower multiplicity or drag the bond somewhere else.");
                                                    }
                                                });
                                                return;
                                            }

                                            Paint color = a.getFill();

                                            //flashing
                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    a.setFill(Color.web("#00FF00").deriveColor(1, 1, 1, 0.5));
                                                }
                                            });

                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException interruptedException) {
                                                interruptedException.printStackTrace();
                                            }

                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    a.setFill(color);
                                                }
                                            });

                                            finalBond.setAtom2(a.getAtom());
                                            finalBond.endXProperty().bind(a.translateXProperty());
                                            finalBond.endYProperty().bind(a.translateYProperty());
                                            finalBond.setCursor(Cursor.HAND);
                                            finalBond.setOnMousePressed(e -> {
                                            });
                                            finalBond.setOnMouseDragged(e -> {
                                            });
                                            finalBond.setOnMouseReleased(e -> {
                                            });
                                            break;
                                        }
                                    }
                                }
                            }).start();
                        });
                    } else {
                        createAlertError("Maximum number of bonds reached!", "Atom cannot form more bonds. Do not continue doing so.");
                    }
                } else {
                    createAlertError("Error creating bond.", "Please try again!");
                }

            }
        };

        singleBond.setOnAction(createBond);
        doubleBond.setOnAction(createBond);
        tripleBond.setOnAction(createBond);

        hydrogenSingleBond.setOnAction(createBond);
        oxygenSingleBond.setOnAction(createBond);
        oxygenDoubleBond.setOnAction(createBond);

        MenuItem item2 = new MenuItem("Delete");
        MenuItem hydrogenDelete = new MenuItem("Delete");
        MenuItem oxygenDelete = new MenuItem("Delete");

        EventHandler<ActionEvent> deleteBond = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                MenuItem source = (MenuItem) e.getTarget();
                ContextMenu popup = source.getParentPopup();
                if (popup != null) {
                    AtomNode a = (AtomNode) popup.getOwnerNode();

                    // removing bonds
                    for (Bond b : a.getAtom().bondsList) {
                        if (canvas.getChildren().contains(b)) {
                            if (b.getAtom2() != null && !b.getAtom2().equals(a.getAtom())) {
                                // removing from other atom
                                b.getAtom2().deleteBond(b);
                            }
                            if (b.getAtom1() != null && !b.getAtom1().equals(a.getAtom())) {
                                b.getAtom1().deleteBond(b);
                            }
                            molecule.deleteBond(b);
                            canvas.getChildren().remove(b);
                        }
                    }

                    molecule.deleteAtomNode(a);
                    canvas.getChildren().remove(a);
                } else {
                    createAlertError("Error deleting atom.", "Please try again!");
                }
            }
        };

        item2.setOnAction(deleteBond);
        hydrogenDelete.setOnAction(deleteBond);
        oxygenDelete.setOnAction(deleteBond);

        atomContextMenu.getItems().addAll(singleBond, doubleBond, tripleBond, item2);
        hydrogenContextMenu.getItems().addAll(hydrogenSingleBond,hydrogenDelete);
        oxygenContextMenu.getItems().addAll(oxygenSingleBond, oxygenDoubleBond, oxygenDelete);


        canvas = new CanvasPane();
        canvas.addGrid();

        nodeGestures = new NodeGestures(canvas);
        sceneGestures = new SceneGestures(canvas);

        tabPane.getTabs().add(new Tab("Tab 1", canvas));

        canvas.prefHeightProperty().bind(tabPane.heightProperty());
        canvas.prefWidthProperty().bind(tabPane.widthProperty());

        tabPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        tabPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        tabPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        // initialising
        molecule = new Molecule();
        funcGroupLabelList = new Label[]{alkaneLabel, alkeneLabel, alkyneLabel, alcoholLabel, etherLabel, aldehydeLabel, ketoneLabel, acidLabel, esterLabel, amineLabel, amideLabel, nitrileLabel};
        reactionsCheckBoxList = new CheckBox[] {neutralisationCheckBox, oxidationCheckBox, additionCheckBox,substitutionCheckBox, condensationCheckBox, dehydrationCheckBox, esterificationCheckBox};
        imfCheckBoxList = new CheckBox[] {ldfCheckBox, hbCheckBox, ddfCheckBox};
        solubilityRadioButtonList = new RadioButton[] {h2oYesRadioButton, h2oNoRadioButton, organicYesRadioButton, organicNoRadioButton};

    }

    public void initialiseLines() {
        singleBondLine.setStrokeWidth(3);
        singleBondLine.getStrokeDashArray().setAll(10.0, 5.0);
        singleBondLine.setStroke(Color.web("#7afbfd").deriveColor(0, 1, 1, 0.5));

        doubleBondLine.setStrokeWidth(5);
        doubleBondLine.getStrokeDashArray().setAll(7.0, 3.0);
        doubleBondLine.setStroke(Color.web("#02aeb1").deriveColor(0, 1, 1, 0.5));

        tripleBondLine.setStrokeWidth(7);
        tripleBondLine.getStrokeDashArray().setAll(5.0, 1.0);
        tripleBondLine.setStroke(Color.web("#004748").deriveColor(0, 1, 1, 0.5));
    }


    // adding atoms
    public void addAtom(AtomNode a) {
        a.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        a.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        a.setCursor(Cursor.HAND);


        if (a instanceof CarbonNode) {
            a.setOnContextMenuRequested(e -> atomContextMenu.show(a, Side.RIGHT, 0, 0));

            try {
                Tooltip tt = makeTooltip(new Tooltip(carbonInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/carbon.jpg"), 250, 250, false, false)));

                Tooltip.install(a, tt);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if (a instanceof HydrogenNode) {
            try {

                a.setOnContextMenuRequested(e -> hydrogenContextMenu.show(a, Side.RIGHT, 0, 0));

                Tooltip tt = makeTooltip(new Tooltip(hydrogenInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/hydrogen.png"), 250, 250, false, false)));

                Tooltip.install(a, tt);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        else if (a instanceof NitrogenNode) {
            try {
                a.setOnContextMenuRequested(e -> atomContextMenu.show(a, Side.RIGHT, 0, 0));

                Tooltip tt = makeTooltip(new Tooltip(nitrogenInfo));
            tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/nitrogen.jpg"), 250, 250, false, false)));

            Tooltip.install(a, tt);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        }
        else if (a instanceof OxygenNode) {
            try {
                a.setOnContextMenuRequested(e -> oxygenContextMenu.show(a, Side.RIGHT, 0, 0));

                Tooltip tt = makeTooltip(new Tooltip(oxygenInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/oxygen.jpg"),250, 250, false, false)));

                Tooltip.install(a, tt);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        molecule.addAtomNode(a);
        canvas.getChildren().add(a);
    }

    @FXML
    public void handleAddCarbon() {
        CarbonNode c = new CarbonNode();
        //c.getStyleClass().add("carbon");
        addAtom(c);
    }

    @FXML
    public void handleAddHydrogen() {
        HydrogenNode h = new HydrogenNode();
       // h.getStyleClass().add("hydrogen");
        addAtom(h);

    }

    @FXML
    public void handleAddNitrogen() {
        NitrogenNode n = new NitrogenNode();
       // n.getStyleClass().add("nitrogen");
        addAtom(n);

    }

    @FXML
    public void handleAddOxygen() {
        OxygenNode o = new OxygenNode();
       // o.getStyleClass().add("oxygen");
        addAtom(o);
    }

    @FXML
    public void handleDetermineProperties() {
        // check if it's a complete molecule
        String result = molecule.isMoleculeComplete();
        if (!result.equals("Complete")) {
            createAlertError("Incomplete molecule!", result);

            resetProperties();
        } else {

        // set mass and formula at the bottom
        amuLabel.setText(String.format("%.2f",molecule.calculateMolecularMass()));

        formulaTextFlow.getChildren().clear();
        int[] formula = molecule.calculateFormula();
        if (formula[0] > 0) {
            Text text = new Text("C");
            if (formula[0] > 1) {
                Text textNo = new Text(""+formula[0]);
                textNo.setTranslateY(text.getFont().getSize() * 0.3);
                formulaTextFlow.getChildren().addAll(text,textNo);
            } else {formulaTextFlow.getChildren().addAll(text);}
        } if (formula[1] > 0) {
            Text text = new Text("H");
            if (formula[1] > 1) {
                Text textNo = new Text(""+formula[1]);
                textNo.setTranslateY(text.getFont().getSize() * 0.3);
                formulaTextFlow.getChildren().addAll(text,textNo);
            } else {formulaTextFlow.getChildren().addAll(text);}
        } if (formula[2] > 0) {
            Text text = new Text("O");
            if (formula[2] > 1) {
                Text textNo = new Text(""+formula[2]);
                textNo.setTranslateY(text.getFont().getSize() * 0.3);
                formulaTextFlow.getChildren().addAll(text,textNo);
            } else {formulaTextFlow.getChildren().addAll(text);}
        } if (formula[3] > 0) {
            Text text = new Text("N");
             if (formula[3] > 1) {
                Text textNo = new Text(""+formula[3]);
                textNo.setTranslateY(text.getFont().getSize() * 0.3);
                formulaTextFlow.getChildren().addAll(text,textNo);
            } else {formulaTextFlow.getChildren().addAll(text);}
        }

        for (Node n: formulaTextFlow.getChildren()) {
            Text t = (Text) n;
            if (run.getTheme().equals("light")) {
                t.setStyle("-fx-font-size: 18px;");
            }
            else if (run.getTheme().equals("dark")) {
                t.setStyle("-fx-font-size: 18px; -fx-fill: #fff;");
            }
        }


        // at the right accordion

        // functional groups
        int[] funcGroups = molecule.calculateFunctionalGroups();

        for (int i = 0; i < funcGroupLabelList.length; i++) {
            funcGroupLabelList[i].setText(""+funcGroups[i]);
        }

        // reactions
        boolean[] reactions = molecule.calculateReactions(funcGroups);
        for (int i = 0; i < reactionsCheckBoxList.length; i++) {
            reactionsCheckBoxList[i].setSelected(reactions[i]);
        }

        // imf
        boolean[] imf = molecule.calculateImf(formula);
        for (int i = 0; i < imfCheckBoxList.length; i++) {
            imfCheckBoxList[i].setSelected(imf[i]);
        }

        // solubility
        boolean solubilityInWater = molecule.calculateSolubilityInWater(formula);
        solubilityRadioButtonList[0].setSelected(solubilityInWater);
        solubilityRadioButtonList[1].setSelected(!solubilityInWater);
        solubilityRadioButtonList[2].setSelected(!solubilityInWater);
        solubilityRadioButtonList[3].setSelected(solubilityInWater);

        createAlertInformation("Your molecule has been analysed!", "Information has loaded.");
    }}


    @FXML
    public void handleClearAll() {
        if (createAlertConfirmation("Are you sure you want to clear all?", "This action cannot be reversed!")) {
            molecule.getAtomNodeList().clear();
            molecule.getEdgesList().clear();
            canvas.getChildren().clear();
            resetProperties();
        }
    }

    // file io methods
    public boolean newProj(File file) {
        try {
            this.currentFile = file;
            tabPane.getTabs().get(0).setText(file.getName());
            this.molecule = new Molecule();
            return true;
        }
        catch (Exception ex) {return false;}
    }


    @FXML
    public void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CH4 File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CH4 Files", "*.ch4")
        );
        currentFile = fileChooser.showOpenDialog(Run.stage);
        if (currentFile != null) {
            run.addToRecent(currentFile);
            if (!openFromFile(currentFile)) {
                createAlertError("Error opening file", "File may be corrupted");
            }
        }
    }

    @FXML
    public void handleSaveFile() {
        if (isSample) {
            createAlertError("You cannot edit a sample file!", "Please do not try!");
        } else if (!molecule.writeToFile(currentFile)) {
            createAlertError("Error with saving to file!", "Please try again!");
        }
    }

    @FXML
    public void handleQuit() {
        if (createAlertConfirmation("Are you sure you want to quit?", "Unsaved files will be discarded!")) {
            run.changeScreen("View/OpenProject.fxml");
        }
    }

    @FXML
    public void handleLightMode() {
        if (!run.changeTheme("light", run.stage)) {
            createAlertError("Unable to change theme!", "Please try again.");
        } else {
            lightMenuItem.setDisable(true);
            darkMenuItem.setDisable(false);

            for (Node t: formulaTextFlow.getChildren()) {
                t.setStyle("-fx-font-size: 18px; -fx-fill: #000;");
            }
        }
    }

    @FXML
    public void handleDarkMode() {
        if (!run.changeTheme("dark",run.stage)) {
            createAlertError("Unable to change theme!", "Please try again.");
        } else {
            lightMenuItem.setDisable(false);
            darkMenuItem.setDisable(true);

            for (Node t: formulaTextFlow.getChildren()) {
                t.setStyle("-fx-font-size: 18px; -fx-fill: #fff;");
            }
        }
    }

    @FXML
    public void handleSearch() {

        try {
            if (!(searchTf.getText().length() == 0)) {
                WebView webView = new WebView();
                webView.getEngine().load("https://google.com/search?q="+searchTf.getText());
                run.createWebView(webView);
            }
        }
        catch (Exception ex) {}



    }

    @FXML
    public void handleAboutProgram() {
        run.changeToSmallScreen("View/AboutProgrammer.fxml");
    }

    public boolean openFromFile(File file) {
        try {
            this.currentFile = file;
            
            Molecule newMolecule = new Molecule();
            tabPane.getTabs().get(0).setText(file.getName());

            BufferedReader input = new BufferedReader(new FileReader(file));
            String line;
            String[] tokens;

            while ((line = input.readLine()).substring(0,1).matches("[1-9]")) {
                tokens = line.split("[ ]");
                String[] coor = tokens[2].split("[,]");
                if (tokens[1].equals("Carbon")) {
                    handleAddCarbon(Double.parseDouble(coor[0]), Double.parseDouble(coor[1]), newMolecule);
                } else if (tokens[1].equals("Hydrogen")) {
                    handleAddHydrogen(Double.parseDouble(coor[0]), Double.parseDouble(coor[1]), newMolecule);
                } else if (tokens[1].equals("Nitrogen")) {
                    handleAddNitrogen(Double.parseDouble(coor[0]), Double.parseDouble(coor[1]), newMolecule);
                } else if (tokens[1].equals("Oxygen")) {
                    handleAddOxygen(Double.parseDouble(coor[0]), Double.parseDouble(coor[1]), newMolecule);
                } else {
                    // corrupted file!
                    return false;
                }

            }

            // straight to bonds
            while ((line = input.readLine()) != null && line.substring(0,1).matches("[0-9]")) {
                tokens = line.split("[ ]");
                String[] atoms = tokens[0].split("[-]");
                
                int bondType = Integer.parseInt(tokens[1]);

                // creating bonds
                AtomNode atomNode1 = null;
                AtomNode atomNode2 = null;
                if (!atoms[0].equals("0")) {
                    atomNode1 = newMolecule.getAtomNodeList().get(Integer.parseInt(atoms[0])-1);
                } if (!atoms[1].equals("0")) {
                atomNode2 = newMolecule.getAtomNodeList().get(Integer.parseInt(atoms[1])-1);
                }

                if (atomNode1 == null && atomNode2 == null) {
                    // both invalid
                    return false;
                }

                if (atomNode1 == null) {
                    atomNode1 = atomNode2;
                    atomNode2 = null;
                }
                
                if (atomNode1.getAtom().getNoOfBonds() + bondType <= atomNode1.getAtom().getMaxNoOfBonds()) {
                    // if atom 1 has space
                    
                    if (atomNode2 != null) {
                        if (atomNode2.getAtom().getNoOfBonds() + bondType <= atomNode2.getAtom().getMaxNoOfBonds()) {
                            // if atom 2 has space
                            
                            // check if there is an existing bond b/w the 2 atoms
                            for (Bond b: atomNode1.getAtom().bondsList) {
                                if (b.getAtom1() != null && b.getAtom2() != null) {
                                    if (b.getAtom1().equals(atomNode2.getAtom()) || b.getAtom2().equals(atomNode2.getAtom())) {
                                        return false;
                                    }
                                }
                            }
                            
                            // no existing bond, proceed create bond                            
                            
                            Bond bond = new Bond(atomNode1.getAtom(), bondType);
                            bond.setAtom2(atomNode2.getAtom());
                            bond.startXProperty().bind(atomNode1.translateXProperty());
                            bond.startYProperty().bind(atomNode1.translateYProperty());
                            bond.endXProperty().bind(atomNode2.translateXProperty());
                            bond.endYProperty().bind(atomNode2.translateYProperty());
                            bond.setCursor(Cursor.HAND);
                            
                            newMolecule.addBond(bond);
                            canvas.getChildren().add(bond);
                            bond.setOnContextMenuRequested(e -> {
                                bondContextMenu.show(bond, Side.RIGHT, 0, 0);
                            });

                        } else {
                            return false;
                        }
                    } else {    
                        // if atom 2 is null
                        Bond bond = new Bond(atomNode1.getAtom(), bondType);
                        bond.startXProperty().bind(atomNode1.translateXProperty());
                        bond.startYProperty().bind(atomNode1.translateYProperty());
                        bond.setEndX(bond.getStartX() + 50);
                        bond.setEndY(bond.getStartY());
                        
                        
                        newMolecule.addBond(bond);
                        canvas.getChildren().add(bond);
                        bond.setCursor(Cursor.CLOSED_HAND);
                        
                        // set event handlers
                        bond.setOnContextMenuRequested(e -> {
                            bondContextMenu.show(bond, Side.RIGHT, 0, 0);
                        });

                        bond.setOnMousePressed(e -> {
                            bond.setEndX(e.getX());
                            bond.setEndY(e.getY());
                        });
                        
                        bond.setOnMouseDragged(e -> {
                            bond.setEndX(e.getX());
                            bond.setEndY(e.getY());
                        });

                        AtomNode finalAtomNode = atomNode1;
                        bond.setOnMouseReleased(e -> {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    for (AtomNode a : newMolecule.getAtomNodeList()) {
                                        //(x - center_x)^2 + (y - center_y)^2 < radius^2 if a point is in a circle
                                        if (!finalAtomNode.equals(a) && Math.pow(e.getX() - a.getTranslateX(), 2) + Math.pow(e.getY() - a.getTranslateY(), 2) <=
                                                Math.pow(a.getRadius(), 2)) {

                                            // if its not the own atom AND if the point is in another atom's circle

                                            for (Bond b : finalAtomNode.getAtom().bondsList) {
                                                // for every bond

                                                // check if there is an existing bond between the 2 atoms
                                                if (b.getAtom1() != null && b.getAtom2() != null) {
                                                    if (b.getAtom1().equals(a.getAtom()) || b.getAtom2().equals(a.getAtom())) {
                                                        Platform.runLater(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                bond.getAtom1().deleteBond(bond);
                                                                newMolecule.deleteBond(bond);
                                                                canvas.getChildren().remove(bond);
                                                                createAlertError("A bond between these two atoms already exist!", "You can you can choose if the bond is single, double or triple.");
                                                            }
                                                        });
                                                        return;
                                                    }
                                                }
                                            }

                                            // check if the atom the bond lands on has enough space
                                            if (a.getAtom().getNoOfBonds() + bond.getType() > a.getAtom().getMaxNoOfBonds()) {

                                                Platform.runLater(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        createAlertError("The atom the bond is dragged to does not have enough electrons to form that bond!", "Please try a bond with lower multiplicity or drag the bond somewhere else.");
                                                    }
                                                });
                                                return;
                                            }

                                            Paint color = a.getFill();

                                            //flashing
                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    a.setFill(Color.web("#00FF00").deriveColor(1, 1, 1, 0.5));
                                                }
                                            });

                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException interruptedException) {
                                                interruptedException.printStackTrace();
                                            }

                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    a.setFill(color);
                                                }
                                            });

                                            bond.setAtom2(a.getAtom());
                                            bond.endXProperty().bind(a.translateXProperty());
                                            bond.endYProperty().bind(a.translateYProperty());
                                            bond.setCursor(Cursor.HAND);
                                            bond.setOnMousePressed(e -> {
                                            });
                                            bond.setOnMouseDragged(e -> {
                                            });
                                            bond.setOnMouseReleased(e -> {
                                            });
                                            break;
                                        }
                                    }
                                }
                            }).start();
                        });
                    }
                }
            }
            input.close();
            this.molecule = newMolecule;
            System.out.println("SIZE" + molecule.getAtomNodeList().size());
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }


    // file io adding
    public void handleAddAtom(AtomNode a, Molecule newMolecule) {
        a.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        a.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        a.setCursor(Cursor.HAND);


        if (a instanceof CarbonNode) {
            a.setOnContextMenuRequested(e -> atomContextMenu.show(a, Side.RIGHT, 0, 0));

            try {
                Tooltip tt = makeTooltip(new Tooltip(carbonInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/carbon.jpg"), 250, 250, false, false)));

                Tooltip.install(a, tt);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (a instanceof HydrogenNode) {
            try {

                a.setOnContextMenuRequested(e -> hydrogenContextMenu.show(a, Side.RIGHT, 0, 0));

                Tooltip tt = makeTooltip(new Tooltip(hydrogenInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/hydrogen.png"), 250, 250, false, false)));

                Tooltip.install(a, tt);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (a instanceof NitrogenNode) {
            try {
                a.setOnContextMenuRequested(e -> atomContextMenu.show(a, Side.RIGHT, 0, 0));

                Tooltip tt = makeTooltip(new Tooltip(nitrogenInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/nitrogen.jpg"), 250, 250, false, false)));

                Tooltip.install(a, tt);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (a instanceof OxygenNode) {
            try {
                a.setOnContextMenuRequested(e -> oxygenContextMenu.show(a, Side.RIGHT, 0, 0));

                Tooltip tt = makeTooltip(new Tooltip(oxygenInfo));
                tt.setGraphic(new ImageView(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/images/oxygen.jpg"), 250, 250, false, false)));

                Tooltip.install(a, tt);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        newMolecule.addAtomNode(a);
        canvas.getChildren().add(a);
    }


    public void handleAddCarbon(double x, double y, Molecule molecule) {
        CarbonNode c = new CarbonNode();
        c.getStyleClass().add("carbon");
        c.setTranslateX(x);
        c.setTranslateY(y);
        handleAddAtom(c, molecule);
    }

    public void handleAddHydrogen(double x, double y, Molecule molecule) {
        HydrogenNode h = new HydrogenNode();
        h.getStyleClass().add("hydrogen");
        h.setTranslateX(x);
        h.setTranslateY(y);
        handleAddAtom(h, molecule);

    }

    public void handleAddNitrogen(double x, double y, Molecule molecule) {
        NitrogenNode n = new NitrogenNode();
        n.getStyleClass().add("nitrogen");
        n.setTranslateX(x);
        n.setTranslateY(y);
        handleAddAtom(n, molecule);

    }

    public void handleAddOxygen(double x, double y, Molecule molecule) {
        OxygenNode o = new OxygenNode();
        o.getStyleClass().add("oxygen");
        o.setTranslateX(x);
        o.setTranslateY(y);
        handleAddAtom(o, molecule);
    }

    // heating
    private boolean isHeatOn = false;
    @FXML public void toggleHeat() {
        isHeatOn = !isHeatOn;
        handleHeat();
    }

    private Thread heatSliderThread;
    @FXML
    public void handleHeat() {
        // bugged
//
//        if (heatSliderThread != null && heatSliderThread.isAlive()) {
//            heatSliderThread.interrupt();
//        }

        if (!isHeatOn) {
            heatSliderThread.interrupt();
            tabPane.setMouseTransparent(false);
            return;
        }
        

        heatSliderThread = new Thread((new Runnable() {
            @Override
            public void run() {

                    tabPane.setMouseTransparent(true);

                    ArrayList<KeyValue> up = new ArrayList<>();
                    ArrayList<KeyValue> down = new ArrayList<>();
                    for (int i = 0; i < canvas.getChildren().size(); i++) {
                        if (canvas.getChildren().get(i) instanceof AtomNode) {
                            up.add(new KeyValue(canvas.getChildren().get(i).translateYProperty(), canvas.getChildren().get(i).getTranslateY() + 50, Interpolator.EASE_BOTH));
                            down.add(new KeyValue(canvas.getChildren().get(i).translateYProperty(), canvas.getChildren().get(i).getTranslateY() - 50, Interpolator.EASE_BOTH));
                        }
                    }

                    EventHandler<ActionEvent> e = x -> {
                    };

                    EventHandler<ActionEvent> u = x -> {
                    };


                    KeyFrame upKeyFrame = new KeyFrame(Duration.millis(2000-(heatSlider.getValue()*18)),"up", e, up);
                    KeyFrame downKeyFrame  = new KeyFrame(Duration.millis(2000-(heatSlider.getValue()*18)),"down", u,down);
                    Timeline timeline  = new Timeline();
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.setAutoReverse(true);
                    timeline.getKeyFrames().addAll(upKeyFrame, downKeyFrame);
                    timeline.play();
//
//                while(!Thread.interrupted()) {
//                    timeline.play();
//                }

            }
        }));
        heatSliderThread.setDaemon(true);
        heatSliderThread.start();
    }



    // helper methods
    public void resetProperties() {
        // clear things
        amuLabel.setText("");
        formulaTextFlow.getChildren().clear();
        for (int i = 0; i < funcGroupLabelList.length; i++) {
            funcGroupLabelList[i].setText("");
        }
        for (int i = 0; i < reactionsCheckBoxList.length; i++) {
            reactionsCheckBoxList[i].setSelected(false);
            reactionsCheckBoxList[i].setCursor(Cursor.NONE);
        }
        for (int i = 0; i < imfCheckBoxList.length; i++) {
            imfCheckBoxList[i].setSelected(false);
            imfCheckBoxList[i].setCursor(Cursor.NONE);
        }
        for (int i = 0; i < solubilityRadioButtonList.length; i++) {
            solubilityRadioButtonList[i].setSelected(false);
            solubilityRadioButtonList[i].setCursor(Cursor.NONE);
        }
        return;
    }

    public Tooltip makeTooltip(Tooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 14px; -fx-text-fill: #000; -fx-background-color: #fff;");
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(250);
        tooltip.setContentDisplay(ContentDisplay.valueOf("TOP"));
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
        return tooltip;
    }

    public void createAlertError(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public void createAlertInformation(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public boolean createAlertConfirmation(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } return false;
    }

}
