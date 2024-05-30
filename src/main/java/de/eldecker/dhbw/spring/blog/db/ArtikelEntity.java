package de.eldecker.dhbw.spring.blog.db;

import static java.time.LocalDateTime.now;

import static jakarta.persistence.GenerationType.AUTO;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table( name = "ARTIKEL" )
public class ArtikelEntity {

    /** Primärschlüssel, wird von JPA gesetzt/verwaltet. */
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
        
    private String titel;
    
    /** 
     * Artikel im Delta-Format von quilljs (falls geändert werden muss).
     * Kann größer als 4k werden, deshalb Annotation {@code LOB} für {@code CLOB} auf Datenbank. 
     */
    @Lob
    @Column( name= "INHALT_DELTA")
    private String inhaltDelta;
    
    /** 
     * Artikel im HTML-Format für Darstellung mit statischer Seite.
     * Kann größer als 4k werden, deshalb Annotation {@code LOB} für {@code CLOB} auf Datenbank. 
     */
    @Lob
    @Column( name= "INHALT_HTML")
    private String inhaltHTML;
    
    @Column( name= "ZEITPUNKT_ANGELEGT")
    private LocalDateTime zeitpunktAngelegt;
    
    
    /**
     * Default-Konstruktor, obligatorisch für JPA.
     */
    public ArtikelEntity() {
        
        titel       = "";
        inhaltDelta = "";
        inhaltHTML  = "";
    }
    
    /**
     * Default-Konstruktor, obligatorisch für JPA.
     */
    public ArtikelEntity( String titel, String inhaltDelta, String inhaltHTML ) {
        
        this.titel       = titel;
        this.inhaltDelta = inhaltDelta;
        this.inhaltHTML  = inhaltHTML;
        
        this.zeitpunktAngelegt = now();
    }
    
    /**
     * Getter für Primärschlüssel. Da ID von JPA verwaltet wird gibt es keine
     * analoge Setter-Methode.
     * 
     * @return Primärschlüssel/ID des Artikels
     */
    public Long getId() {
        
        return id;
    }

    public String getTitel() {
        
        return titel;
    }

    public void setTitel( String titel ) {
        
        this.titel = titel;
    }

    public String getInhaltDelta() {
        
        return inhaltDelta;
    }

    public void setInhaltDelta( String inhaltDelta ) {
        
        this.inhaltDelta = inhaltDelta;
    }

    public String getInhaltHTML() {
        
        return inhaltHTML;
    }

    public void setInhaltHTML( String inhaltHTML ) {
        
        this.inhaltHTML = inhaltHTML;
    }

    public LocalDateTime getZeitpunktAngelegt() {
        
        return zeitpunktAngelegt;
    }

    public void setZeitpunktAngelegt( LocalDateTime zeitpunktAngelegt ) {
        
        this.zeitpunktAngelegt = zeitpunktAngelegt;
    }
    
    
}
