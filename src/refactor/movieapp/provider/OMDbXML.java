/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.provider;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristof
 */
@XmlRootElement(name = "root")
public class OMDbXML {

    @XmlAttribute
    private String response;

    @XmlElement
    private OMDbMovieXml movie;

    public boolean hasResponse() {
        return Boolean.parseBoolean(response.toLowerCase());
    }

    public OMDbMovieXml getMovie() {
        return movie;
    }
}
