package de.eldecker.dhbw.spring.blog.web;

import static java.lang.String.format;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.eldecker.dhbw.spring.blog.db.AutorEntity;
import de.eldecker.dhbw.spring.blog.db.AutorenRepo;
import de.eldecker.dhbw.spring.blog.sicherheit.RollenChecker;
import de.eldecker.dhbw.spring.blog.sicherheit.Sicherheitskonfiguration;


/**
 * Spezieller Controller für die Seiten für Admin-Zugriff (Anlegen neuer Nutzer).
 * Diese Pfade dieses Controllers fangen alle mit {@code /admin} an und sind
 * nicht für anoynme Nutzer freigegben, siehe auch Klasse
 * {@link Sicherheitskonfiguration}.
 */
@Controller
@RequestMapping( "/admin" )
public class AdminThymeleafController {

    private static final Logger LOG = LoggerFactory.getLogger( AdminThymeleafController.class );

    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Autoren (Nutzer). */
    private final AutorenRepo _autorenRepo;

    /** Bean für Hashing von Passwort. */
    private final BCryptPasswordEncoder _bcryptEncoder;

    /** Bean zum Überprüfen, ob Nutzer bestimmte Rollen hat. */
    private final RollenChecker _rollenChecker;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    public AdminThymeleafController( AutorenRepo autorenRepo,
                                     BCryptPasswordEncoder bcryptEncoder,
                                     RollenChecker rollenChecker ) {

        _autorenRepo   = autorenRepo;
        _bcryptEncoder = bcryptEncoder;
        _rollenChecker = rollenChecker;
    }


    /**
     * Zeigt die Seite für das Anlegen eines neuen Autors durch einen Admin
     * an. Es wird überprüft, ob der Nutzer die Admin-Rolle hat (eigentlich
     * würde es reichen, diese Überprüfung beim Absenden des Formulars mit
     * dem Namen und Passwort des neuen Nutzers zu machen).
     *
     * @param authentication Objekt für Abfrage authentifizierter Nutzer und
     *                       dessen Rollen
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @return Name der Template-Datei "autor-neu-formular.html" wenn der aufrufende
     *         Nutzer die Admin-Rolle hat, sonst "fehler.html"; in beiden Fällen wird
     *         der Dateiname ohne die Datei-Endung zurückgegeben.
     */
    @GetMapping( "/autorAnlegenFormular" )
    public String autorAnlegenFormular( Authentication authentication,
                                        Model model ) {

        if ( _rollenChecker.istAdmin( authentication ) == false ) {

            LOG.warn( "Non-Admin-Nutzer hat versucht, Seite zum Anlegen neuer Autoren aufzurufen." );

            model.addAttribute( "fehlertext",
                                "Nur Admins dürfen Seite zum Anlegen neuer Autoren aufrufen." );
            return "fehler";
        }

        return "autor-anlegen-formular";
    }


    /**
     * Controller-Methode für eigentliches Anlegen neuer Autor durch einen Admin.
     *
     * @param authentication Objekt für Abfrage authentifizierter Nutzer und
     *                       dessen Rollen
     *
     * @param model Objekt für Platzhalterwerte in Template
     *
     * @param anmeldename Anmeldenamen für den neuen Autor
     *
     * @param passwort1 Passwort für neuen Autor
     *
     * @param passwort2 Wiederholung Passwort für neuen Autor,
     *                  muss mit {@code passwort1} übereinstimmen.
     *
     * @return Name von Template-Datei "autor-anlegen-ergebnis.html" mit
     *         dem Ergebnis (hat Anlegen geklappt oder nicht, z.B. weil
     *         zwei unterschiedliche Passwörter eingegeben wurden); wenn
     *         der Nutzer keine Admin-Rolle hat, dann "fehler.html"; in
     *         beiden Fällen wird der Dateiname ohne die Datei-Endung
     *         zurückgegeben.
     */
    @PostMapping( "/autorAnlegen" )
    public String autorAnlegen( Authentication authentication,
                                Model model,
                                @RequestParam(value = "anmeldename", required = true) String anmeldename,
                                @RequestParam(value = "passwort1"  , required = true) String passwort1  ,
                                @RequestParam(value = "passwort2"  , required = true) String passwort2  ) {

        if ( _rollenChecker.istAdmin( authentication ) == false ) {

            LOG.warn( "Non-Admin-Nutzer hat versucht, neuen Autor anzulegen." );

            model.addAttribute( "fehlertext",
                                "Nur Admins dürfen neue Autoren anlegen." );
            return "fehler";
        }

        anmeldename = anmeldename.trim();
        if ( anmeldename.length() < 3 ) {

            model.addAttribute( "ergebnis_text", "Anmeldename hat weniger als 3 Zeichen." );
            return "autor-anlegen-ergebnis";
        }

        final Optional<AutorEntity> autorOptional = _autorenRepo.findByName( anmeldename );
        if ( autorOptional.isPresent() ) {

            model.addAttribute( "ergebnis_text", "Es gibt schon einen Nutzer diesem Namen." );
            return "autor-anlegen-ergebnis";
        }

        passwort1 = passwort1.trim();
        passwort2 = passwort2.trim();

        if ( passwort1.length() < 6 ) {

            model.addAttribute( "ergebnis_text", "Passwort hat weniger als 6 Zeichen." );
            return "autor-anlegen-ergebnis";
        }

        if ( passwort1.length() != passwort2.length() ) {

            model.addAttribute( "ergebnis_text", "Die beiden Passwörter sind unterschiedlich lang." );
            return "autor-anlegen-ergebnis";
        }

        if ( ! passwort1.equals( passwort2) ) {

            model.addAttribute( "ergebnis_text", "Die beiden Passwörter sind nicht identisch." );
            return "autor-anlegen-ergebnis";
        }


        final String passwortHash = _bcryptEncoder.encode( passwort1 );

        AutorEntity autorEntityNeu = new AutorEntity( anmeldename, passwortHash, false );

        autorEntityNeu = _autorenRepo.save( autorEntityNeu );

        final String erfolgsText = format( "Autor \"%s\" erfolgreich angelegt mit ID=%d.",
                                           anmeldename, autorEntityNeu.getId() );
        LOG.info( erfolgsText );

        model.addAttribute( "ergebnis_text", erfolgsText );

        return "autor-anlegen-ergebnis";
    }

}
