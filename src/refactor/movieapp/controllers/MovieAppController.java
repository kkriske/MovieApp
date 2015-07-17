/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import refactor.movieapp.util.SceneManager;

/**
 *
 * @author kristof
 */
public class MovieAppController implements Initializable {

    @FXML
    private TabPane tabpane;

    @FXML
    private TextField searchbox;

    @FXML
    private ImageView resetsearch, settings;

    private AnchorPane settingsroot;

    public MovieAppController() throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetsearch.setOnMouseClicked(e -> searchbox.clear());
        tabpane.getSelectionModel().selectedItemProperty().addListener((s, o, n) -> searchbox.clear());
        settings.setOnMouseClicked(e -> {
            try {
                if (settingsroot == null) {
                    settingsroot = FXMLLoader.load(getClass().getResource("settingswindow.fxml"));
                }
                SceneManager.nextRoot(settingsroot);
            } catch (IOException ex) {
                System.out.println("failed to create settingswindow");
            }
        });
    }

}
