package de.eldecker.dhbw.spring.blog.sicherheit;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Konfiguration von Web-Security: auf bestimmte Pfad soll man nur nach Authentifizierung
 * zugreifen dürfen.
 */
@Configuration
@EnableWebSecurity
public class Sicherheitskonfiguration {

    /** Array mit Pfaden, auf die auch ohne Authentifizierung zugegriffen werden kann. */
    private final static String[] OEFFENTLICHE_PFADE_ARRAY = { "/index.html"     ,
                                                               "/styles.css"     ,
                                                               "/h2-console/**"  ,
                                                               "/app/**"
                                                             };
    
    /**
     * Konfiguration Sicherheit für HTTP (formularbasierte Authentifizierung).
     * 
     * @param http Objekt als Ausgangspunkt für Sicherheitskonfiguration
     * 
     * @return Sicherheitskonfiguration
     * 
     * @throws Exception Fehler bei Sicherheitskonfiguration aufgetreten
     */    
    @Bean
    public SecurityFilterChain httpKonfiguration( HttpSecurity http ) throws Exception {

        final AntPathRequestMatcher[] oeffentlichPfadMatcherArray = getMatcherFuerOeffentlichePfade();
        
        return http.csrf( (csrf) -> csrf.disable() )
                   .authorizeHttpRequests( auth -> auth.requestMatchers( oeffentlichPfadMatcherArray ).permitAll()
                                                       .anyRequest().authenticated() )
                   .formLogin( formLogin -> formLogin.defaultSuccessUrl( "/app/artikel/liste", true ) ) // true=alwaysUse 
                   .logout(logout -> logout
                           .logoutUrl( "/abmelden" )
                           .invalidateHttpSession( true )
                           .deleteCookies( "JSESSIONID" )
                          )
                   .headers( headers -> headers.disable() ) // damit H2-Konsole funktioniert
                   .build();                   
    }
    
    
    /**
     * Erzeugt für öffentliche Pfade aus String-Array in einen Array von 
     * {@code AntPathRequestMatcher}-Objekten.
     *
     * @return Array mit Matcher-Objekten für die öffentliche Pfade 
     *         (Pfade, die ohne Authentifizierung aufgerufen werden können)
     */
    private static AntPathRequestMatcher[] getMatcherFuerOeffentlichePfade() {

        final int anzahlOeffentlichePfade = OEFFENTLICHE_PFADE_ARRAY.length;
        final AntPathRequestMatcher[] ergebnisArray = new AntPathRequestMatcher[ anzahlOeffentlichePfade ];
        for ( int i = 0; i < anzahlOeffentlichePfade; i++ ) {

            ergebnisArray[ i ] = antMatcher( OEFFENTLICHE_PFADE_ARRAY[i] );
        }

        return ergebnisArray;
    }    
}
