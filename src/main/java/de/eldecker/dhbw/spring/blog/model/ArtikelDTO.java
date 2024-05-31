package de.eldecker.dhbw.spring.blog.model;


/**
 * DTO (Data Transfer Objekt) für JSON-Payload von Browser.  
 * 
 * @param artikelID Primärschlüssel 
 * 
 * @param titel Titel/Überschrift des Artikels
 * 
 * @param inhaltDelta Inhalt im Delta-Format von quill.js
 * 
 * @param inhaltHTML Inhalt in HTML-Format (für Darstellung mit Thymeleaf)
 */
public record ArtikelDTO( int    artikelID  ,
                          String titel      ,
                          String inhaltDelta,
                          String inhaltHTML ) {
}
