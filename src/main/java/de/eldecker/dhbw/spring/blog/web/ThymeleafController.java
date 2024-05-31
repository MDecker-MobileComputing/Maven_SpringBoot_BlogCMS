package de.eldecker.dhbw.spring.blog.web;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.eldecker.dhbw.spring.blog.db.ArtikelEntity;
import de.eldecker.dhbw.spring.blog.db.ArtikelRepo;
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
    @ExceptionHandler(BlogException.class)
    public String exceptionBehandeln( BlogException ex, Model model ) {
        
        LOG.error( ex.getMessage() );
        
        model.addAttribute( "fehlertext", ex.getMessage() );
        
        return "fehler";
    }

    
    /**
     * Einzelnen Artikel anzeigen.
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
    public String artikelAnzeigen( Model model,
                                   @PathVariable("artikelID") long artikelID ) 
        throws BlogException {
        
        final Optional<ArtikelEntity> artikelOptional = _artikelRepo.findById( artikelID );
        if ( artikelOptional.isEmpty() ) {
            
            throw new BlogException( "Kein Artikel mit ID=" + artikelID + " gefunden." );
        }
        
        final ArtikelEntity artikelEntity = artikelOptional.get();
        
        model.addAttribute( "artikel", artikelEntity );
        
        return "artikel-anzeige";
    }
    
    
    /**
     * Seite mit Liste der Blog-Artikel anzeigen
     *  
     * @param model Objekt für Platzhalterwerte in Template
     * 
     * @return Name der Template-Datei "artikel-liste.html" ohne Datei-Endung
     */
    @GetMapping( "/artikel/liste" )
    public String artikelListe( Model model ) {
       
        final List<ArtikelEntity> artikelListe = _artikelRepo.findAllByOrderByZeitpunktAngelegtDesc();
        
        model.addAttribute( "artikel_liste", artikelListe );
        
        return "artikel-liste";
    }
    
}
