package de.eldecker.dhbw.spring.blog.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.eldecker.dhbw.spring.blog.model.ArtikelDTO;


@RestController
@RequestMapping( "/api/v1" )
public class BlogRestController {

    private static final Logger LOG = LoggerFactory.getLogger( BlogRestController.class );
    
    /** Objekt für Deserialisierung von JSON-Payload von Frontend. */
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    
    /**
     * REST-Endpunkt um neue Artikel (mit RichText-Editor erstellt) zu speichern.
     * 
     * @param jsonPayload JSON-Payload vom Frontend mit neuem Artikel.
     * 
     * @return Im Erfolgsfall wird HTTP-Status-Code 200 (OK) zurückgeben, der
     *         Response-Body besteht dann nur aus der ID des (neuen) Artikels,
     *         zu dem weitergeleitet werden soll.
     */
    @PostMapping( "/speichern" )
    public ResponseEntity<String> artikelSpeichern( @RequestBody String jsonPayload ) {
        
        LOG.info( "JSON-Payload empfangen: {}", jsonPayload );
        
        try {
        
            final ArtikelDTO artikel = _objectMapper.readValue( jsonPayload, ArtikelDTO.class );
            
            LOG.info( "Payload deserialisiert: " + artikel );
        }
        catch ( JsonProcessingException ex ) {
            
            final String fehlerText = "JSON mit Artikel kann nicht deserialisiert werden. " + ex.getMessage();  
            
            LOG.error( fehlerText );
            return new ResponseEntity<>( fehlerText, BAD_REQUEST ); // HTTP-Status-Code 201, Nachricht enthält ID neuer Artikel
        }
            
        return new ResponseEntity<>( "123", OK ); // HTTP-Status-Code 201 
    }
    
}
