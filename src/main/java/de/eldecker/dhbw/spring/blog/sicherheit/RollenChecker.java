package de.eldecker.dhbw.spring.blog.sicherheit;

import static de.eldecker.dhbw.spring.blog.sicherheit.MeinUserDetailsService.ROLLE_ADMIN;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Diese Bean enthält Methoden, mit denen in verschiedenen Controller-Klassen
 * überprüft werden kann, ob der aktuelle Nutzer angemeldet ist und die
 * erforderlichen Rollen hat.
 */
@Service
public class RollenChecker {

    /**
     * Name der Admin-Rolle mit Prefix "ROLE_" (wird von <i>Spring Security</i> hinzugefügt,
     * können wir nicht selbst hinzufügen.
     */
    public static final String ROLLE_ADMIN_MIT_PREFIX = "ROLE_" + ROLLE_ADMIN;


    /**
     * Hilfsmethode zum Überprüfen, ob der Nutzer, der den Request gemacht
     * hat, angemeldet ist und auch die Admin-Rolle hat.
     *
     * @param auth {@code Authentication}-Objekt, das der Controller-Methode
     *             als Argument übergeben wurde
     *
     * @return {@code true} gdw. der aktuelle Nutzer angemeldet ist und
     *         die Admin-Rolle hat (nur ein angemeldeter Nutzer kann die
     *         Admin-Rolle haben)
     */
    public boolean istAdmin( Authentication authentication ) {

        if ( authentication == null || !authentication.isAuthenticated() ) {

            return false;
        }

         // Spring Security setzt Prefix "ROLE_" vorne an Rollennamen

        final Collection<? extends GrantedAuthority> authoritiesCollection =
                                                                   authentication.getAuthorities();
        for ( GrantedAuthority ga: authoritiesCollection ) {

            final String authorityString =  ga.getAuthority();
            if ( authorityString.equals( ROLLE_ADMIN_MIT_PREFIX ) ) {

                return true;
            }
        }

        return false;
    }

}
