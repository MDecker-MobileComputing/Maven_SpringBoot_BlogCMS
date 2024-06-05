package de.eldecker.dhbw.spring.blog.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Repository für {@link ArtikelEntity}, wird von <i>Spring Data JPA</i>
 * automatisch implementiert und instanziiert.
 */
@RepositoryRestResource( exported = false )
public interface ArtikelRepo extends JpaRepository<ArtikelEntity, Long> {

    /**
     * Derived Query Method: Liefert Liste aller Artikel zurück.
     *
     * @return Liste aller Artikel, sortiert nach absteigendem Erzeugungszeitpunkt
     */
    List<ArtikelEntity> findAllByOrderByZeitpunktAngelegtDesc();


   /**
     * Textesuche nach Artikeln anhand Inhalt und Titel.
     * <br><br>
     *
     * Weil die Spalte {@code inhaltPlain} vom Typ {@code CLOB} ist, muss
     * die Funktion {@code cast()} verwendet werden, um den Inhalt als
     * {@code string} zu behandeln.
     *
     * @param suchbegriff Sucbegriff, wird als Teil-String case-insensitive
     *                    in den Attribute/Spalten {@code titel} und {@code inhaltPlain}
     *                    gesucht.
     *
     * @return Suchergebnis: Liste der gefundenen Artikel, kann leer sein;
     *         sortiert nach absteigendem Erzeugungszeitpunkt, also neueste Artikel
     *         zuerst.
     */
    @Query( "SELECT a FROM ArtikelEntity a "                                                         +
            "WHERE lower(a.titel) LIKE lower(concat('%', :suchbegriff, '%')) OR "                    +
                  "lower(cast(a.inhaltPlain as string)) LIKE lower(concat('%', :suchbegriff, '%')) " +
                  "ORDER BY a.zeitpunktAngelegt DESC" )
    List<ArtikelEntity> holeArtikelTextsuche( @Param("suchbegriff") String suchbegriff );

}
