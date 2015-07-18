/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.io.File;
import javafx.scene.control.Label;

/**
 *
 * @author kristof
 */
public class MovieThumbnail extends Label {

    public MovieThumbnail(File movie) {
        super(movie.getName());
    }

}
