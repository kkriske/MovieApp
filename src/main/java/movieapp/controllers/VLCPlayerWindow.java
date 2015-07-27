/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.controllers;

import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.java.movieapp.util.SceneManager;
import simplevlc.SimpleVLCPlayer;

/**
 *
 * @author kristof
 */
public class VLCPlayerWindow extends SimpleVLCPlayer {

    private final Label title;
    private final ImageView close;
    private final Stage primstage;

    public VLCPlayerWindow(Stage primstage) {
        super(primstage);
        this.primstage = primstage;
        title = new Label();
        title.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(title, 2d);
        AnchorPane.setLeftAnchor(title, 28d);
        AnchorPane.setBottomAnchor(title, 2d);
        AnchorPane.setRightAnchor(title, 28d);

        close = new ImageView(new Image("/main/java/resources/close.png"));
        close.setOnMouseClicked(e -> exit());
        close.setFitHeight(24);
        close.setFitWidth(24);
        close.getStyleClass().add("hoverbutton");
        close.setPickOnBounds(true);
        AnchorPane.setRightAnchor(close, 2d);
        AnchorPane.setTopAnchor(close, 2d);
        AnchorPane.setBottomAnchor(close, 2d);

        AnchorPane topbar = new AnchorPane(title, close);
        topbar.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        topbar.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        topbar.setPrefSize(USE_COMPUTED_SIZE, 28);
        AnchorPane.setTopAnchor(topbar, 0d);
        AnchorPane.setLeftAnchor(topbar, 0d);
        AnchorPane.setRightAnchor(topbar, 0d);

        getControls().getChildren().add(topbar);

        setOnKeyPressed(e -> {
            if (KeyCode.ESCAPE.equals(e.getCode())) {
                exit();
            }
        });

        topbar.visibleProperty().bind(controlsVisibleProperty());
        topbar.opacityProperty().bind(controlsOpacityProperty());
    }

    public void play(File file, String title) {
        this.title.setText(title);
        play(file.getAbsolutePath());
    }

    private void exit() {
        primstage.setFullScreen(false);
        setPause(true);
        stop();
        SceneManager.previousRoot();
    }

}
