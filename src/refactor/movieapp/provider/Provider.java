/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.provider;

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

    abstract public Properties getProperties(String imdb);

}
