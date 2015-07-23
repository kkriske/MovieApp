/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import refactor.movieapp.util.SceneManager;

/**
 *
 * @author kristof
 */
public class InfoWindow extends AnchorPane implements Initializable {

    @FXML
    private ImageView close, img;
    @FXML
    private Label runtime, genre, language, rating, imdb, plot;
    @FXML
    private Text title, year;
    @FXML
    private AnchorPane infopane;
    private DoubleProperty imgheight, imgwidth;

    public InfoWindow() {
        imgheight = new SimpleDoubleProperty(0);
        imgwidth = new SimpleDoubleProperty(0);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("infowindow.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            System.err.println("failed to load InfoWindow");
        }
    }

    public void init(MovieThumbnail current) {
        Properties prop = current.getProp();
        title.setText(prop.getProperty("title"));
        year.setText(prop.getProperty("year"));
        runtime.setText(prop.getProperty("runtime"));
        genre.setText(prop.getProperty("genre"));
        language.setText(prop.getProperty("language"));
        rating.setText(prop.getProperty("rating"));
        imdb.setText(prop.getProperty("imdb"));
        plot.setText(prop.getProperty("plot"));

        try (FileInputStream fs = new FileInputStream(current.getPosterFile())) {
            img.setImage(new Image(fs));
        } catch (IOException ex) {
            System.err.println("Failed to init MovieInfoOverlay");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (KeyCode.ESCAPE.equals(e.getCode())) {
                close();
            }
        });
        sceneProperty().addListener((s, o, n) -> {
            if (n != null) {
                requestFocus();
            }
        });
        img.imageProperty().addListener((s, o, n) -> {
            if (n != null) {
                imgheight.set(n.getHeight());
            }
        });
        img.fitHeightProperty().bind(Bindings.min(Bindings.subtract(heightProperty(), 40), imgheight));
        img.fitHeightProperty().addListener((s, o, n) -> imgwidth.set(img.prefWidth((double) n) + 20));
        infopane.prefWidthProperty().bind(Bindings.subtract(widthProperty(), imgwidth));
    }

    @FXML
    private void close() {
        SceneManager.previousRoot();
    }

    @FXML
    private void openIMDb() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.imdb.com/title/" + imdb.getText()));
        } catch (URISyntaxException | IOException ex) {
            System.err.println("Failed to initialize MovieInfoOverlay");
        }
    }
}
