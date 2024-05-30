package de.eldecker.dhbw.spring.blog.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

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
    
    @PostMapping( "/speichern" )
    public ResponseEntity<String> artikelSpeichern( @RequestBody String jsonPayload ) {
        
        LOG.info( "JSON-Payload empfangen: {}", jsonPayload );
        
        try {
        
            final ArtikelDTO artikel = _objectMapper.readValue( jsonPayload, ArtikelDTO.class );
            
            LOG.info( "Payload deserialisiert: " + artikel );
        }
        catch ( JsonProcessingException ex ) {
            
            final String fehlerText = "JSON mit Artikel kann nicht deserialisiert werden. " + ex.getMessage();  
            
            LOG.error( fehlerText, ex );
            return new ResponseEntity<>( "Content empfangen", BAD_REQUEST ); // HTTP-Status-Code 201
        }
            
        return new ResponseEntity<>( "Content empfangen", CREATED ); // HTTP-Status-Code 201 
    }
    
}
