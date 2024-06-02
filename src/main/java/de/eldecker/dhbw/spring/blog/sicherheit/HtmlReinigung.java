package de.eldecker.dhbw.spring.blog.sicherheit;

import org.jsoup.Jsoup;

import org.springframework.stereotype.Component;
import static org.jsoup.safety.Safelist.relaxed;


/**
 * Bean-Klasse um die über REST-Endpunkt empfangenen HTML-Inhalte zu bereinigen,
 * vor allem um evtl. eingebauten JavaScript-Code zu entfernen.
 */
@Component
public class HtmlReinigung {

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

        return Jsoup.clean( html, relaxed() );
    }

}
