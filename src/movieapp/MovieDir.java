/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 *
 * @author kristof
 */
public class MovieDir {

    private final File dir, moviefile, propfile, poster;
    private Properties properties;
    private final String vlc;

    MovieDir(String vlc, File dir, File moviefile) {
        this.vlc = vlc;
        this.dir = dir;
        this.moviefile = moviefile;
        propfile = new File(dir, "movieproperties.properties");
        poster = new File(dir, "poster.jpg");
        if (propfile.exists()) {
            this.properties = new Properties();
            try {
                this.properties.load(new FileInputStream(propfile));
            } catch (IOException ex) {
                System.err.println("Failed to load properties");
            }
        }
    }

    public File getDir() {
        return dir;
    }

    public File getMoviefile() {
        return moviefile;
    }

    public String getMoviefileName() {
        String[] split = moviefile.toString().split("\\\\");
        return split[split.length - 1];
    }

    public Properties getProperties() {
        return properties;
    }

    public File getPropfile() {
        return propfile;
    }

    public File getPoster() {
        return poster;
    }

    public boolean setProperties(Properties properties) {
        if (properties != null) {
            this.properties = properties;
            try (FileOutputStream fo = new FileOutputStream(propfile)) {
                this.properties.store(fo, null);
                BufferedImage img = ImageIO.read(new URL(properties.getProperty("poster")));
                ImageIO.write(img, "jpg", poster);
            } catch (IOException ex) {
                System.err.println("Failed to write movieproperties.properties file");
            }
            return true;
        }
        return false;
    }

    public void playMovie() {
        try {
            Runtime.getRuntime().exec(String.format("\"%s\" \"%s\"", vlc, moviefile));
        } catch (IOException ex) {
            System.err.println("Failed to play file: " + moviefile);
        }
    }

}
