package de.eldecker.dhbw.spring.blog.model;


/**
 * Applikations-spezifische Exception-Klasse.
 */
@SuppressWarnings("serial")
public class BlogException extends Exception {

    /**
     * Konstruktor um Exception mit Fehlertext zu erzeugen.
     * 
     * @param fehlertext Beschreibung von Fehler
     */
    public BlogException( String fehlertext ) {
        
        super( fehlertext );
    }
    
}
