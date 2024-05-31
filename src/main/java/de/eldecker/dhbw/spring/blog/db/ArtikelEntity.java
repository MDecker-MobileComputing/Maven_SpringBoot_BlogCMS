package de.eldecker.dhbw.spring.blog.db;

import static java.time.LocalDateTime.now;

import static jakarta.persistence.GenerationType.AUTO;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Klasse repräsentiert einen Blog-Artikel inkl. Überschrift und
 * Content (als clob).
 */
@Entity
@Table( name = "ARTIKEL" )
public class ArtikelEntity {

    /** Primärschlüssel, wird von JPA gesetzt/verwaltet. */
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    /** Attribute mit Titel/Überschrift von Artikel. */
    private String titel;

    /**
     * Artikel im Delta-Format von quilljs (falls geändert werden muss), ohne Überschrift/Titel.
     * Kann größer als 4k werden, deshalb Annotation {@code LOB} für {@code CLOB} auf Datenbank.
     */
    @Lob
    @Column( name= "INHALT_DELTA")
    private String inhaltDelta;

    /**
     * Artikel im HTML-Format für Darstellung mit statischer Seite, ohne Überschrift/Titel.
     * Kann größer als 4k werden, deshalb Annotation {@code LOB} für {@code CLOB} auf Datenbank.
     */
    @Lob
    @Column( name= "INHALT_HTML")
    private String inhaltHTML;

    /** Zeitpunkt (Datum+Uhrzeit), zu dem der Artikel angelegt wurde. */
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


    /**
     * Getter für Überschrift/Titel des Blog-Artikels.
     *
     * @return Überschrift des Artikels
     */
    public String getTitel() {

        return titel;
    }


    /**
     * Setter für Überschrift/Titel des Blog-Artikels.
     *
     * @param titel Überschrift des Artikels
     */
    public void setTitel( String titel ) {

        this.titel = titel;
    }


    /**
     * Getter für Inhalt im Delta-Format von quill.js.
     *
     * @return Artikel im Delta-Format  (ohne Titel/Überschrift)
     */
    public String getInhaltDelta() {

        return inhaltDelta;
    }


    /**
     * Setter für Inhalt im Delta-Format von quill.js.
     *
     * @param inhaltDelta Artikel im Delta-Format  (ohne Titel/Überschrift)
     */
    public void setInhaltDelta( String inhaltDelta ) {

        this.inhaltDelta = inhaltDelta;
    }


    /**
     * Getter für Inhalt im HTML-Format.
     *
     * @return Artikel im HTML-Format (ohne Titel/Überschrift)
     */
    public String getInhaltHTML() {

        return inhaltHTML;
    }


    /**
     * Setter für Inhalt im HTML-Format.
     *
     * @param inhaltHTML Artikel im HTML-Format (ohne Titel/Überschrift)
     */
    public void setInhaltHTML( String inhaltHTML ) {

        this.inhaltHTML = inhaltHTML;
    }


    /**
     * Getter für Zeitpunkt, zu dem der Artikel angelegt wurde.
     *
     * @return Zeitpunkt (Datum+Uhrzeit)
     */
    public LocalDateTime getZeitpunktAngelegt() {

        return zeitpunktAngelegt;
    }


    /**
     * Setter für Zeitpunkt, zu dem der Artikel angelegt wurde.
     *
     * @param zeitpunktAngelegt Zeitpunkt (Datum+Uhrzeit)
     */
    public void setZeitpunktAngelegt( LocalDateTime zeitpunktAngelegt ) {

        this.zeitpunktAngelegt = zeitpunktAngelegt;
    }


    /**
     * Methode liefert String-Repräsentation des Objekts zurück
     *
     * @return String mit ID + Titel, aber ohne eigentlichen Inhalt (wäre zu lang)
     */
    @Override
    public String toString() {

        return "Artikel mit ID=" + id + ": " + titel;
    }


    /**
     * Berechnet Hash-Wert für Objekt, in dem die ID nicht eingeht (weil sie von JPA
     * evtl. noch nicht gesetzt worden ist).
     *
     * @return Hash-Wert, der eindeutig für aufrufendes Objekt sein sollte
     */
    @Override
    public int hashCode() {

        return Objects.hash( titel, inhaltDelta, inhaltHTML, zeitpunktAngelegt );
    }

    
    /**
     * Vergleich aufrufendes Objekt mit {@code obj}.
     * 
     * @return {@code true} gdw. {code obj} auch ein Objekt der Klasse {@link ArtikelEntity}
     *         ist und alle Attribute (bis auf die ID) dieselben Werte haben
     */
    @Override
    public boolean equals( Object obj ) {

        if ( obj == this ) { return true; }

        if ( obj == null ) { return false; }

        if ( obj instanceof ArtikelEntity andererArtikel ) {

            return Objects.equals( titel            , andererArtikel.titel             ) &&
                   Objects.equals( inhaltDelta      , andererArtikel.inhaltDelta       ) &&
                   Objects.equals( inhaltHTML       , andererArtikel.inhaltHTML        ) &&
                   Objects.equals( zeitpunktAngelegt, andererArtikel.zeitpunktAngelegt );
        } else {

            return false;
        }
    }

}
