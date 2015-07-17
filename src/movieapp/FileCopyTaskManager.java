/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author kristof
 */
public class FileCopyTaskManager extends Stage {

    private static FileCopyTaskManager instance;
    @FXML
    private VBox box;

    public static FileCopyTaskManager getInstance() {
        if (instance == null) {
            instance = new FileCopyTaskManager();
        }
        return instance;
    }

    private FileCopyTaskManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/styling/fileCopyTaskManager.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());
            setScene(scene);
            setResizable(false);
        } catch (IOException ex) {
            System.err.println("failed to create FileCopyTaskManager");
        }
    }

    public void addTask(FileCopyTask task) {
        if (!isShowing()) {
            show();
        }
        box.getChildren().add(task);
        task.startTask();
    }

}
