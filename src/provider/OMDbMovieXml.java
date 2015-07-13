/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provider;

import java.util.Properties;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author kristof
 */
public class OMDbMovieXml {

    @XmlAttribute
    private String title;
    @XmlAttribute
    private String year;
    @XmlAttribute
    private String rated;
    @XmlAttribute
    private String released;
    @XmlAttribute
    private String runtime;
    @XmlAttribute
    private String genre;
    @XmlAttribute
    private String director;
    @XmlAttribute
    private String writer;
    @XmlAttribute
    private String actors;
    @XmlAttribute
    private String plot;
    @XmlAttribute
    private String poster;
    @XmlAttribute
    private String imdbID;

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getRated() {
        return rated;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public String getWriter() {
        return writer;
    }

    public String getActors() {
        return actors;
    }

    public String getPlot() {
        return plot;
    }

    public String getPoster() {
        return poster;
    }

    public String getImdbID() {
        return imdbID;
    }

    public Properties createProperties() {
        Properties prop = new Properties();
        prop.setProperty("title", title);
        prop.setProperty("year", year);
        prop.setProperty("rated", rated);
        prop.setProperty("released", released);
        prop.setProperty("runtime", runtime);
        prop.setProperty("genre", genre);
        prop.setProperty("director", director);
        prop.setProperty("writer", writer);
        prop.setProperty("actors", actors);
        prop.setProperty("plot", plot);
        prop.setProperty("poster", poster);
        prop.setProperty("imdb", imdbID);
        return prop;
    }

}
