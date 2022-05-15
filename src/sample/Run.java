package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;

public class Run extends Application {

    public static Stage stage;
    public static Stage splashStage;
    public static FXMLLoader loader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = new Stage();
        stage.setTitle("CH4");
        Image icon = new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/icon2.jpg"));
        stage.getIcons().add(icon);

//        //TEST CODE//
//        stage.show();
//        loader = new FXMLLoader(getClass().getResource("View/OpenProject.fxml"));
//        Parent root = loader.load();
//        stage.setScene(new Scene(root, 900,600));
//        changeTheme(getTheme(), stage);
//        // TEST CODE //

        splashStage = new Stage(StageStyle.UNDECORATED);
        loader = new FXMLLoader(getClass().getResource("View/Splash.fxml"));
        Parent root = loader.load();
        splashStage.setScene(new Scene(root, 800,400));
        changeTheme(getTheme(), splashStage);
        splashStage.centerOnScreen();
        splashStage.show();
    }

    public void closeSplashStage() {
     splashStage.close();
    }

    public void createWebView(WebView webView) {
        Stage newStage = new Stage();
        newStage.setTitle("Search");

        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 640, 400);

        newStage.setScene(scene);
        newStage.show();
    }

    public String getTheme() {
        try {
            BufferedReader input = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Resources/theme.txt"));
            String line = input.readLine();
            input.close();
            return line;
        }

        catch(IOException ex) {
            return null;
        }
    }

    public void changeScreen(String input)  {
        try {
            loader = new FXMLLoader(Run.class.getResource(input));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("View/stylesheets/stylesheet.css").toString());
            stage.setScene(scene);
            changeTheme(getTheme(), stage);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void changeToSmallScreen(String input)  {
        try {
            Stage newStage = new Stage();
            newStage.setTitle("About Program");
            newStage.setResizable(false);
            loader = new FXMLLoader(Run.class.getResource(input));
            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 400);
            scene.getStylesheets().add(getClass().getResource("View/stylesheets/stylesheet.css").toString());
            if (getTheme().equals("light")) {
                scene.getStylesheets().add(getClass().getResource("View/stylesheets/lightmode.css").toString());
                } else if (getTheme().equals("dark")) {
                scene.getStylesheets().add(getClass().getResource("View/stylesheets/darkmode.css").toString());
            }
            newStage.setScene(scene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public boolean changeTheme(String theme, Stage stage) {
        if (theme.equals("light")) {
            stage.getScene().getStylesheets().remove(getClass().getResource("View/stylesheets/darkmode.css").toString());
            stage.getScene().getStylesheets().add(getClass().getResource("View/stylesheets/lightmode.css").toString());
        } else if (theme.equals("dark")) {
            stage.getScene().getStylesheets().remove(getClass().getResource("View/stylesheets/lightmode.css").toString());
            stage.getScene().getStylesheets().add(getClass().getResource("View/stylesheets/darkmode.css").toString());
        } else {return false;}

        try {
            PrintWriter output = new PrintWriter(System.getProperty("user.dir") + "/Resources/theme.txt");
            output.println(theme);
            output.close();
            return true;
        }
        catch(IOException ex) {
            return false;
        }
    }

    public boolean addToRecent(File file) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Resources/recentFiles.txt"));
            String line;
            String files = "";
            while ((line=input.readLine()) != null && line.length() > 0) {
                if (!line.equals(file.getAbsolutePath())) {
                    files += "\n" + line;
                }
            }
            input.close();

            files = file.getAbsolutePath() + files;
            PrintWriter output = new PrintWriter(new FileOutputStream(System.getProperty("user.dir") + "/Resources/recentFiles.txt"));
            output.print(files);
            output.close();
            return true;
        }
        catch (Exception ex) {return false;}
    }


    public static void main(String[] args) {
        launch(args);
    }
}
