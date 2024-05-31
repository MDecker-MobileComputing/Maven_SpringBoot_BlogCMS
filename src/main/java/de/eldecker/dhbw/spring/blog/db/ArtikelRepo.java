package de.eldecker.dhbw.spring.blog.db;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository für {@link ArtikelEntity}, wird von <i>Spring Data JPA</i> 
 * automatisch implementiert und instanziiert.
 */
public interface ArtikelRepo extends JpaRepository<ArtikelEntity, Long> {

}
