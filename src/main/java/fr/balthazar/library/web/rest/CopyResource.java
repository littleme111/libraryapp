package fr.balthazar.library.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.balthazar.library.domain.Copy;

import fr.balthazar.library.repository.CopyRepository;
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
 * REST controller for managing Copy.
 */
@RestController
@RequestMapping("/api")
public class CopyResource {

    private final Logger log = LoggerFactory.getLogger(CopyResource.class);

    private static final String ENTITY_NAME = "copy";

    private final CopyRepository copyRepository;

    public CopyResource(CopyRepository copyRepository) {
        this.copyRepository = copyRepository;
    }

    /**
     * POST  /copies : Create a new copy.
     *
     * @param copy the copy to create
     * @return the ResponseEntity with status 201 (Created) and with body the new copy, or with status 400 (Bad Request) if the copy has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/copies")
    @Timed
    public ResponseEntity<Copy> createCopy(@RequestBody Copy copy) throws URISyntaxException {
        log.debug("REST request to save Copy : {}", copy);
        if (copy.getId() != null) {
            throw new BadRequestAlertException("A new copy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Copy result = copyRepository.save(copy);
        return ResponseEntity.created(new URI("/api/copies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /copies : Updates an existing copy.
     *
     * @param copy the copy to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated copy,
     * or with status 400 (Bad Request) if the copy is not valid,
     * or with status 500 (Internal Server Error) if the copy couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/copies")
    @Timed
    public ResponseEntity<Copy> updateCopy(@RequestBody Copy copy) throws URISyntaxException {
        log.debug("REST request to update Copy : {}", copy);
        if (copy.getId() == null) {
            return createCopy(copy);
        }
        Copy result = copyRepository.save(copy);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, copy.getId().toString()))
            .body(result);
    }

    /**
     * GET  /copies : get all the copies.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of copies in body
     */
    @GetMapping("/copies")
    @Timed
    public List<Copy> getAllCopies() {
        log.debug("REST request to get all Copies");
        return copyRepository.findAll();
        }

    /**
     * GET  /copies/:id : get the "id" copy.
     *
     * @param id the id of the copy to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the copy, or with status 404 (Not Found)
     */
    @GetMapping("/copies/{id}")
    @Timed
    public ResponseEntity<Copy> getCopy(@PathVariable Long id) {
        log.debug("REST request to get Copy : {}", id);
        Copy copy = copyRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(copy));
    }

    /**
     * DELETE  /copies/:id : delete the "id" copy.
     *
     * @param id the id of the copy to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/copies/{id}")
    @Timed
    public ResponseEntity<Void> deleteCopy(@PathVariable Long id) {
        log.debug("REST request to delete Copy : {}", id);
        copyRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
