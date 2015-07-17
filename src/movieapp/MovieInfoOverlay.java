/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author kristof
 */
public class MovieInfoOverlay extends AnchorPane implements Initializable {

    private final BorderPane root;
    @FXML
    private ImageView close, img;
    @FXML
    private Label title, year, runtime, genre, language, rating, imdb, plot;

    public MovieInfoOverlay(BorderPane root) {
        this.root = root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/movieInfoOverlay.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            System.err.println("Failed to create MovieInfoOverlay");
        }
    }

    public void init(MovieDir movieDir) {
        Properties prop = movieDir.getProperties();
        title.setText(prop.getProperty("title"));
        year.setText(prop.getProperty("year"));
        runtime.setText(prop.getProperty("runtime"));
        genre.setText(prop.getProperty("genre"));
        language.setText(prop.getProperty("language"));
        rating.setText(prop.getProperty("rating"));
        imdb.setText(prop.getProperty("imdb"));
        plot.setText(prop.getProperty("plot"));

        try (FileInputStream fs = new FileInputStream(movieDir.getPoster().toString())) {
            img.setImage(new Image(fs));
        } catch (IOException ex) {
            System.err.println("Failed to init MovieInfoOverlay");
        }
        root.setCenter(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        close.setOnMouseClicked(e -> {
            root.setCenter(null);
            img.setImage(null);
        });
        img.setFitHeight(615);
        img.setFitWidth(428);
        imdb.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.imdb.com/title/" + imdb.getText()));
            } catch (URISyntaxException | IOException ex) {
                System.err.println("Failed to initialize MovieInfoOverlay");
            }
        });
    }

}
