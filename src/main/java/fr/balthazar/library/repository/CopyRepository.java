package fr.balthazar.library.repository;

import fr.balthazar.library.domain.Copy;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Copy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CopyRepository extends JpaRepository<Copy, Long> {

}
