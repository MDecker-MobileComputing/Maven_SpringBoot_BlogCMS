package de.eldecker.dhbw.spring.blog.model;


/**
 * DTO (Data Transfer Objekt) für JSON-Payload von Browser zu Backend.
 *
 * @param artikelID Primärschlüssel; ist für neue Artikel {@code -1}
 *
 * @param titel Titel/Überschrift des Artikels
 *
 * @param inhaltDelta Inhalt im Delta-Format von quill.js
 *
 * @param inhaltHTML Inhalt in HTML-Format (für Darstellung mit Thymeleaf)
 *
 * @param inhaltPlain Inhalt im Plaintext-Format (für Suche)
 */
public record ArtikelDTO( int    artikelID   ,
                          String titel       ,
                          String inhaltDelta ,
                          String inhaltHTML  ,
                          String inhaltPlain
                        ) {
}
