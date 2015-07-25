/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 *
 * @author kristof
 */
public final class Settings {

    /**
     * directory where settings are stored
     */
    static final public String PROPERTIES_DIRECTORY = System.getProperty("user.home") + File.separator + ".movieApp";
    /**
     * name of property file
     */
    static final public String PROPERTIES_FILENAME = "movieAppProperties.properties";
    /**
     * full path to property file
     */
    static final public String PROPERTIES_PATH = PROPERTIES_DIRECTORY + File.separator + PROPERTIES_FILENAME;
    static final private String EXTENSIONS = "extensions";
    static final private String DIRECTORIES = "movie-directories";
    static final private String DEFAULT_EXTENSIONS = "[mpeg,mpg,mpe,m1s,mpa,mp2,m2a,mp2v,m2v,m2s,avi,mov,qt,asf,asx,wmv,wma,wmx,rm,ra,ram,rmvb,mp4,3gp,mkv,ogm]";
    static final private Properties properties = new Properties();
    static final private ObservableSet<String> extensions = FXCollections.observableSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
    static final private ObservableSet<String> directories = FXCollections.observableSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

    static {
        try {
            File dir = new File(PROPERTIES_DIRECTORY);
            File file = new File(dir, PROPERTIES_FILENAME);
            if (!file.isFile()) {
                if (!dir.isDirectory()) {
                    dir.mkdir();
                }
                file.createNewFile();
                resetDirectories();
                resetExtensions();
            } else {
                properties.load(new FileInputStream(PROPERTIES_PATH));
                try {
                    String prop = properties.getProperty(EXTENSIONS).replaceAll("^\\[|\\]$", "");
                    if (!prop.matches("[ ]*")) {
                        extensions.addAll(Arrays.asList(prop.split(",[ ]*")));
                    }
                } catch (NullPointerException ex) {
                    resetExtensions();
                }
                try {
                    String prop = properties.getProperty(DIRECTORIES).replaceAll("^\\[|\\]$", "");
                    if (!prop.matches("[ ]*")) {
                        directories.addAll(Arrays.asList(prop.split(",[ ]*")));
                    }
                } catch (NullPointerException ex) {
                    resetDirectories();
                }
            }
        } catch (IOException ex) {
            System.err.println("Failed to create settings");
        }
    }

    private Settings() {
        throw new UnsupportedOperationException("Settings is a static utility class, it is not meant to be instantiated");
    }

    static final public ObservableSet<String> getExtensions() {
        return extensions;
    }

    static final public ObservableSet<String> getDirectories() {
        return directories;
    }

    static final public boolean removeExtensions(Set<String> ext) {
        boolean success = extensions.removeAll(ext);
        if (success) {
            properties.setProperty(EXTENSIONS, extensions.toString());
            saveProperties();
        }
        return success;
    }

    static final public boolean removeExtension(String ext) {
        boolean success = extensions.remove(ext);
        if (success) {
            properties.setProperty(EXTENSIONS, extensions.toString());
            saveProperties();
        }
        return success;
    }

    static final public boolean addExtensions(Set<String> ext) {
        boolean success = extensions.addAll(ext);
        if (success) {
            properties.setProperty(EXTENSIONS, extensions.toString());
            saveProperties();
        }
        return success;
    }

    static final public boolean addExtension(String ext) {
        boolean success = extensions.add(ext);
        if (success) {
            properties.setProperty(EXTENSIONS, extensions.toString());
            saveProperties();
        }
        return success;
    }

    static final public void resetExtensions() {
        properties.setProperty(EXTENSIONS, DEFAULT_EXTENSIONS);
        extensions.clear();
        extensions.addAll(Arrays.asList(properties.getProperty(EXTENSIONS).replaceAll("^\\[|\\]$", "").split(",[ ]*")));
        saveProperties();
    }

    static final public boolean removeDirectories(Set<String> dir) {
        boolean success = directories.removeAll(dir);
        if (success) {
            properties.setProperty(DIRECTORIES, directories.toString());
            saveProperties();
        }
        return success;
    }

    static final public boolean removeDirectory(String dir) {
        boolean success = directories.remove(dir);
        if (success) {
            properties.setProperty(DIRECTORIES, directories.toString());
            saveProperties();
        }
        return success;
    }

    static final public boolean addDirectories(Set<String> dir) {
        boolean success = directories.addAll(dir);
        if (success) {
            properties.setProperty(DIRECTORIES, directories.toString());
            saveProperties();
        }
        return success;
    }

    static final public boolean addDirectory(String dir) {
        boolean success = directories.add(dir);
        if (success) {
            properties.setProperty(DIRECTORIES, directories.toString());
            saveProperties();
        }
        return success;
    }

    static final public void resetDirectories() {
        properties.setProperty(DIRECTORIES, "");
        directories.clear();
        saveProperties();
    }

    private static void saveProperties() {
        try {
            properties.store(new FileOutputStream(PROPERTIES_PATH), null);
        } catch (IOException ex) {
            System.err.println("failed to update settings");
        }
    }

}
