/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javax.imageio.ImageIO;
import main.java.movieapp.provider.Provider;
import main.java.movieapp.util.ThreadExecutor;

/**
 *
 * @author kristof
 */
public class MovieThumbnail extends AnchorPane implements Initializable {

    private final File movie;
    private boolean loaded;
    private ObjectProperty<Properties> prop;
    private static final String PROPERTIES_FILENAME = "movieproperties.properties";
    private static final String POSTER_FILENAME = "poster.jpg";

    @FXML
    private ImageView img;
    @FXML
    private Label title;
    private boolean imgLoaded;
    private File posterfile;
    private File propfile;

    public MovieThumbnail(File movie) {
        this.movie = movie;
        loaded = false;
        imgLoaded = false;
        prop = new SimpleObjectProperty<>(null);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPrefSize(170, 290);
        //init properties
        if (getPropfile().isFile()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(propfile));
                prop.set(properties);
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

    public final File getPosterFile() {
        if (posterfile == null) {
            posterfile = new File(movie.getParentFile(), POSTER_FILENAME);
        }
        return posterfile;
    }

    public final File getPropfile() {
        if (propfile == null) {
            propfile = new File(movie.getParentFile(), PROPERTIES_FILENAME);
        }
        return propfile;
    }

    private void init() {
        ThreadExecutor.execute(() -> {
            //init poster
            if (prop != null && getPosterFile().isFile()) {
                loadImg();
            } else {
                unloadImg();
            }

            //init text
            Properties properties = prop.get();
            Platform.runLater(() -> title.setText(
                    properties == null
                            ? movie.getName()
                            : String.format("%s (%s)",
                                    properties.getProperty("title"),
                                    properties.getProperty("year"))));
        });
    }

    public boolean setProperties(String imdb) {
        try {
            if (imdb == null || imdb.isEmpty()) {
                return false;
            } else if (imdb.matches("^tt[0-9]{7}$")) {
                Properties properties = Provider.getProvider().getProperties(imdb);
                if (properties == null) {
                    return false;
                }
                try (FileOutputStream fo = new FileOutputStream(getPropfile())) {
                    properties.store(fo, null);
                    BufferedImage bufimg = ImageIO.read(new URL(properties.getProperty("poster")));
                    ImageIO.write(bufimg, "jpg", getPosterFile());
                } catch (IOException ex) {
                    System.err.println("failed to write movieproperties.properties file");
                }
                prop.set(properties);
                init();
                return true;
            }
        } catch (ConnectException ex) {
            System.err.println(ex);
        }
        return false;
    }

    public boolean removeProperties() {
        boolean success = getPropfile().delete();
        if (success) {
            getPosterFile().delete();
            prop.set(null);
            init();
        }
        return success;
    }

    public boolean hasProp() {
        return prop.get() != null;
    }

    public Properties getProp() {
        return prop.get();
    }

    public ObjectProperty<Properties> getPropProperty() {
        return prop;
    }

    public File getMovie() {
        return movie;
    }

    public void filter(File dir, String genre, String filter) {
        Properties properties = prop.get();
        boolean visible = filter == null || filter.isEmpty()
                || (filter.matches("^tt[0-9]{7}$") && properties != null && properties.getProperty("imdb").toLowerCase().equals(filter))
                || (properties == null ? movie.getName() : properties.getProperty("title")).toLowerCase().contains(filter);
        visible = visible && (genre == null || (properties == null ? "" : properties.getProperty("genre")).toLowerCase().contains(genre));
        final boolean v = visible && (dir == null || (dir.equals(movie.getParentFile().getParentFile())));

        Platform.runLater(() -> {
            setVisible(v);
            setManaged(v);
        });
    }

    public synchronized void loadImg() {
        if (imgLoaded) {
            return;
        }
        imgLoaded = true;
        //System.out.println("load");

        //try (FileInputStream fi = new FileInputStream(getPosterFile())) {
        Image image = new Image("file:" + getPosterFile().toString(), 160, 240, true, true);
        Platform.runLater(() -> img.setImage(image));
        //} catch (IOException ex) {
        //    System.err.println("failed to load image");
        //}
    }

    //private static int count = 0;
    public synchronized void unloadImg() {
        if (!imgLoaded) {
            return;
        }
        imgLoaded = false;
        //System.out.println("unload");
        //count++;
        Platform.runLater(() -> img.setImage(null));
        //System.gc();
        /*if (count % 12 == 0) {
         count = 0;
         Runtime.getRuntime().gc();
         }*/
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
            Properties p1 = ((MovieThumbnail) o1).prop.get(),
                    p2 = ((MovieThumbnail) o2).prop.get();
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
