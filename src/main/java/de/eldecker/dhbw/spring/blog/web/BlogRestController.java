package de.eldecker.dhbw.spring.blog.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.eldecker.dhbw.spring.blog.db.ArtikelEntity;
import de.eldecker.dhbw.spring.blog.db.ArtikelRepo;
import de.eldecker.dhbw.spring.blog.model.ArtikelDTO;


/**
 * Controller-Klasse, die REST-Endpunkte bereitstellt.
 */
@RestController
@RequestMapping( "/api/v1" )
public class BlogRestController {

    private static final Logger LOG = LoggerFactory.getLogger( BlogRestController.class );
        
    /** Objekt für Deserialisierung von JSON-Payload von Frontend. */
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    /** Repo-Bean für Zugriff auf Tabelle mit Artikeln. */
    @Autowired
    private ArtikelRepo _artikelRepo;
    
    
    /**
     * REST-Endpunkt um neue Artikel (mit RichText-Editor erstellt) zu speichern.
     * 
     * @param jsonPayload JSON-Payload vom Frontend mit neuem Artikel.
     * 
     * @return Im Erfolgsfall wird HTTP-Status-Code 200 (OK) zurückgeben, der
     *         Response-Body enthalt dann den relativen Pfad, unter dem der 
     *         neue Artikel angezeigt wird (zu dem er Browser also weiterleiten
     *         soll).
     */
    @PostMapping( "/speichern" )
    public ResponseEntity<String> artikelSpeichern( @RequestBody String jsonPayload ) {
        
        LOG.info( "JSON-Payload empfangen: {}", jsonPayload );
        
        try {
        
            final ArtikelDTO artikel = _objectMapper.readValue( jsonPayload, ArtikelDTO.class );
            
            LOG.info( "Payload für Artikel mit Titel \"{}\" deserialisiert.", artikel.titel() );
            
            if ( artikel.titel().isBlank() ) {
                
                return new ResponseEntity<>( "Titel von Artikel ist leer", BAD_REQUEST ); 
            }
            
            ArtikelEntity artikelEntity = new ArtikelEntity( artikel.titel().trim(),
                                                             artikel.inhaltDelta(),
                                                             artikel.inhaltHTML() );
            artikelEntity = _artikelRepo.save( artikelEntity );
            
            LOG.info( "Artikel mit Titel \"{}\" unter ID={} gespeichert.", 
                      artikelEntity.getTitel(), artikelEntity.getId() );
            
            final String forwardToPfad = "/app/artikel/" + artikelEntity.getId();
            
            return new ResponseEntity<>( forwardToPfad, OK ); // HTTP-Status-Code 201
        }
        catch ( JsonProcessingException ex ) {
            
            final String fehlerText = "JSON mit Artikel kann nicht deserialisiert werden. " + ex.getMessage();  
            
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, BAD_REQUEST ); 
        }                     
    }
    
}
