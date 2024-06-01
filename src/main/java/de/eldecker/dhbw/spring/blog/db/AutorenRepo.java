package de.eldecker.dhbw.spring.blog.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Repository für {@link AutorEntity}, wird von <i>Spring Data JPA</i>
 * automatisch implementiert und instanziiert.
 */
@RepositoryRestResource( exported = false )
public interface AutorenRepo extends JpaRepository<AutorEntity, Long> {

    /**
     * Derived Query Method: Autor anhand Anmeldenamen suchen.
     * 
     * @param name Anmeldename des Autors
     * 
     * @return Optional enthält zugehöriges {@link AutorEntity}-Objekt
     *         wenn gefunden. 
     */
    Optional<AutorEntity> findByName( String name );

}
