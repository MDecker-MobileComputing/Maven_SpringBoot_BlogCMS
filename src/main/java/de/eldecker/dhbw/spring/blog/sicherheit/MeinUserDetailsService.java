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
import de.eldecker.dhbw.spring.blog.web.AdminThymeleafController;


/**
 * Implementierung Interface {@code UserDetailsService}, von der beim Anmeldevorgang
 * eines Autors ein Nutzerobjekt für einen bestimmten Nutzernamen abgefragt.
 */
@SuppressWarnings("unused")
@Service
public class MeinUserDetailsService implements UserDetailsService {

    /** 
     * Rolle für Autoren, erlaubt anlegen und ändern von Artikeln.<br>
     * <i>Spring Security</i> fügt noch ein "ROLE_" vorne an (darf man
     * nicht selber machen).  
     */
    public static final String ROLLE_AUTOR = "AUTOR";
    
    /** 
     * Rolle für Autoren, die auch Admin sind, also anderen Autoren anlegen 
     * können.<br>
     * <i>Spring Security</i> fügt noch ein "ROLE_" vorne an (darf man
     * nicht selber machen).    
     * <br><br>
     * 
     * Siehe Methode {@link AdminThymeleafController#istAdmin(Authentication)}
     * für Auswertung dieser Rolle.
     */
    public static final String ROLLE_ADMIN = "ADMIN";
    

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
     *                                   in der Datenbanktabelle {@code AUTOR}
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

            String[] rollenArray;
            if (autorEntity.isAdmin()) {

                rollenArray = new String[]{ ROLLE_AUTOR, ROLLE_ADMIN };

            } else {

                rollenArray = new String[]{ ROLLE_AUTOR };
            }
            
            final UserDetails userDetails = User.withUsername( nutzername )
                    .password( passwort ) // kein "{bcrypt}"-Prefix, weil wir eigene Bcrypt-Konfiguration verwenden
                    .roles( rollenArray )
                    .build();
            
            return userDetails;
        }
    }

}
