package sample.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import sample.Run;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenProjectController implements Initializable {
    private FXMLLoader loader;
    private WorkspaceController workspaceController;

    private Run run = new Run();

    @FXML AnchorPane anchorPane;
    @FXML MenuBar menuBar;

    @FXML MenuItem lightMenuItem;
    @FXML MenuItem darkMenuItem;

    @FXML FlowPane recentFlowPane;
    @FXML FlowPane sampleFlowPane;

    @FXML ImageView newImgView;
    @FXML ImageView folderImgView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loader = Run.loader;

        loadRecentFlowPane();
        loadSampleFlowPane();

        // resizable
        menuBar.prefWidthProperty().bind(anchorPane.widthProperty());

        lightMenuItem.setDisable(false);
        darkMenuItem.setDisable(false);

        try {
            if (run.getTheme().equals("dark")) {
                newImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/darknew.png")));
                folderImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/darkfolder.png")));
            } else if (run.getTheme().equals("light")) {
                newImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/new.png")));
                folderImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/folder.png")));
            }
        } catch (IOException ex) {
            createAlertError("Unable to load icons!", "Sorry for the inconvenience.");
        }
    }

    @FXML
    public void handleNewProject() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CH4 File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CH4 Files", "*.ch4")
            );
            File selectedFile = fileChooser.showSaveDialog(Run.stage);
            if (selectedFile != null) {

                run.addToRecent(selectedFile);
                run.changeScreen("View/Workspace.fxml");

                loader = Run.loader;
                workspaceController = loader.getController();
                workspaceController.initialiseLines();
                workspaceController.newProj(selectedFile);
            }
        } catch (Exception ex) {
            run.changeScreen("View/OpenProject.fxml");
            createAlertError("Error creating project!", "Please try again.");

        }
    }

    @FXML
    public void handleOpenProject() {
        try {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CH4 File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CH4 Files", "*.ch4")
        );
        File currentFile = fileChooser.showOpenDialog(Run.stage);
        if (currentFile != null) {
            run.addToRecent(currentFile);
            run.changeScreen("View/Workspace.fxml");
            loader = Run.loader;
            workspaceController = loader.getController();
            workspaceController.initialiseLines();
            if (!workspaceController.openFromFile(currentFile)) {
                run.changeScreen("View/OpenProject.fxml");
                createAlertError("Error opening file", "File may be corrupted");
            }
        }} catch (Exception ex) {
            run.changeScreen("View/OpenProject.fxml");
            createAlertError("Error opening project!", "Please try again.");

        }
    }

    // color scheme
    @FXML
    public void handleLightMode() {
        if (!run.changeTheme("light", run.stage)) {
            createAlertError("Unable to change theme!", "Please try again.");
        } else {
            lightMenuItem.setDisable(true);
            darkMenuItem.setDisable(false);

            try {
                newImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/new.png")));
                folderImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/folder.png")));
            } catch (IOException ex) {
                createAlertError("Unable to load icons!", "Sorry for the inconvenience.");
            }
        }
    }

    @FXML
    public void handleDarkMode() {
        if (!run.changeTheme("dark", run.stage)) {
            createAlertError("Unable to change theme!", "Please try again.");
        } else {
            lightMenuItem.setDisable(false);
            darkMenuItem.setDisable(true);

            try {
                newImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/darknew.png")));
                folderImgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/darkfolder.png")));
            } catch (IOException ex) {
                createAlertError("Unable to load icons!", "Sorry for the inconvenience.");
            }
        }
    }

    // about program
    @FXML
    public void handleAboutProgram() {
        run.changeToSmallScreen("View/AboutProgrammer.fxml");
    }

    // helper
    public void loadRecentFlowPane() {
        try {
            BufferedReader input = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Resources/recentFiles.txt"));
            String line;
            int count = 0;
            while ((line=input.readLine()) != null && line.length() > 0) {
                if (count == 5) {
                    break;
                }
                File file = new File(line);
                if (file.exists()) {
                    count++;
                    VBox vBox = new VBox();
                    Rectangle rect = new Rectangle(140,150);
                    rect.setFill(Color.web("#7afbfd").deriveColor(0, 1, 1, 0.5));
                    vBox.getChildren().addAll(new Label(file.getName()), rect);
                    vBox.setPadding(new Insets(0,20,0,0));
                    rect.setOnMouseClicked(e-> {
                        run.addToRecent(file);
                        run.changeScreen("View/Workspace.fxml");
                        loader = Run.loader;
                        workspaceController = loader.getController();
                        workspaceController.initialiseLines();
                        if (!workspaceController.openFromFile(file)) {
                            run.changeScreen("View/OpenProject.fxml");
                            createAlertError("Error opening file", "File may be corrupted");
                        }
                    });
                    recentFlowPane.getChildren().add(vBox);
                }
            }
        }
        catch (IOException ex) {}
    }

    public void loadSampleFlowPane() {
        try {
            BufferedReader input = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Resources/sampleFiles.txt"));
            String line;
            int count = 0;
            while ((line=input.readLine()) != null && line.length() > 0) {
                if (count == 5) {
                    break;
                }
                File file = new File(System.getProperty("user.dir") + "/Resources/"+line);
                if (file.exists()) {
                    count++;
                    VBox vBox = new VBox();
                    Rectangle rect = new Rectangle(140,120);
                    rect.setFill(Color.web("#7afbfd").deriveColor(0, 1, 1, 0.5));
                    vBox.getChildren().addAll(new Label(file.getName()), rect);
                    vBox.setPadding(new Insets(0,20,0,0));
                    rect.setOnMouseClicked(e-> {
                        run.changeScreen("View/Workspace.fxml");
                        loader = Run.loader;
                        workspaceController = loader.getController();
                        workspaceController.isSample = true;
                        workspaceController.initialiseLines();
                        if (!workspaceController.openFromFile(file)) {
                            run.changeScreen("View/OpenProject.fxml");
                            createAlertError("Error opening file", "File may be corrupted");
                        }
                    });
                    sampleFlowPane.getChildren().add(vBox);
                }
            }
        }
        catch (IOException ex) {}
    }

    public void createAlertError(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }
}
