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
public class VLCOverlay {

    private final BorderPane root;
    private final SimpleVLCPlayer player;

    public VLCOverlay(Stage primstage, BorderPane root) {
        this.root = root;
        player = new SimpleVLCPlayer(primstage);

        root.widthProperty().addListener((s, o, n) -> player.setPrefWidth((double) n));
        root.heightProperty().addListener((s, o, n) -> player.setPrefHeight((double) n));

        /*parentProperty().addListener((s, o, n) -> {
         if (o != null) {
         maxHeightProperty().unbind();
         maxWidthProperty().unbind();
         }
         sout
         if (n != null && n instanceof Region) {
         maxHeightProperty().bind(((Region) n).heightProperty());
         maxWidthProperty().bind(((Region) n).widthProperty());
         }
         });*/
    }

    public void playMovie(File moviefile) {
        root.setCenter(player);
        player.play(moviefile.toString());
    }

}
