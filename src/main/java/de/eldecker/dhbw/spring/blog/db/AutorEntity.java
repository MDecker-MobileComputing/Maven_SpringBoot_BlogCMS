package de.eldecker.dhbw.spring.blog.db;

import static jakarta.persistence.GenerationType.AUTO;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


/**
 * Entität für einen Autor. Zum Lesen muss man nicht bei der Webseite angemeldet 
 * sein, aber zum Erstellen von Blog-Artikeln schon. Jeder Autor kann auch nur
 * seine eigenen Artikel ändern.
 */
@Entity
@Table( name = "AUTOR" )
public class AutorEntity {

    /** Primärschlüssel, wird von JPA gesetzt/verwaltet. */
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    
    /** Nutzername des Autors. */
    private String name;
    
    /** 
     * Passwort des Autors (bcrypted).
     * <br><br>
     * 
     * <a href="https://en.wikipedia.org/wiki/Bcrypt#Description">Erklärung bcrypt-Algo auf engl. Wikipedia</a> 
     */
    private String passwort;
    
    /** Wenn {@code true}, dann ist der Autor auch ein Admin und kann andere Nutzer-Konto anlegen. */
    private boolean admin;
    
    /** 
     * Liste der Artikel, die der Autor geschrieben hat.
     * Der Owner dieser Assoziation ist {@link ArtikelEntity}, siehe
     * Attribut {@code autor} in dieser Klasse.
     */
    @OneToMany( mappedBy = "autor" )
    private List<ArtikelEntity> artikel = new ArrayList<>( 10 ); 
    
    
    /** 
     * Default-Konstruktor, wird von JPA benötigt. 
     */
    public AutorEntity() {
        
        name     = "";
        passwort = "";
        
        admin = false;
    }
    
    
    /**
     * Konstruktor um Autor mit Nutzername und Passwort anzulegen.
     * 
     * @param name Nutzername
     * 
     * @param passwort Passwort
     */
    public AutorEntity( String name, String passwort ) {
     
        this.name     = name;
        this.passwort = passwort;
        
        admin = false;
    }

    /**
     * Konstruktor um Autor mit Nutzername und Passwort anzulegen,
     * mit {@code istAdmin} kann zusätzlich festgelegt werden, ob der
     * Nutzer ein Admin sein soll oder nicht.
     * 
     * @param name Nutzername
     * 
     * @param passwort Passwort
     * 
     * @param istAdmin {@code true} gdw. der Autor ein Admin sein soll,
     *                 also auch andere Autoren anlegen können soll.
     */
    public AutorEntity( String name, String passwort, boolean istAdmin ) {
        
        this( name, passwort );
        
        admin = istAdmin;
    }

    
    /**
     * Getter für ID/Primärschlüssel. Da die ID von JPA verwaltet wird,
     * gibt es keinen Setter für die ID.
     * 
     * @return ID des Autors
     */
    public Long getId() {
        
        return id;
    }

    
    /**
     * Getter für Nutzername.
     * 
     * @return Nutzername des Autors
     */
    public String getName() {
        
        return name;
    }

    
    /**
     * Setter für Nutzername.
     * 
     * @param name Nutzername des Autors
     */
    public void setName( String name ) {
        
        this.name = name;
    }


    /**
     * Getter für Passwort.
     * 
     * @return Passwort (bcrypted)
     */
    public String getPasswort() {
        
        return passwort;
    }


    /**
     * Setter für Passwort.
     * 
     * @param passwort Passwort (bcrypted)
     */
    public void setPasswort( String passwort ) {
        
        this.passwort = passwort;
    }
    
    
    /**
     * Getter für Abfrage, ob Autor ein Admin ist.
     * 
     * @return {@code true} gdw. wenn Autor ein Admin ist.
     */
    public boolean isAdmin() {
        
        return admin;
    }
    
    
    /**
     * Getter für Liste der Artikel, die der Autor geschrieben hat
     * und deshalb auch ändern darf.
     * 
     * @return Liste der Artikel von Autor
     */
    public List<ArtikelEntity> getArtikel() {
    
        return artikel;
    }

    
    /**
     * String-Repräsentation des Autors.
     * 
     * @return String mit Begriff "Autor" und Nutzername; 
     *         Passwort darf nicht enthalten sein (auch wenn es bcrypted ist).
     */
    @Override
    public String toString() {
        
        final String str = format( "Autor \"%s\", hat %d Artikel geschrieben.", 
                                   name, artikel.size() );        
        return str; 
    }
    
    
    /**
     * Berechnet Hashwert für das Objekt.
     * <br><br>
     * 
     * Die ID darf nicht ein Input-Wert für die Hash-Berechnung sein,
     * weil Sie für neue Objekte erst beim Persistieren von JPA
     * gesetzt wird.  
     *
     * @return Hashwert für alle Attribute des Objekts (bis auf ID) ein.
     */
    @Override
    public int hashCode() {

        return Objects.hash( name, passwort, artikel, admin );
    }
    
    
    /**
     * Vergleich aufrufendes Objekt mit {@code obj}.
     * 
     * @return {@code true} gdw. {@code obj} auch eine Instanz von {@link AutorEntity}
     *         ist und alle Attribute bis auf die ID denselben Wert haben. 
     */
    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) { return true; }

        if ( obj == null ) { return false; }

        if ( obj instanceof AutorEntity andererAutor ) {

            return Objects.equals( name    , andererAutor.name     ) && 
                   Objects.equals( passwort, andererAutor.passwort ) &&
                   Objects.equals( artikel , andererAutor.artikel  ) &&
                   admin == andererAutor.admin;
            
        } else {
            
            return false;
        }
    }
}
