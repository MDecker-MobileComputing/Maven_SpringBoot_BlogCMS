package de.eldecker.dhbw.spring.blog.db;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.AUTO;

import static java.time.LocalDateTime.now;
import static java.lang.String.format;


import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
     * <br><br>
     * 
     * Beispiel für Delta-Format:
     * <pre>
     * {"ops":[{"attributes":{"bold":true},"insert":"Testbeitrag (Demo-Content)"},{"insert":"\n"}]}
     * </pre>
     */
    @Lob
    @Column( name= "INHALT_DELTA" )
    private String inhaltDelta;

    /**
     * Artikel im HTML-Format für Darstellung mit statischer Seite, ohne Überschrift/Titel.
     * Kann größer als 4k werden, deshalb Annotation {@code LOB} für {@code CLOB} auf Datenbank.
     */
    @Lob
    @Column( name= "INHALT_HTML" )
    private String inhaltHTML;

    /** Zeitpunkt (Datum+Uhrzeit), zu dem der Artikel angelegt wurde. */
    @Column( name= "ZEITPUNKT_ANGELEGT" )
    private LocalDateTime zeitpunktAngelegt;

    /** 
     * Zeitpunkt (Datum+Uhrzeit), zu dem der Artikel zum letzten Mal geändert wurde;
     * wenn der Artikel noch nie geändert wurde, dann ist der Zeitpunkt der selbe 
     * wie für {@link #zeitpunktAngelegt}.
     */
    @Column( name= "ZEITPUNKT_GAENDERT" )
    private LocalDateTime zeitpunktGeaendert;
    
    /**
     * Autor, der den Blog-Artikel angelegt hat und ihn ändern darf
     * (ein Autor darf nur die von ihm angelegten Artikel ändern). 
     * Die Autoren-Entity ist der "Owner" dieser Assoziation, da in ihrer
     * Tabelle es eine Spalte für das Fremdschlüsselattribut gibt. 
     */
    @ManyToOne( fetch = EAGER )
    @JoinColumn( name = "autor__fk", referencedColumnName = "id" )
    private AutorEntity autor;    


    /**
     * Default-Konstruktor, obligatorisch für JPA.
     */
    public ArtikelEntity() {

        titel       = "";
        inhaltDelta = "";
        inhaltHTML  = "";
        
        autor = null;
    }


    /**
     * Konstruktor um Werte für drei {@code String}-Attribute zu setzen.
     * <br><br>
     * 
     * Zeitpunkt für Anlegen und letzte Änderung wird auf aktuelle Systemzeit
     * gesetzt.
     * 
     * @param titel Titel/Überschrift von Artikel
     * 
     * @param inhaltDelta Inhalt (ohne Titel/Überschrift) im quill.js-eigenen Delta-Format
     * 
     * @param inhaltHTML Inhalt (ohne Titel/Überschrift) im HTML-Format 
     */
    public ArtikelEntity( String titel, String inhaltDelta, String inhaltHTML ) {

        this.titel       = titel;
        this.inhaltDelta = inhaltDelta;
        this.inhaltHTML  = inhaltHTML;

        this.zeitpunktAngelegt  = now();
        this.zeitpunktGeaendert = now();
        
        autor = null;
    }
    
    /**
     * Konstruktor um Werte für die drei String-Attribute und den Autor zu setzen.
     * <br><br>
     * 
     * Zeitpunkt für Anlegen und letzte Änderung wird auf aktuelle Systemzeit
     * gesetzt. 
     * 
     * @param titel Titel/Überschrift von Artikel
     * 
     * @param inhaltDelta Inhalt (ohne Titel/Überschrift) im quill.js-eigenen Delta-Format
     * 
     * @param inhaltHTML Inhalt (ohne Titel/Überschrift) im HTML-Format 
     * 
     * @param autor Autor, der den Artikel geschrieben hat
     */
    public ArtikelEntity( String titel, String inhaltDelta, String inhaltHTML, AutorEntity autor ) {
       
        this ( titel, inhaltDelta, inhaltHTML );
        
        this.autor = autor;
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
     * Getter für Zeitpunkt, zu dem der Artikel zum letzten Mal geändert wurde.
     * 
     * @return Letzter Änderungszeitpunkt; gleicher Wert wie von  
     *         {@link #getZeitpunktAngelegt()} wenn noch nie geändert.
     */
    public LocalDateTime getZeitpunktGeaendert() {
        
        return zeitpunktGeaendert;
    }


    /**
     * Setter für Zeitpunkt, zu dem der Artikel zum letzten Mal geändert wurde.
     * 
     * @param  zeitpunktGeaendert Letzter Änderungszeitpunkt
     */    
    public void setZeitpunktGeaendert( LocalDateTime zeitpunktGeaendert ) {
        
        this.zeitpunktGeaendert = zeitpunktGeaendert;
    }
        
    
    /**
     * Getter für Autor des Artikels.
     * 
     * @return Autor, der Artikel geschrieben und evtl. auch geändert hat.
     */
    public AutorEntity getAutor() {
        
        return autor;
    }

    
    /**
     * Setter für Autor des Artikels.
     * 
     * @param autor Autor, der Artikel geschrieben und evtl. auch geändert hat.
     */
    public void setAutor( AutorEntity autor ) {
        
        this.autor = autor;
    }


    /**
     * Methode liefert String-Repräsentation des Objekts zurück
     *
     * @return String mit ID + Autor + Titel, aber ohne eigentlichen Inhalt
     */
    @Override
    public String toString() {
        
        final String str = format( "Artikel mit ID=%d von Autor %s: \"%s\"", 
                                   id, autor, titel );

        return str;
    }


    /**
     * Berechnet Hash-Wert für Objekt, in dem die ID nicht eingeht (weil sie von JPA
     * evtl. noch nicht gesetzt worden ist).
     *
     * @return Hash-Wert, der eindeutig für aufrufendes Objekt sein sollte
     */
    @Override
    public int hashCode() {

        return Objects.hash( titel, 
                             inhaltDelta, inhaltHTML, 
                             zeitpunktAngelegt, zeitpunktGeaendert,
                             autor
                           );
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

            return Objects.equals( titel             , andererArtikel.titel              ) &&
                   Objects.equals( inhaltDelta       , andererArtikel.inhaltDelta        ) &&
                   Objects.equals( inhaltHTML        , andererArtikel.inhaltHTML         ) &&
                   Objects.equals( zeitpunktAngelegt , andererArtikel.zeitpunktAngelegt  ) &&
                   Objects.equals( zeitpunktGeaendert, andererArtikel.zeitpunktGeaendert ) &&
                   Objects.equals( autor             , andererArtikel.autor              );
        } else {

            return false;
        }
    }

}
