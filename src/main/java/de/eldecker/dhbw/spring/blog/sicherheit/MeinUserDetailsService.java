package de.eldecker.dhbw.spring.blog.sicherheit;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.blog.db.AutorEntity;
import de.eldecker.dhbw.spring.blog.db.AutorenRepo;


/**
 * Implementierung Interface {@code UserDetailsService}, von der beim Anmeldevorgang
 * eines Autors ein Nutzerobjekt für einen bestimmten Nutzernamen abgefragt.
 */
@Service
public class MeinUserDetailsService implements UserDetailsService {

    public static final String ROLLE_AUTOR = "autor";

    /** Repo-Bean für Zugriff auf Datenbanktabelle mit Autoren. */
    private AutorenRepo _autorRepo;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public MeinUserDetailsService( AutorenRepo autorRepo ) {

        _autorRepo = autorRepo;
    }


    /**
     * Diese Methode wird aufgerufen, wenn ein Nutzer {@code nutzername} und ein Passwort
     * im Anmeldeformular eingegeben hat. Es wird dann in der Datenbank nachgeschaut, ob es
     * einen aktiven Nutzer mit {@code nutzername} gibt.
     *
     * @param nutzername Im Anmeldeformular eingegebener Nutzername des Autors
     *
     * @return Nutzer-Objekt für {@code nutzername}, enthält u.a. Passwort im Bcrypt-Format
     *
     * @throws UsernameNotFoundException Es gibt keinen Nutzer mit {@code nutzername}
     */
    @Override
    public UserDetails loadUserByUsername( String nutzername ) throws UsernameNotFoundException {

        final Optional<AutorEntity> autorOptional = _autorRepo.findByName( nutzername );

        if ( autorOptional.isEmpty() ) {

            throw new UsernameNotFoundException(
                            "Kein Autor mit Name \"" + nutzername + "\" gefunden" );
        } else {

            final AutorEntity autorEntity = autorOptional.get();

            final String passwort = autorEntity.getPasswort();

            final UserDetails userDetails = User.withUsername( nutzername )
                                                .password( passwort ) // kein "{bcrypt}"-Prefix, weil wir eigene Bcrypt-Konfiguration verwenden
                                                .roles( ROLLE_AUTOR )
                                                .build();
            return userDetails;
        }
    }

}
