/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;

/**
 *
 * @author kristof
 */
public class MovieAppTab extends Tab implements Initializable {

    @FXML
    private FlowPane flowpane;
    /*private final List<MovieDir> content;
     private final MovieInfoOverlay overlay;
     private final StringProperty searchprop;
     private final List<MovieAppThumbnail> movies;
     private final VLCOverlay vlcOverlay;*/

    public MovieAppTab(String title, File dir) {
        super(title);
        /*this.content = content;
         this.overlay = overlay;
         this.vlcOverlay = vlcOverlay;
         this.searchprop = searchprop;
         movies = new ArrayList<>();
         try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/movieAppTab.fxml"));
         loader.setController(this);
         setContent(loader.load());
         } catch (IOException ex) {
         ex.printStackTrace();
         System.err.println("Failed to create MovieAppTab");
         }*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*content.stream().forEach(c -> {
         MovieAppThumbnail thumb = new MovieAppThumbnail(c, overlay, vlcOverlay);
         searchprop.addListener((s, o, n) -> thumb.filter(n));
         movies.add(thumb);
         });
         sort();*/

    }

    /*private void sort() {
     Collections.sort(movies, (n1, n2) -> {
     Properties p1 = ((MovieAppThumbnail) n1).getProps(),
     p2 = ((MovieAppThumbnail) n2).getProps();
     return p1 == null ? p2 == null ? 0 : Integer.MAX_VALUE : p2 == null ? Integer.MIN_VALUE : p2.getProperty("year").compareTo(p1.getProperty("year"));
     });
     flowpane.getChildren().clear();
     flowpane.getChildren().addAll(movies);
     }*/
}
