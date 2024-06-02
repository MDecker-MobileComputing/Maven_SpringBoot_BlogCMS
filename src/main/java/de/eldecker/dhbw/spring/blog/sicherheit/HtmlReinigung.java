package de.eldecker.dhbw.spring.blog.sicherheit;

import static org.jsoup.safety.Safelist.relaxed;


import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;



/**
 * Bean-Klasse um die über REST-Endpunkt empfangenen HTML-Inhalte zu bereinigen,
 * vor allem um evtl. eingebauten JavaScript-Code zu entfernen.
 */
@Component
public class HtmlReinigung {

    /** 
     * Eigene Safelist basiered auf der Default-Safelist "relaxed", die zusätzlich
     * CSS-Styling erlaubt (damit farbige Texte möglich sind).
     */
    private Safelist _safelist =  relaxed().addAttributes( ":all", "style" );

    
    /**
     * Bereinigt den übergebenen HTML-String von potentiell gefährlichen
     * JavaScript-Inhalten. Es wird die nicht ganz so strenge
     * Whitelist "relaxed" verwendet.
     *
     * @param html HTML-String, der bereinigt werden soll
     *
     * @return bereinigter HTML-String
     */
    public String sanitize( String html ) {

        return Jsoup.clean( html, _safelist );
    }

}
