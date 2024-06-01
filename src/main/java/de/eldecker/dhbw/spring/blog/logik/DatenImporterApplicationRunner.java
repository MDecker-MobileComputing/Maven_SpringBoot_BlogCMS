package de.eldecker.dhbw.spring.blog.logik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.blog.db.ArtikelEntity;
import de.eldecker.dhbw.spring.blog.db.ArtikelRepo;

/**
 * Bean um bei Bedarf Demo-Content unmittelbar nach Start der Anwendung zu importieren.
 */
@Service
public class DatenImporterApplicationRunner implements ApplicationRunner {

    private final static Logger LOG = LoggerFactory.getLogger( DatenImporterApplicationRunner.class );

    /** Repo für Zugriff auf Tabelle mit Blog-Artikeln. */
    private final ArtikelRepo _artikelRepo;
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public DatenImporterApplicationRunner( ArtikelRepo artikelRepo ) {
        
        _artikelRepo = artikelRepo;
    }
    
    
    /**
     * Diese Methode wird ausgeführt, sobald die Anwendung initialisiert wurde.
     * 
     * @param args Wird nicht ausgewertet
     */
    @Override
    public void run( ApplicationArguments args ) throws Exception {

        final long anzahlAlt = _artikelRepo.count();
        if ( anzahlAlt > 0 ) {
            
            LOG.info( "Es sind schon {} Artikel in der Datenbank, deshalb wird kein Demo-Content importiert." );
            
        } else {
            
            LOG.info( "Keine Artikel in Datenbank, importiere Demo-Content." );
            
            final ArtikelEntity artikel1 = 
                    new ArtikelEntity( "Test-Beitrag (automatisch erzeugt)", 
                                       "{\"ops\":[{\"attributes\":{\"bold\":true},\"insert\":\"Testbeitrag (Demo-Content)\"},{\"insert\":\"\\n\"}]}", 
                                       "<p>Testbeitrag (Demo-Content)</p>");                                         
            
            _artikelRepo.save( artikel1 );            
            
            final long anzahlNeu = _artikelRepo.count();
            LOG.info( "Artikel in DB nach Import von Demo-Content: {}", anzahlNeu );
        }
    }
    
}
