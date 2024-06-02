package de.eldecker.dhbw.spring.blog.sicherheit;

import static de.eldecker.dhbw.spring.blog.sicherheit.MeinUserDetailsService.ROLLE_ADMIN;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger( RollenChecker.class );


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

            LOG.warn( "Nicht authentifizierter Nutzer hat versucht eine Admin-Seite aufzurufen." );
            return false;
        }

        final String rolleGesucht = "ROLE_" + ROLLE_ADMIN;

        final Collection<? extends GrantedAuthority> authoritiesCollection =
                authentication.getAuthorities();

        for ( GrantedAuthority ga: authoritiesCollection ) {

            final String authorityString =  ga.getAuthority();
            if ( authorityString.equals( rolleGesucht ) ) {

                return true;
            }
        }

        LOG.warn( "Autor \"{}\" hat versucht ohne Admin-Rolle eine Admin-Seite aufzurufen.",
                  authentication.getName() );

        return false;
    }

}
