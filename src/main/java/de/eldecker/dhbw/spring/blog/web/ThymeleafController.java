package de.eldecker.dhbw.spring.blog.web;

import static java.lang.String.format;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import de.eldecker.dhbw.spring.blog.db.ArtikelEntity;
import de.eldecker.dhbw.spring.blog.db.ArtikelRepo;
import de.eldecker.dhbw.spring.blog.db.AutorEntity;
import de.eldecker.dhbw.spring.blog.model.BlogException;


/**
 * Die Mapping-Methoden in dieser Klassen geben den Namen des Thymeleaf-Templates zurück,
 * das angezeigt werden soll.
 */
@Controller
@RequestMapping( "/app" )
public class ThymeleafController {

    private static final Logger LOG = LoggerFactory.getLogger( ThymeleafController.class );


    /** Repo-Bean für Zugriff auf Tabelle mit Artikeln. */
    @Autowired
    private ArtikelRepo _artikelRepo;


    /**
     * Event-Handler für Exceptions der Klasse {@link BlogException}
     *
     * @param ex Exception, die von einer Controller-Methode geworfen wurde
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @return Name der Template-Datei "fehler.html" ohne Datei-Endung
     */
    @ExceptionHandler( BlogException.class )
    public String onBlogException( BlogException ex, Model model ) {

        LOG.error( ex.getMessage() );

        model.addAttribute( "fehlertext", ex.getMessage() );

        return "fehler";
    }


    /**
     * Event-Handler für Exception, wenn in int/long-Pfadparameter übergeben wird,
     * der aber keinen gültigen Zahlenwert darstellt (also nicht geparst werden kann).
     *
     * @param ex Exception-Objekt
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @return Name der Template-Datei "fehler.html" ohne Datei-Endung
     */
    @ExceptionHandler( MethodArgumentTypeMismatchException.class )
    public String onArgTypeMismatchException( MethodArgumentTypeMismatchException ex, Model model ) {

        final String fehlerText = format( "Ungültiger Wert für Parameter \"%s\" übergeben: \"%s\"",
                                          ex.getName(), ex.getValue() );

        LOG.error( fehlerText );

        model.addAttribute( "fehlertext", fehlerText );

        return "fehler";
    }


    /**
     * Einzelnen Artikel anzeigen.
     *
     * @param authentication Objekt für Abfrage authentifizierter Nutzer
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @param artikelID ID des Artikels, der angezeigt werden soll
     *
     * @return Name der Template-Datei "artikel-anzeige.html" ohne Datei-Endung
     *
     * @throws BlogException Artikel mit {@code artikelID} wurde nicht gefunden
     */
    @GetMapping( "/artikel/{artikelID}" )
    public String artikelAnzeigen( Authentication authentication,
                                   Model model,
                                   @PathVariable("artikelID") long artikelID )
        throws BlogException {

        final Optional<ArtikelEntity> artikelOptional = _artikelRepo.findById( artikelID );
        if ( artikelOptional.isEmpty() ) {

            throw new BlogException( "Kein Artikel mit ID=" + artikelID + " gefunden." );
        }

        final ArtikelEntity artikelEntity = artikelOptional.get();

        model.addAttribute( "artikel", artikelEntity );

        if ( authentication != null && authentication.isAuthenticated() ) {

            model.addAttribute( "angemeldetAls", authentication.getName() );
        }

        return "artikel-anzeige";
    }


    /**
     * Seite mit Liste der Blog-Artikel anzeigen.
     *
     * @param authentication Objekt für Abfrage authentifizierter Nutzer
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @return Name der Template-Datei "artikel-liste.html" ohne Datei-Endung
     */
    @GetMapping( "/artikel/liste" )
    public String artikelListe( Authentication authentication, Model model ) {

        final List<ArtikelEntity> artikelListe = _artikelRepo.findAllByOrderByZeitpunktAngelegtDesc();

        model.addAttribute( "artikel_liste", artikelListe );

        if ( authentication != null && authentication.isAuthenticated() ) {

            model.addAttribute( "angemeldetAls", authentication.getName() );
        }

        return "artikel-liste";
    }


    /**
     * Controller für Textsuche.
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @param suchbegriff URL-Parameter mit Suchbegriff
     *
     * @return Name der Template-Datei "artikel-suche-ergebnis.html" ohne Datei-Endung,
     *         oder "fehler.html" bei leerem Suchbegriff
     */
    @GetMapping( "/artikel/suche" )
    public String artikelSuche( Model model,
                                @RequestParam(value = "suchbegriff", required = true) String suchbegriff ) {

        suchbegriff = suchbegriff.trim();

        if ( suchbegriff.isBlank() ) {

            model.addAttribute( "fehlertext", "Leerer Suchbegriff" );
            return "fehler";
        }

        final List<ArtikelEntity> artikelListe = _artikelRepo.holeArtikelTextsuche( suchbegriff );

        model.addAttribute( "suchbegriff"  , suchbegriff  );
        model.addAttribute( "artikel_liste", artikelListe );

        return "artikel-suche-ergebnis";
    }

}
