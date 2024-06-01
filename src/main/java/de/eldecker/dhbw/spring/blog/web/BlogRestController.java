package de.eldecker.dhbw.spring.blog.web;

import static java.time.LocalDateTime.now;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.eldecker.dhbw.spring.blog.db.ArtikelEntity;
import de.eldecker.dhbw.spring.blog.db.ArtikelRepo;
import de.eldecker.dhbw.spring.blog.db.AutorEntity;
import de.eldecker.dhbw.spring.blog.db.AutorenRepo;
import de.eldecker.dhbw.spring.blog.model.ArtikelDTO;
import de.eldecker.dhbw.spring.blog.model.TitelUndDeltaInhaltDTO;


/**
 * Controller-Klasse, die REST-Endpunkte bereitstellt.
 */
@RestController
@RequestMapping( "/api/v1" )
public class BlogRestController {

    private static final Logger LOG = LoggerFactory.getLogger( BlogRestController.class );

    /** Bean für JSON-Serialisierung/Deserialisierung. */
    private final ObjectMapper _objectMapper;

    /** Repo-Bean für Zugriff auf Tabelle mit Artikeln. */
    private final ArtikelRepo _artikelRepo;
    
    /** Repo-Bean für Zugriff auf Tabelle mit Autoren. */
    private final AutorenRepo _autorenRepo; 


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public BlogRestController( ArtikelRepo artikelRepo,
                               AutorenRepo autorenRepo,
                               ObjectMapper objectMapper ) {

        _artikelRepo  = artikelRepo;
        _autorenRepo  = autorenRepo;
        _objectMapper = objectMapper;
    }

    
    /**
     * Artikel für Änderung für quilljs bereitstellen.
     *
     * @param artikelID ID des Artikels
     *
     * @param authentication Objekt, um Authentifzierung abzufragen
     *
     * @return Mögliche HTTP-Status-Codes:
     *         <ul>
     *         <li>200 (OK): Erfolg, Body enthält JSON mit Titel und Artikel im Delta-Format.</li>
     *         <li>401 (Unauthorized): Nutzer ist nicht angemeldet.</li>
     *         <li>404 (Not Found): Kein Artikel mit {@code artikelId} gefunden.</li>
     *         <li>500 (Internal Server Error): Internes Problem bei JSON-Erstellung.</li>
     *         </ul>
     *         Bei den Fehler-Coedes (4xx und 5xx) enthält der Body einen String (kein JSON) mit
     *         einer kurzen Fehlerbeschreibung.
     */
    @GetMapping( "/holen/{artikelID}" )
    public ResponseEntity<String> artikelHolen( @PathVariable("artikelID") long artikelID,
                                                Authentication authentication ) {                                                 
        
        if ( authentication == null || authentication.isAuthenticated() == false ) {
            
            final String fehlerText = "Unangemeldeter Nutzer kann Artikel nicht für Änderung abrufen.";
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, UNAUTHORIZED );            
        }                
        
        final Optional<ArtikelEntity> artikelOptional = _artikelRepo.findById( artikelID );
        if ( artikelOptional.isEmpty() ) {

            LOG.warn( "Artikel mit ungültigter ID={} von Frontend angefordert.", artikelID );
            return new ResponseEntity<>( "Kein Artikel mit ID=" + artikelID + " gefunden.", 
                                         NOT_FOUND );
        }

        final ArtikelEntity artikelEntity = artikelOptional.get();

        final TitelUndDeltaInhaltDTO dto =
                new TitelUndDeltaInhaltDTO( artikelEntity.getTitel(),
                                            artikelEntity.getInhaltDelta() );        
        try {
            
            final String json = _objectMapper.writeValueAsString( dto );
            
            return new ResponseEntity<>( json, OK);             
        }
        catch ( JsonProcessingException ex ) {
            
            LOG.error( "Fehler bei Serialisierung von DTO nach JSON: " + ex.getMessage() );
            return new ResponseEntity<>( "Interner Fehler bei Bereitstellung von Artikel im JSON-Format.", 
                                          INTERNAL_SERVER_ERROR );
        }
    }

    
    /**
     * REST-Endpunkt um neuen Artikel zu speichern.
     *
     * @param jsonPayload JSON-Payload vom Frontend mit neuem Artikel
     * 
     * @param authentication Objekt, um Authentifzierung abzufragen 
     *
     * @return Mögliche HTTP-Status-Codes:
     *         <ul>
     *         <li>201 (Created)     : Erfolg, Body enthält Pfad, an dem der neue Artikel zu finden ist.</li>
     *         <li>400 (Bad Request) : JSON-Payload konnte nicht deserialisert werden; Body enthält Fehlermeldung.</li>
     *         <li>401 (Unauthorized): Nutzer ist nicht angemeldet.</i>
     *         <li>403 (Forbidden)   : Nutzer ist angemeldet, aber wurde nicht in DB gefunden (kann eigentlich nicht sein).</li> 
     *         </ul>
     */
    @PostMapping( "/neu" )
    public ResponseEntity<String> artikelNeu( @RequestBody String jsonPayload,
                                              Authentication authentication ) {

        if ( authentication == null || authentication.isAuthenticated() == false ) {
            
            final String fehlerText = "Unangemeldeter Nutzer kann nicht neuen Artikel anlegen.";
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, UNAUTHORIZED );            
        }
        
        final String anmeldeName = authentication.getName(); 
        final Optional<AutorEntity> autorOptional = _autorenRepo.findByName( anmeldeName );
        if ( autorOptional.isEmpty() ) {
            
            final String fehlerText = "Nutzer angemeldet, aber nicht in DB gefunden.";
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, FORBIDDEN );            
        }
        final AutorEntity autorEntity = autorOptional.get();
        
        try {

            final ArtikelDTO artikel = _objectMapper.readValue( jsonPayload, ArtikelDTO.class );

            LOG.info( "Payload für Artikel mit Titel \"{}\" deserialisiert.", artikel.titel() );

            if ( artikel.titel().isBlank() ) {

                return new ResponseEntity<>( "Titel von Artikel ist leer", BAD_REQUEST );
            }
            
            ArtikelEntity artikelEntity = new ArtikelEntity( artikel.titel().trim(),
                                                             artikel.inhaltDelta() ,
                                                             artikel.inhaltHTML()  ,
                                                             autorEntity );
            artikelEntity = _artikelRepo.save( artikelEntity );

            LOG.info( "Neuen Artikel mit Titel \"{}\" von \"{}\" unter ID={} gespeichert.",
                      artikelEntity.getTitel(), anmeldeName, artikelEntity.getId() );

            final String forwardToPfad = "/app/artikel/" + artikelEntity.getId();
            
            return new ResponseEntity<>( forwardToPfad, CREATED );
        }
        catch ( JsonProcessingException ex ) {

            final String fehlerText = "JSON mit neuen Artikel kann nicht deserialisiert werden. " + 
                                      ex.getMessage();
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, BAD_REQUEST );
        }
    }
    
    
    /**
     * REST-Endpunkt um geänderten Artikel zu speichern.
     *
     * @param jsonPayload JSON-Payload vom Frontend mit neuem Artikel.
     * 
     * @param authentication Objekt, um Authentifzierung abzufragen
     *
     * @return Mögliche HTTP-Status-Codes:
     *         <ul>
     *         <li>200 (OK): Erfolg, Body enthält Pfad, an dem der geänderte Artikel zu finden ist.</li>
     *         <li>400 (Bad Request): 
     *         <ul>
     *         <li>Leerer Titel</li>
     *         <li>JSON-Payload konnte nicht deserialisert werden</li>
     *         <li>Artikel mit ID aus Payload nicht gefunden</li>         
     *         </ul>
     *         In allen Fällen enthält die Payload eine Fehlermeldung.
     *         </li>
     *         <li>401 (Unauthorized): Nutzer nicht angemeldet</li>
     *         </ul>
     */
    @PostMapping( "/aendern" )
    public ResponseEntity<String> artikelAendern( @RequestBody String jsonPayload,
                                                  Authentication authentication) {    
        
        if ( authentication == null || authentication.isAuthenticated() == false ) {
            
            final String fehlerText = "Unangemeldeter Nutzer kann Artikel nicht ändern.";
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, UNAUTHORIZED );            
        }        
        
        try {
                        
            final ArtikelDTO artikelDTO = _objectMapper.readValue( jsonPayload, ArtikelDTO.class );           
            if ( artikelDTO.titel().isBlank() ) {

                return new ResponseEntity<>( "Titel von zu änderndem Artikel ist leer", BAD_REQUEST );
            }            
            
            long artikelId = artikelDTO.artikelID();
            final Optional<ArtikelEntity> artikelOptional = _artikelRepo.findById( artikelId );
            if ( artikelOptional.isEmpty() ) {
                
                final String fehlerText = "Kein Artikel mit ID=" + artikelId + " zum Ändern gefunden.";
                LOG.error( fehlerText );
                return new ResponseEntity<>( fehlerText, BAD_REQUEST );
            }
            
            final ArtikelEntity artikelEntity = artikelOptional.get();
            
            artikelEntity.setTitel(       artikelDTO.titel()       );
            artikelEntity.setInhaltDelta( artikelDTO.inhaltDelta() );
            artikelEntity.setInhaltHTML(  artikelDTO.inhaltHTML()  );
            artikelEntity.setZeitpunktGeaendert( now()             );
            
            _artikelRepo.save( artikelEntity );
            
            LOG.info( "Geänderter Artikel mit ID={} auf DB geschrieben: \"{}\"", 
                      artikelDTO.artikelID(), artikelDTO.titel() );
            
            final String forwardToPfad = "/app/artikel/" + artikelId;
            
            return new ResponseEntity<>( forwardToPfad, OK );
        }
        catch ( JsonProcessingException ex ) {

            final String fehlerText = "JSON mit geändertem Artikel kann nicht deserialisiert werden. " + 
                                      ex.getMessage();
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, BAD_REQUEST );
        }
    }

}
