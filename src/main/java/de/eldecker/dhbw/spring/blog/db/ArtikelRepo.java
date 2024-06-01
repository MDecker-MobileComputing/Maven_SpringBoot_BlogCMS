package de.eldecker.dhbw.spring.blog.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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

}
