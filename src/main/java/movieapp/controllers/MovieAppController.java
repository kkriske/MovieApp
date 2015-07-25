/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import main.java.movieapp.controllers.MovieThumbnail.Comparator;
import main.java.movieapp.util.SceneManager;
import main.java.movieapp.util.Settings;
import main.java.movieapp.util.ThreadExecutor;

/**
 *
 * @author kristof
 */
public class MovieAppController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TextField searchbox;
    @FXML
    private ImageView resetsearch;
    @FXML
    private FlowPane flowpane;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private ComboBox<Comparator> sortbox;
    @FXML
    private ComboBox<File> dirbox;
    @FXML
    private ComboBox<String> genrebox;

    private final static String[] genres = new String[]{
        null, "Action", "Adventure", "Animation",
        "Biography", "Comedy", "Crime", "Documentary",
        "Drama", "Family", "Fantasy", "Film-Noir",
        "Game-Show", "History", "Horror", "Music",
        "Musical", "Mystery", "News", "Reality-TV",
        "Romance", "Sci-Fi", "Sport", "Talk-Show",
        "Thriller", "War", "Western"
    };

    private AnchorPane settingsroot;
    private ThumbnailContextMenu contextmenu;
    private boolean changed;
    private final List<MovieThumbnail> tobeloaded, thumbnails;

    public MovieAppController() throws IOException {
        changed = true;
        tobeloaded = new ArrayList<>();
        thumbnails = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetsearch.setOnMouseClicked(e -> {
            searchbox.clear();
        });
        searchbox.setOnKeyPressed(e -> {
            if (KeyCode.ESCAPE.equals(e.getCode())) {
                searchbox.clear();
            }
        });
        root.setOnMouseClicked(e -> {
            root.requestFocus();
            if (MouseButton.SECONDARY.equals(e.getButton())) {
                if (e.getTarget() instanceof MovieThumbnail) {
                    if (contextmenu == null) {
                        contextmenu = new ThumbnailContextMenu(MovieAppController.this.root.getScene().getWindow());
                    }
                    contextmenu.showMenu((MovieThumbnail) e.getTarget(), e.getScreenX(), e.getScreenY());
                }
            }

        });
        Map<KeyCode, EventHandler<KeyEvent>> map = new HashMap<>();
        map.put(KeyCode.F, e -> searchbox.requestFocus());
        map.put(KeyCode.S, e -> sortbox.show());
        map.put(KeyCode.D, e -> dirbox.show());
        map.put(KeyCode.G, e -> genrebox.show());
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (KeyCode.ESCAPE.equals(e.getCode()) || KeyCode.ENTER.equals(e.getCode())) {
                root.requestFocus();
            } else if (e.isControlDown() && map.containsKey(e.getCode())) {
                root.requestFocus();
                sortbox.hide();
                dirbox.hide();
                genrebox.hide();
                map.get(e.getCode()).handle(e);
            }
        });
        sortbox.setOnHidden(e -> root.requestFocus());
        dirbox.setOnHidden(e -> root.requestFocus());
        genrebox.setOnHidden(e -> root.requestFocus());

        root.sceneProperty().addListener((s, o, n) -> {
            if (n != null) {
                init();
                root.requestFocus();
            }
        });
        Settings.getDirectories().addListener((Change<? extends String> change) -> changed = true);

        //init comboboxes
        sortbox.getItems().addAll(
                Comparator.forYear(),
                Comparator.forTitle(),
                Comparator.forReleased(),
                Comparator.forRuntime()
        );
        sortbox.getSelectionModel().select(Comparator.forYear());

        dirbox.setButtonCell(new NullForAllListCell<>());
        dirbox.setCellFactory(param -> new NullForAllListCell<>());

        dirbox.getItems().add(null);
        dirbox.getSelectionModel().select(null);

        genrebox.setButtonCell(new NullForAllListCell<>());
        genrebox.setCellFactory(param -> new NullForAllListCell<>());

        genrebox.getItems().addAll(genres);
        genrebox.getSelectionModel().select(null);

        //init listeners for lazy loading
        scrollpane.vvalueProperty().addListener(e -> updateVisible());
        scrollpane.heightProperty().addListener(e -> updateVisible());

        //init listeners for filtering and sorting
        sortbox.getSelectionModel().selectedItemProperty().addListener((s, o, n) -> refreshOverview(true, false, false));
        dirbox.getSelectionModel().selectedItemProperty().addListener((s, o, n) -> refreshOverview(false, true, false));
        genrebox.getSelectionModel().selectedItemProperty().addListener((s, o, n) -> refreshOverview(false, true, false));
        searchbox.textProperty().addListener((s, o, n) -> refreshOverview(false, true, true));
    }

    @FXML
    private void openSettings() {
        try {
            if (settingsroot == null) {
                settingsroot = FXMLLoader.load(getClass().getResource("settingswindow.fxml"));
            }
            SceneManager.nextRoot(settingsroot);
        } catch (IOException ex) {
            System.out.println("failed to create settingswindow");
        }
    }

    private synchronized void init() {

        ThreadExecutor.execute(() -> {
            if (changed) {
                changed = false;
                thumbnails.clear();
                tobeloaded.clear();
                Platform.runLater(() -> {
                    dirbox.getItems().clear();
                    dirbox.getItems().add(null);
                });
                ObservableSet<String> extensions = Settings.getExtensions();
                Settings.getDirectories()
                        .stream()
                        .map(rootdir -> new File(rootdir))
                        .filter(File::isDirectory)
                        .map(rootdir -> {
                            Platform.runLater(() -> dirbox.getItems().add(rootdir));
                            return rootdir.listFiles();
                        })
                        .map(Arrays::stream)
                        .forEach(moviedirstream -> moviedirstream
                                .filter(File::isDirectory)
                                .map(File::listFiles)
                                .map(Arrays::stream)
                                .forEach(moviefilestream -> moviefilestream
                                        .filter(File::isFile)
                                        .filter(movie -> movie.getName().contains("."))
                                        .filter(movie -> !movie.getName().split("\\.")[0].toLowerCase().equals("sample"))
                                        .filter(movie -> extensions.contains(movie.getName().substring(movie.getName().lastIndexOf(".") + 1)))
                                        .forEach(movie -> {
                                            MovieThumbnail thumb = new MovieThumbnail(movie);
                                            thumb.setLayoutY(1.0);//workaround to force the property to update
                                            thumb.layoutYProperty().addListener(e -> checkVisible(thumb));
                                            thumb.getPropProperty().addListener((s, o, n) -> refreshOverview(true, false, false));
                                            tobeloaded.add(thumb);
                                        })
                                )
                        );
                thumbnails.addAll(tobeloaded);
            }
            Platform.runLater(() -> {
                sortbox.getSelectionModel().select(Comparator.forYear());
                dirbox.getSelectionModel().select(null);
                genrebox.getSelectionModel().select(null);
                searchbox.clear();
                refreshOverview(true, false, false);
            });
        });

    }

    private void refreshOverview(boolean sort, boolean filter, boolean updateVisible) {
        ThreadExecutor.execute(() -> {
            if (sort) {
                thumbnails.sort(sortbox.getSelectionModel().getSelectedItem());
                Platform.runLater(() -> flowpane.getChildren().setAll(thumbnails));
            }
            if (filter) {
                String f = searchbox.getText().toLowerCase();
                File dir = dirbox.getSelectionModel().getSelectedItem();
                String g = genrebox.getSelectionModel().getSelectedItem();
                if (g != null) {
                    g = g.toLowerCase();
                }
                final String gg = g;
                thumbnails.stream().forEach(m -> m.filter(dir, gg, f));
            }
            if (updateVisible) {
                Platform.runLater(() -> updateVisible());
            }
        });
    }

    private void updateVisible() {
        if (!tobeloaded.isEmpty()) {
            List<MovieThumbnail> tmp = new ArrayList<>();
            tobeloaded.stream().forEach(thumb -> {
                if (checkVisible(thumb)) {
                    //tmp.add(thumb);
                }
            });
            //tobeloaded.removeAll(tmp);
        }
    }

    private boolean checkVisible(MovieThumbnail thumb) {
        if (thumb.isVisible()) {
            double x1 = scrollpane.getVvalue() * (flowpane.getHeight() - scrollpane.getHeight());
            double x2 = x1 + scrollpane.getHeight();
            x1 -= 300;
            x2 += 300;
            double y1 = thumb.getLayoutY();
            double y2 = y1 + thumb.getHeight();
            if (y2 > y1 && x1 <= y2 && y1 <= x2) {
                if (thumb.isLoaded()) {
                    thumb.loadImg();
                } else {
                    thumb.load();
                }
            } else {
                if (thumb.isLoaded()) {
                    thumb.unloadImg();
                }
            }
        }
        return thumb.isLoaded();

    }

    private class NullForAllListCell<T> extends ListCell<T> {

        public NullForAllListCell() {
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                setText(item == null ? "all" : item instanceof File ? ((File) item).getName() : item.toString());
            }
        }
    }

}
