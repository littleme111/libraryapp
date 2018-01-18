package fr.balthazar.library.repository;

import fr.balthazar.library.domain.Borrow;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Borrow entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    @Query("select borrow from Borrow borrow where borrow.user.login = ?#{principal.username}")
    List<Borrow> findByUserIsCurrentUser();

}
