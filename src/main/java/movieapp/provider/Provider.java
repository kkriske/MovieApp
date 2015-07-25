/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.movieapp.provider;

import java.net.ConnectException;
import java.util.Properties;

/**
 *
 * @author kristof
 */
public abstract class Provider {

    private static Provider p;

    public static Provider getProvider() {
        return p;
    }

    public static void setProvider(Provider provider) {
        p = provider;
    }

    abstract public Properties getProperties(String imdb) throws ConnectException;

}
