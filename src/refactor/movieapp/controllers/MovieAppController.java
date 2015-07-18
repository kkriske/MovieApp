/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import refactor.movieapp.util.SceneManager;
import refactor.movieapp.util.Settings;

/**
 *
 * @author kristof
 */
public class MovieAppController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TextField searchbox;
    @FXML
    private ImageView resetsearch;
    @FXML
    private FlowPane flowpane;

    private AnchorPane settingsroot;

    public MovieAppController() throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetsearch.setOnMouseClicked(e -> searchbox.clear());
        root.setOnMouseClicked(e -> root.requestFocus());
        root.sceneProperty().addListener((s, o, n) -> {
            if (n != null) {
                init();
                root.requestFocus();
            }
        });
    }

    @FXML
    private void openSettings() {
        try {
            if (settingsroot == null) {
                settingsroot = FXMLLoader.load(getClass().getResource("settingswindow.fxml"));
            }
            SceneManager.nextRoot(settingsroot);
        } catch (IOException ex) {
            System.out.println("failed to create settingswindow");
        }
    }

    private void init() {
        Settings.getDirectories()
                .parallelStream()
                .map(rootdir -> new File(rootdir))
                .filter(rootdir -> rootdir.isDirectory())
                .forEach(rootdir -> Arrays.stream(rootdir.listFiles())
                        .parallel()
                        .filter(moviedir -> moviedir.isDirectory())
                        .forEach(moviedir -> Arrays.stream(moviedir.listFiles())
                                .filter(movie -> movie.isFile())
                                .map(movie -> movie.getName())
                                .filter(movie -> movie.contains("."))
                                .filter(movie -> !movie.split("\\.")[0].toLowerCase().equals("sample"))
                                .filter(movie -> Settings.getExtensions().contains(movie.substring(movie.lastIndexOf(".") + 1)))
                                .map(movie -> new File(moviedir, movie))
                                .forEach(movie -> Platform.runLater(() -> flowpane.getChildren().add(new MovieThumbnail(movie))))
                        )
                );
    }

}
