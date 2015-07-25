/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.java.movieapp.provider.OMDbProvider;
import main.java.movieapp.provider.Provider;
import main.java.movieapp.util.SceneManager;
import main.java.movieapp.util.ThreadExecutor;

/**
 *
 * @author kristof
 */
public class MovieApp extends Application {

    private final int width = 1070, height = 700;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/main/java/movieapp/controllers/movieApp.fxml"));
        Scene scene = new Scene(root, width, height);
        primaryStage.getIcons().add(new Image("/main/java/resources/icon.png"));

        //init static classes
        Provider.setProvider(new OMDbProvider());
        SceneManager.setScene(scene);

        primaryStage.setOnCloseRequest(e -> ThreadExecutor.shutdown());

        primaryStage.setTitle("Movies");
        primaryStage.setMinHeight(height + 39);
        primaryStage.setMinWidth(width + 16);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
