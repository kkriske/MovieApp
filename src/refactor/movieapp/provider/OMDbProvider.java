/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.provider;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author kristof
 */
public class OMDbProvider extends Provider {

    private Unmarshaller um;

    public OMDbProvider() {
        try {
            um = JAXBContext.newInstance(OMDbXML.class).createUnmarshaller();
        } catch (JAXBException ex) {
            System.err.println("Failed to create OMDbProvider");
        }
    }

    @Override
    public Properties getProperties(String imdb) {
        try {
            URLConnection con = new URL("http://www.omdbapi.com/?r=xml&i=" + imdb).openConnection();
            OMDbXML xml = (OMDbXML) um.unmarshal(con.getInputStream());
            return xml.hasResponse() ? xml.getMovie().createProperties() : null;
        } catch (IOException | JAXBException ex) {
            System.err.println("failed to get properties");
            return null;
        }
    }

}