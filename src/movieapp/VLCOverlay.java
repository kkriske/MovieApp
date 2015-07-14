/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.File;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import simplevlc.SimpleVLCPlayer;

/**
 *
 * @author kristof
 */
/*public class VLCOverlay {

 private final BorderPane root;
 private final SimpleVLCPlayer player;

 public VLCOverlay(Stage primstage, BorderPane root) {
 this.root = root;
 player = new SimpleVLCPlayer(primstage);

 root.widthProperty().addListener((s, o, n) -> player.setPrefWidth((double) n));
 root.heightProperty().addListener((s, o, n) -> player.setPrefHeight((double) n));

 }

 public void playMovie(File moviefile) {
 root.setCenter(player);
 player.play(moviefile.toString());
 }

 }*/
public class VLCOverlay extends SimpleVLCPlayer {

    private final BorderPane root;

    public VLCOverlay(Stage primstage, BorderPane root) {
        super(primstage);
        this.root = root;

        prefWidthProperty().bind(root.widthProperty());
        prefHeightProperty().bind(root.heightProperty());
    }

    public void playMovie(File moviefile) {
        root.setCenter(this);
        play(moviefile.toString());
    }

}
