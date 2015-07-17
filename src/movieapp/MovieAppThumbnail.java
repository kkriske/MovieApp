/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import com.sun.javafx.scene.control.skin.ContextMenuSkin;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import provider.Provider;

/**
 *
 * @author kristof
 */
public class MovieAppThumbnail extends StackPane implements Initializable {

    @FXML
    private ImageView img;
    @FXML
    private Label text;
    @FXML
    private MenuItem playbutton, vlctestplaybutton, resetbutton, infobutton, openbutton, dummy;
    @FXML
    private CustomMenuItem imdbbutton;
    @FXML
    private ContextMenu contextmenu;
    @FXML
    private Menu copymenu;
    @FXML
    private TextField imdb;
    private final MovieDir movieDir;
    private final MovieInfoOverlay infoOverlay;
    private final VLCOverlay vlcOverlay;

    public MovieAppThumbnail(MovieDir movieDir, MovieInfoOverlay infoOverlay, VLCOverlay vlcOverlay) {
        this.movieDir = movieDir;
        this.infoOverlay = infoOverlay;
        this.vlcOverlay = vlcOverlay;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/movieAppThumbnail.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            System.err.println("Failed to load MovieAppThumbnail");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //init button visibility
        boolean hasimdb = movieDir.getProperties() != null;
        resetbutton.setVisible(hasimdb);
        imdbbutton.setVisible(!hasimdb);
        infobutton.setVisible(hasimdb);

        visibleProperty().bindBidirectional(managedProperty());
        copymenu.setOnShowing(a -> {
            List<StorageDevice> removableDevices = StorageDeviceDetector.getInstance().getRemovableDevices();
            copymenu.getItems().clear();
            if (removableDevices.isEmpty()) {
                copymenu.getItems().add(dummy);
            } else {
                removableDevices.stream().forEach(d -> {
                    MenuItem device = new MenuItem(d.getSystemDisplayName());
                    device.setOnAction(e -> {
                        FileCopyTaskManager.getInstance().addTask(new FileCopyTask(movieDir.getMoviefile(), d.getRootDirectory(), movieDir.getMoviefileName()));

                        /*ProgressBar bar = new ProgressBar(0);
                         final SimpleDoubleProperty prop = new SimpleDoubleProperty(0);
                         bar.progressProperty().bind(prop);
                         if (d.getRootDirectory().exists()) {
                         Runnable task = () -> {
                         double count = 0.01;
                         try (FileInputStream is = new FileInputStream(movieDir.getMoviefile());
                         FileOutputStream os = new FileOutputStream(new File(d.getRootDirectory(), movieDir.getMoviefileName()))) {
                         long totalsize = movieDir.getMoviefile().length();
                         long coppiedsize = 0;
                         byte[] buffer = new byte[1024];
                         int length = 0;
                         while ((length = is.read(buffer)) > 0) {
                         os.write(buffer, 0, length);

                         coppiedsize += length;
                         System.out.println(totalsize - coppiedsize);
                         double val = (double) coppiedsize / (double) totalsize;
                         if (val > count) {
                         count += 0.01;
                         os.getFD().sync();
                         }
                         prop.setValue(val);
                         }

                         } catch (IOException ex) {
                         System.err.println("Failed to copy files");
                         }
                         System.out.println("very last thing in run");
                         };
                         /*Task<Void> task = new Task<Void>() {

                         @Override
                         protected Void call() throws Exception {
                         try (InputStream is = new FileInputStream(movieDir.getMoviefile());
                         OutputStream os = new FileOutputStream(new File(d.getRootDirectory(), movieDir.getMoviefileName()))) {
                         long totalsize = movieDir.getMoviefile().length();
                         long coppiedsize = 0;
                         byte[] buffer = new byte[1024];
                         int length = 0;
                         while ((length = is.read(buffer)) > 0) {
                         os.write(buffer, 0, length);
                         coppiedsize += length;
                         System.out.println(coppiedsize);
                         updateProgress(coppiedsize, totalsize);
                         }
                         /*Runtime.getRuntime().exec(
                         String.format("cmd /C copy \"%s\" \"%s\"",
                         movieDir.getMoviefile().getPath(),
                         d.getRootDirectory().toString()));*/
                        //Files.copy(movieDir.getMoviefile().toPath(), new File(d.getRootDirectory(), movieDir.getMoviefileName()).toPath());
                                    /*} catch (IOException ex) {
                         ex.printStackTrace();
                         System.err.println("Failed to copy files");
                         }
                         return null;
                         }
                         };*/

                        /*Label label = new Label();
                         prop.addListener((s, o, n) -> Platform.runLater(() -> label.setText(n.toString())));
                         Stage stage = new Stage();
                         stage.setScene(new Scene(new VBox(bar, label)));
                         stage.show();
                         stage.setOnCloseRequest(q -> bar.progressProperty().unbind());
                         new Thread(task).start();
                         }*/
                    });
                    copymenu.getItems().add(device);
                });
            }
        }
        );

        //consume clickevents on contextmenu
        ContextMenuSkin skin = new ContextMenuSkin(contextmenu);

        skin.getNode().setOnKeyPressed(e -> e.consume());
        contextmenu.setSkin(skin);

        imdb.setOnKeyPressed(e -> {
            String txt = imdb.getText();
            if (KeyCode.ENTER.equals(e.getCode()) && txt.matches("^tt[0-9]{7}$")) {
                contextmenu.hide();
                new Thread(() -> getIMDb(txt)).start();
            }
        }
        );
        playbutton.setOnAction(e -> movieDir.playMovie());
        vlctestplaybutton.setOnAction(e -> vlcOverlay.playMovie(movieDir.getMoviefile()));
        infobutton.setOnAction(e -> infoOverlay.init(movieDir));
        resetbutton.setOnAction(e -> resetIMDb());
        openbutton.setOnAction(e -> {
            try {
                Runtime.getRuntime().exec(String.format("explorer.exe /select, \"%s\"", movieDir.getMoviefile()));
            } catch (IOException ex) {
                System.err.println("Failed to open explorer");
            }
        }
        );
        contextmenu.setOnShowing(e -> imdb.clear());
        setOnMouseClicked(e -> {
            if (MouseButton.PRIMARY.equals(e.getButton()) && e.getClickCount() == 2) {
                movieDir.playMovie();
            }
        }
        );
        initThumb(movieDir.getProperties() == null);
    }

    private void getIMDb(String id) {
        boolean success = movieDir.setProperties(Provider.getProvider().getProperties(id));
        Platform.runLater(() -> {
            resetbutton.setVisible(success);
            imdbbutton.setVisible(!success);
            infobutton.setVisible(success);
            if (success) {
                initThumb(false);
            }
        });

    }

    private void resetIMDb() {
        File props = movieDir.getPropfile();
        File poster = movieDir.getPoster();
        boolean success = props.delete();
        if (success) {
            initThumb(true);
        }
        poster.delete();
        resetbutton.setVisible(!success);
        imdbbutton.setVisible(success);
        infobutton.setVisible(!success);
    }

    private void initThumb(boolean delete) {
        if (delete) {
            img.setImage(null);
            text.setText(movieDir.getMoviefile().getPath().replaceAll("^.*\\\\([^\\\\]+)$", "$1"));
        } else {
            try (FileInputStream fs = new FileInputStream(movieDir.getPoster())) {
                img.setImage(new Image(fs));
                text.setText(String.format("%s (%s)",
                        movieDir.getProperties().getProperty("title"),
                        movieDir.getProperties().getProperty("year")));
            } catch (IOException ex) {
                System.err.println("Init Thumbnail failed");
            }
        }
    }

    public void filter(String n) {
        setVisible(text.getText().toLowerCase().contains(n.toLowerCase()));
    }

    public Properties getProps() {
        return movieDir.getProperties();
    }
}
