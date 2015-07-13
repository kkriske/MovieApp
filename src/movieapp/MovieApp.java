/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import provider.OMDbProvider;
import provider.Provider;

/**
 *
 * @author kristof
 */
public class MovieApp extends Application {

    private final int width = 1070, height = 700;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException, IOException {
        Provider.setProvider(new OMDbProvider());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/styling/movieApp.fxml"));
        loader.setController(new MovieAppController(primaryStage, "movieAppProperties.properties"));
        Parent root = loader.load();

        Scene scene = new Scene(root, width, height);
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
