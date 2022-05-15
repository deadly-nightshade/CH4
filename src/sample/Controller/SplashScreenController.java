package sample.Controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import sample.Run;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class SplashScreenController implements Initializable {
    @FXML AnchorPane anchorPane;
    @FXML ProgressBar progressBar;

    private ArrayList<Circle> circleList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        circleList = new ArrayList<>();

        for (int i = 0; i < anchorPane.getChildren().size()-1; i++) {
            circleList.add((Circle) anchorPane.getChildren().get(i));
        }

        anchorPane.getChildren().clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Circle c : circleList) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //Instantiating FadeTransition class
                            FadeTransition fade = new FadeTransition();


                            //setting the duration for the Fade transition
                            fade.setDuration(Duration.millis(300));

                            //setting the initial and the target opacity value for the transition
                            fade.setFromValue(0);
                            fade.setToValue(10);

                            //setting cycle count for the Fade transition
                            fade.setCycleCount(1);

                            //setting Circle as the node onto which the transition will be applied
                            fade.setNode(c);

                            anchorPane.getChildren().add(c);

                            //playing the transition
                            fade.play();



                        }
                    });

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Wait for 2 seconds.
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        new Run().changeScreen("View/OpenProject.fxml");
                        new Run().closeSplashStage();
                    }
                });
            }
        }).start();
    }
}
