package de.eldecker.dhbw.spring.blog.logik;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.blog.db.ArtikelEntity;
import de.eldecker.dhbw.spring.blog.db.ArtikelRepo;
import de.eldecker.dhbw.spring.blog.db.AutorEntity;
import de.eldecker.dhbw.spring.blog.db.AutorenRepo;

/**
 * Bean um bei Bedarf Demo-Content unmittelbar nach Start der Anwendung zu importieren.
 */
@Service
public class DatenImporterApplicationRunner implements ApplicationRunner {

    private final static Logger LOG = LoggerFactory.getLogger( DatenImporterApplicationRunner.class );

    /**
     * Objekt um Passwörter mit Bcrypt zu verhashen.
     * <br><br>
     *
     * Alternative zur Erzeugung von Bcrypt-Hashes für Demo-User:
     * Online-Dienste wie <a href="https://bcrypt.online/" target="_blank">bcrypt.online</a>.
     */
    private final static BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

    /** Repo für Zugriff auf Tabelle mit Blog-Artikeln. */
    private final ArtikelRepo _artikelRepo;

    /** Repo für Zugriff auf Tabelle mit Autoren. */
    private final AutorenRepo _autorRepo;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public DatenImporterApplicationRunner( ArtikelRepo artikelRepo,
                                           AutorenRepo autorRepo   ) {
        _artikelRepo = artikelRepo;
        _autorRepo   = autorRepo;
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

            LOG.info( "Es sind schon {} Artikel in der Datenbank, deshalb wird kein Demo-Content importiert.",
                      anzahlAlt );
        } else {

            final String passwort1 = BCRYPT_ENCODER.encode( "g3h3im" );
            final String passwort2 = BCRYPT_ENCODER.encode( "s3cr3t" );

            final AutorEntity autor1 = new AutorEntity( "alice", passwort1 );
            final AutorEntity autor2 = new AutorEntity( "bob"  , passwort2 );

            final List<AutorEntity> autorenListe = List.of( autor1, autor2 );
            _autorRepo.saveAll( autorenListe );


            LOG.info( "Keine Artikel in Datenbank, importiere Demo-Content." );

            final ArtikelEntity artikel1 =
                    new ArtikelEntity( "Test-Beitrag 1 (automatisch erzeugt)",
                                       "{\"ops\":[{\"attributes\":{\"bold\":true},\"insert\":\"Testbeitrag (Demo-Content)\"},{\"insert\":\"\\n\"}]}",
                                       "<p>Testbeitrag (Demo-Content)</p>",
                                       "Testbeitrag (Demo-Content)",
                                       autor1
                                     );

            final ArtikelEntity artikel2 =
                    new ArtikelEntity( "Test-Beitrag 2 (automatisch erzeugt)",
                                       "{\"ops\":[{\"attributes\":{\"bold\":true},\"insert\":\"Noch ein Testbeitrag (Demo-Content)\"},{\"insert\":\"\\n\"}]}",
                                       "<p>Noch ein Testbeitrag (Demo-Content)</p>",
                                       "Noch ein Testbeitrag (Demo-Content)",
                                       autor1
                                     );
            final List<ArtikelEntity> artikelListe = List.of( artikel1, artikel2 );
            _artikelRepo.saveAll( artikelListe );

            final long anzahlNeu = _artikelRepo.count();
            LOG.info( "Artikel in DB nach Import von Demo-Content: {}", anzahlNeu );
        }
    }

}
