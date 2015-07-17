/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author kristof
 */
public class MovieAppController implements Initializable {

    private final List<String> extensions;
    private final Properties properties;
    @FXML
    private TabPane root;
    @FXML
    private BorderPane overlayRoot;
    @FXML
    private TextField searchbox;
    @FXML
    private ImageView resetsearch;
    private final Stage primstage;

    public MovieAppController(Stage primstage, String propertieslocation) throws IOException {
        this.primstage = primstage;
        properties = new Properties();
        properties.load(getClass().getResourceAsStream(propertieslocation));
        extensions = Arrays.asList(properties.getProperty("extensions").split(",[ ]*"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MovieInfoOverlay overlay = new MovieInfoOverlay(overlayRoot);
        VLCOverlay vlcOverlay = new VLCOverlay(primstage, overlayRoot);
        resetsearch.setOnMouseClicked(e -> searchbox.clear());
        new Thread(() -> {
            for (String s : properties.getProperty("film_directories").split(",[ ]*")) {
                List<MovieDir> filterred = filter(s);
                String[] split = s.split("\\\\");
                Platform.runLater(() -> root.getTabs().add(new MovieAppTab(split[split.length - 1], filterred, overlay, vlcOverlay, searchbox.textProperty())));
            }
        }).start();
        root.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                if (KeyCode.F.equals(e.getCode())) {
                    searchbox.requestFocus();
                } else if (KeyCode.TAB.equals(e.getCode())) {
                    if (e.isShiftDown()) {
                        root.getSelectionModel().selectNext();
                    } else {
                        root.getSelectionModel().selectPrevious();
                    }
                }
            }
        });
        searchbox.setOnKeyPressed(e -> {
            if (KeyCode.ESCAPE.equals(e.getCode()) || KeyCode.ENTER.equals(e.getCode())) {
                root.requestFocus();
            }
        });
    }

    private List<MovieDir> filter(String dir) {
        String vlc = properties.getProperty("VLC_executable");
        File directory = new File(dir);
        List<MovieDir> list = new ArrayList<>();
        for (String name : directory.list()) {
            File subdir = new File(directory, name);//normaal filmdirectory
            if (subdir.isFile()) {
                continue;
            }
            /*List<String> subnames = Arrays.asList(subdir.list());
             if (subnames.contains("movieproperties.properties")) {
             list.add(new MovieDir(vlc, directory, subdir, true));
             } else*/ {
                boolean found1 = false;
                List<File> subdirs = new ArrayList<>();
                for (String n : subdir.list()) {
                    if (!n.split("\\.")[0].toLowerCase().equals("sample")) {
                        File f = new File(subdir, n);//potentieel moviefile
                        if (f.isDirectory()) {
                            subdirs.add(f);
                        } else {
                            String[] split = n.split("\\.");
                            if (split.length > 0 && extensions.contains(split[split.length - 1].toLowerCase())) {//moviefile
                                list.add(new MovieDir(vlc, subdir, f));
                                found1 = true;
                                break;
                            }
                        }
                    }
                }
                if (!found1) {
                    for (File f1 : subdirs) {
                        /*List<String> subsubnames = Arrays.asList(f1.list());
                         if (subsubnames.contains("movieproperties.properties")) {
                         list.add(new MovieDir(vlc, f1, f1, true));
                         } else*/ {
                            for (String n : f1.list()) {
                                if (!n.split("\\.")[0].toLowerCase().equals("sample")) {
                                    File f2 = new File(f1, n);//potentieel moviefile
                                    if (f2.isFile()) {
                                        String[] split = n.split("\\.");
                                        if (split.length > 0 && extensions.contains(split[split.length - 1].toLowerCase())) {//moviefile
                                            list.add(new MovieDir(vlc, f1, f2));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(list.size());
        return list;
    }

}
