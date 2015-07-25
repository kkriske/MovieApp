/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.controllers;

import com.sun.javafx.scene.control.skin.ContextMenuSkin;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import main.java.movieapp.util.SceneManager;
import main.java.movieapp.util.ThreadExecutor;

/**
 *
 * @author kristof
 */
public class ThumbnailContextMenu extends ContextMenu implements Initializable {

    private final Window owner;
    private MovieThumbnail current;
    @FXML
    private MenuItem imdbbutton, resetbutton, infobutton, reloadbutton;
    @FXML
    private TextField imdb;
    private InfoWindow infowindow;

    public ThumbnailContextMenu(Window owner) {
        this.owner = owner;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contextmenu.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("failed to load ThumbnailContextMenu");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ContextMenuSkin skin = new ContextMenuSkin(this);
        skin.getNode().setOnKeyPressed(e -> e.consume());
        setSkin(skin);

        imdb.setOnKeyPressed(e -> {
            String id = imdb.getText();
            if (KeyCode.ENTER.equals(e.getCode()) && id.matches("^tt[0-9]{7}$")) {
                hide();
                setIMDb(id);
            }
        });
        setOnHiding(e -> imdb.clear());
    }

    public synchronized void showMenu(MovieThumbnail movieThumbnail, double x, double y) {
        current = movieThumbnail;
        boolean hasIMDb = movieThumbnail.hasProp();
        imdbbutton.setVisible(!hasIMDb);
        resetbutton.setVisible(hasIMDb);
        infobutton.setVisible(hasIMDb);
        reloadbutton.setVisible(hasIMDb);
        show(owner, x, y);
    }

    @FXML
    private void play() {

    }

    @FXML
    private void info() {
        if (infowindow == null) {
            infowindow = new InfoWindow();
        }
        infowindow.init(current);
        SceneManager.nextRoot(infowindow);
    }

    @FXML
    private void opendir() throws IOException {
        Desktop.getDesktop().open(current.getMovie().getParentFile());
    }

    @FXML
    private void resetIMDb() {
        boolean success = current.removeProperties();
        resetbutton.setVisible(!success);
        imdbbutton.setVisible(success);
        infobutton.setVisible(!success);
        reloadbutton.setVisible(!success);
    }

    @FXML
    private void reloadIMDb() {
        final MovieThumbnail cur = current;
        ThreadExecutor.execute(() -> {
            String key = cur.getProp().getProperty("imdb");
            cur.setProperties(key);
        });
    }

    private void setIMDb(String id) {
        final MovieThumbnail cur = current;

        ThreadExecutor.execute(() -> {
            boolean success = cur.setProperties(id);
            Platform.runLater(() -> {
                imdbbutton.setVisible(!success);
                resetbutton.setVisible(success);
                infobutton.setVisible(success);
                reloadbutton.setVisible(success);
            });
        });
    }

}
