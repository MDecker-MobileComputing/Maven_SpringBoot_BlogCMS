package de.eldecker.dhbw.spring.blog.model;

/**
 * DTO (Data Transfer Objekt) für JSON-Payload von Backend zu Browser.
 * Wird für REST-Endpunkt benötigt, bei dem das Frontend einen zu ändernden
 * Artikel abruft.
 *  
 * @param titel Überschrift von Artikel
 *  
 * @param deltaInhalt Artikel mit Deltaformat für quilljs
 */
public record TitelUndDeltaInhaltDTO( String titel, 
                                      String deltaInhalt 
                                    ) {
}
