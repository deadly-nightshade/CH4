package sample.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.Run;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class AboutProgrammerController implements Initializable {
    @FXML private ImageView imgView;

    @FXML private ComboBox languageComboBox;
    @FXML private Label yoursLabel;
    @FXML private Label nameLabel;
    @FXML private Label schoolLabel;
    @FXML private Label classLabel;

    private Locale locale = Locale.getDefault();
    private ResourceBundle rb;

    private Run run;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rb = ResourceBundle.getBundle("sample.ResourceBundle");
        languageComboBox.getItems().addAll("English", "Chinese", "Malay", "French");
        languageComboBox.setValue("English");

        run = new Run();
        String theme = run.getTheme();
        try {
            if (theme.equals("light")) {
                imgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/icon2.jpg")));
            }  else if (theme.equals("dark")) {
                imgView.setImage(new Image(new FileInputStream(System.getProperty("user.dir") + "/Resources/Images/darkicon.png")));
            }}
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSelectLanguage() {
        System.out.println("HI");
        if (languageComboBox.getValue().equals("English")) {
            rb = ResourceBundle.getBundle("sample.ResourceBundle", Locale.ENGLISH);
            changeText();
        } else if (languageComboBox.getValue().equals("Chinese")) {
            rb = ResourceBundle.getBundle("sample.ResourceBundle", new Locale("zh", "CN"));
            changeText();
        } else if (languageComboBox.getValue().equals("Malay")) {
            rb = ResourceBundle.getBundle("sample.ResourceBundle", new Locale("ms","MY"));
            changeText();
        } else if (languageComboBox.getValue().equals("French")) {
            rb = ResourceBundle.getBundle("sample.ResourceBundle", new Locale("fr", "FR"));
            changeText();
        }
    }

    public void changeText() {
        System.out.println(rb.getLocale().toLanguageTag());
        yoursLabel.setText(rb.getString("madebyyourstruly"));
        nameLabel.setText(rb.getString("name"));
        schoolLabel.setText(rb.getString("school"));
        classLabel.setText(rb.getString("class"));
    }
}
