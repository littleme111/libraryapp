package fr.balthazar.library.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.balthazar.library.domain.Borrow;

import fr.balthazar.library.repository.BorrowRepository;
import fr.balthazar.library.web.rest.errors.BadRequestAlertException;
import fr.balthazar.library.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Borrow.
 */
@RestController
@RequestMapping("/api")
public class BorrowResource {

    private final Logger log = LoggerFactory.getLogger(BorrowResource.class);

    private static final String ENTITY_NAME = "borrow";

    private final BorrowRepository borrowRepository;

    public BorrowResource(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    /**
     * POST  /borrows : Create a new borrow.
     *
     * @param borrow the borrow to create
     * @return the ResponseEntity with status 201 (Created) and with body the new borrow, or with status 400 (Bad Request) if the borrow has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/borrows")
    @Timed
    public ResponseEntity<Borrow> createBorrow(@RequestBody Borrow borrow) throws URISyntaxException {
        log.debug("REST request to save Borrow : {}", borrow);
        if (borrow.getId() != null) {
            throw new BadRequestAlertException("A new borrow cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Borrow result = borrowRepository.save(borrow);
        return ResponseEntity.created(new URI("/api/borrows/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /borrows : Updates an existing borrow.
     *
     * @param borrow the borrow to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated borrow,
     * or with status 400 (Bad Request) if the borrow is not valid,
     * or with status 500 (Internal Server Error) if the borrow couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/borrows")
    @Timed
    public ResponseEntity<Borrow> updateBorrow(@RequestBody Borrow borrow) throws URISyntaxException {
        log.debug("REST request to update Borrow : {}", borrow);
        if (borrow.getId() == null) {
            return createBorrow(borrow);
        }
        Borrow result = borrowRepository.save(borrow);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, borrow.getId().toString()))
            .body(result);
    }

    /**
     * GET  /borrows : get all the borrows.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of borrows in body
     */
    @GetMapping("/borrows")
    @Timed
    public List<Borrow> getAllBorrows() {
        log.debug("REST request to get all Borrows");
        return borrowRepository.findAll();
        }

    /**
     * GET  /borrows/:id : get the "id" borrow.
     *
     * @param id the id of the borrow to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the borrow, or with status 404 (Not Found)
     */
    @GetMapping("/borrows/{id}")
    @Timed
    public ResponseEntity<Borrow> getBorrow(@PathVariable Long id) {
        log.debug("REST request to get Borrow : {}", id);
        Borrow borrow = borrowRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(borrow));
    }

    /**
     * DELETE  /borrows/:id : delete the "id" borrow.
     *
     * @param id the id of the borrow to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/borrows/{id}")
    @Timed
    public ResponseEntity<Void> deleteBorrow(@PathVariable Long id) {
        log.debug("REST request to delete Borrow : {}", id);
        borrowRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
