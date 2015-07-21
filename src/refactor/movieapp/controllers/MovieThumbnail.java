/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javax.imageio.ImageIO;

/**
 *
 * @author kristof
 */
public class MovieThumbnail extends StackPane implements Initializable {

    private final File movie;
    private boolean loaded;
    private Properties prop;
    private static final String PROPERTIES_FILENAME = "movieproperties.properties";
    private static final String POSTER_FILENAME = "poster.jpg";

    @FXML
    private ImageView img;
    @FXML
    private Label title;

    public MovieThumbnail(File movie) {
        this.movie = movie;
        loaded = false;
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPrefSize(170, 290);
        //init properties
        File propfile = new File(movie.getParentFile(), PROPERTIES_FILENAME);
        if (propfile.isFile()) {
            prop = new Properties();
            try {
                prop.load(new FileInputStream(propfile));
            } catch (IOException ex) {
                System.err.println("Failed to load properties");
            }
        }
    }

    public synchronized boolean isLoaded() {
        return loaded;
    }

    public void load() {
        synchronized (this) {
            if (loaded) {
                return;
            }
            loaded = true;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("movieThumbnail.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("failed to create moviethumbnail");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    private void init() {
        new Thread(() -> {
            //init poster
            File posterfile = new File(movie.getParentFile(), POSTER_FILENAME);
            if (posterfile.isFile()) {
                Platform.runLater(() -> {
                    try {
                        img.setImage(new Image(new FileInputStream(posterfile)));
                    } catch (FileNotFoundException ex) {
                        System.err.println("failed to load image");
                    }
                });
            }

            //init text
            Platform.runLater(() -> title.setText(
                    prop == null
                            ? movie.getName()
                            : String.format("%s (%s)",
                                    prop.getProperty("title"),
                                    prop.getProperty("year"))));
        }).start();
    }

    public void delete() {
        img.setImage(null);
        title.setText(movie.getName());
    }

    public boolean setProperties(Properties properties) {
        if (properties != null) {
            prop = properties;
            try (FileOutputStream fo = new FileOutputStream(new File(movie.getParentFile(), PROPERTIES_FILENAME))) {
                prop.store(fo, null);
                BufferedImage img = ImageIO.read(new URL(properties.getProperty("poster")));
                ImageIO.write(img, "jpg", new File(movie.getParentFile(), POSTER_FILENAME));
            } catch (IOException ex) {
                System.err.println("Failed to write movieproperties.properties file");
            }
            return true;
        }
        return false;
    }

    public void filter(File dir, String genre, String filter) {
        boolean visible = filter == null || filter.isEmpty() || (prop == null ? movie.getName() : prop.getProperty("title")).toLowerCase().contains(filter);
        visible = visible && (genre == null || (prop == null ? "" : prop.getProperty("genre")).toLowerCase().contains(genre));
        final boolean v = visible && (dir == null || (dir.equals(movie.getParentFile().getParentFile())));

        Platform.runLater(() -> {
            setVisible(v);
            setManaged(v);
        });
    }

    public static abstract class Comparator implements java.util.Comparator<Node> {

        private static Comparator foryear;

        public static Comparator forYear() {
            if (foryear == null) {
                foryear = new Comparator("year", "year") {

                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareTo(s2);
                    }
                };
            }
            return foryear;
        }

        private static Comparator fortitle;

        public static Comparator forTitle() {
            if (fortitle == null) {
                fortitle = new Comparator("title", "title") {

                    @Override
                    public int compare(String s1, String s2) {
                        return s2.compareToIgnoreCase(s1);
                    }
                };
            }
            return fortitle;
        }

        private static Comparator forruntime;

        public static Comparator forRuntime() {
            if (forruntime == null) {
                forruntime = new Comparator("runtime", "runtime") {

                    @Override
                    public int compare(String s1, String s2) {
                        return new Integer(s1.split(" ")[0]).compareTo(new Integer(s2.split(" ")[0]));
                    }
                };
            }
            return forruntime;
        }

        private static Comparator forreleased;

        public static Comparator forReleased() {
            if (forreleased == null) {
                forreleased = new Comparator("released", "release date") {

                    private final Map<String, Integer> map;

                    {
                        map = new HashMap<>();
                        map.put("jan", 1);
                        map.put("feb", 2);
                        map.put("mar", 3);
                        map.put("apr", 4);
                        map.put("may", 5);
                        map.put("jun", 6);
                        map.put("jul", 7);
                        map.put("aug", 8);
                        map.put("sep", 9);
                        map.put("oct", 10);
                        map.put("nov", 11);
                        map.put("dec", 12);
                    }

                    @Override
                    public int compare(String s1, String s2) {
                        String[] a1 = s1.split(" "),
                                a2 = s2.split(" ");
                        try {
                            return LocalDate.of(Integer.parseInt(a1[2]), map.get(a1[1].toLowerCase()), Integer.parseInt(a1[0]))
                                    .compareTo(LocalDate.of(Integer.parseInt(a2[2]), map.get(a2[1].toLowerCase()), Integer.parseInt(a2[0])));
                        } catch (Exception ex) {
                            System.out.println(s1 + " " + s2);
                        }
                        return 0;
                    }
                };
            }
            return forreleased;
        }

        private final String propname, tostring;

        private Comparator(String propname, String tostring) {
            this.propname = propname;
            this.tostring = tostring;
        }

        @Override
        public int compare(Node o1, Node o2) {
            Properties p1 = ((MovieThumbnail) o1).prop,
                    p2 = ((MovieThumbnail) o2).prop;
            return p1 == null
                    ? p2 == null
                            ? 0
                            : Integer.MAX_VALUE
                    : p2 == null
                            ? Integer.MIN_VALUE
                            : comp(p1.getProperty(propname), p2.getProperty(propname));
        }

        abstract public int compare(String s1, String s2);

        @Override
        public String toString() {
            return tostring;
        }

        private int comp(String p1, String p2) {
            return p1 == null || p1.equalsIgnoreCase("N/A")
                    ? p2 == null || p2.equalsIgnoreCase("N/A")
                            ? 0
                            : Integer.MAX_VALUE
                    : p2 == null || p2.equalsIgnoreCase("N/A")
                            ? Integer.MIN_VALUE
                            : compare(p2, p1);
        }

    }

}
