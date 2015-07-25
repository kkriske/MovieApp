/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.util;

import java.util.Stack;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author kristof
 */
public final class SceneManager {

    static private Scene s;
    final static private Stack<Parent> r = new Stack<>();

    private SceneManager() {
        throw new UnsupportedOperationException("SceneManager is a static utility class, it is not meant to be instantiated");
    }

    static final public void setScene(Scene scene) {
        s = scene;
    }

    static final public void nextRoot(Parent root) {
        if (s != null) {
            r.push(s.getRoot());
            s.setRoot(root);
        }
    }

    static final public void previousRoot() {
        if (!r.isEmpty() && s != null) {
            s.setRoot(r.pop());
        }
    }
}
