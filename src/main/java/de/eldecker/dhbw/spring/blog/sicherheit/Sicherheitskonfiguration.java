package de.eldecker.dhbw.spring.blog.sicherheit;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



/**
 * Konfiguration von Web-Security: auf bestimmte Pfad soll man nur nach Authentifizierung
 * zugreifen dürfen. Artikel lesen dürfen anonyme Nutzer, aber nur angemeldete Nutzer (Autoren)
 * dürfen Artikel anlegen und ändern.
 */
@Configuration
@EnableWebSecurity
public class Sicherheitskonfiguration {

    /** Array mit Pfaden, auf die auch ohne Authentifizierung zugegriffen werden kann. */
    private final static String[] OEFFENTLICHE_PFADE_ARRAY = { "/index.html"   ,
                                                               "/styles.css"   ,
                                                               "/h2-console/**",
                                                               "/app/**"       ,
                                                               "/public/**"
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

        return http.csrf( (csrf) -> csrf.disable() )
                   .authorizeHttpRequests( auth -> auth.requestMatchers( OEFFENTLICHE_PFADE_ARRAY ).permitAll()
                                                       .anyRequest().authenticated() )
                   .formLogin( formLogin -> formLogin.defaultSuccessUrl( "/app/artikel/liste", true ) ) // true=alwaysUse
                   .logout(logout -> logout
                           .logoutUrl( "/abmelden" )
                           .logoutSuccessUrl("/public/autor-abgemeldet.html" )
                           .invalidateHttpSession( true )
                           .deleteCookies( "JSESSIONID" )
                          )
                   .headers( headers -> headers.disable() ) // damit H2-Konsole funktioniert
                   .build();
    } 
}
