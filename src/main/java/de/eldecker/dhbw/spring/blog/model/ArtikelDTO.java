package de.eldecker.dhbw.spring.blog.model;

/**
 * DTO (Data Transfer Objekt) für JSON-Payload von Browser.  
 */
public record ArtikelDTO( int artikelID,
                          String titel,
                          String inhaltDelta,
                          String inhaltHTML ) {
}
