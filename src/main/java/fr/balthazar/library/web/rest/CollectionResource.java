package fr.balthazar.library.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.balthazar.library.domain.Collection;

import fr.balthazar.library.repository.CollectionRepository;
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
 * REST controller for managing Collection.
 */
@RestController
@RequestMapping("/api")
public class CollectionResource {

    private final Logger log = LoggerFactory.getLogger(CollectionResource.class);

    private static final String ENTITY_NAME = "collection";

    private final CollectionRepository collectionRepository;

    public CollectionResource(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * POST  /collections : Create a new collection.
     *
     * @param collection the collection to create
     * @return the ResponseEntity with status 201 (Created) and with body the new collection, or with status 400 (Bad Request) if the collection has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/collections")
    @Timed
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection) throws URISyntaxException {
        log.debug("REST request to save Collection : {}", collection);
        if (collection.getId() != null) {
            throw new BadRequestAlertException("A new collection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Collection result = collectionRepository.save(collection);
        return ResponseEntity.created(new URI("/api/collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /collections : Updates an existing collection.
     *
     * @param collection the collection to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated collection,
     * or with status 400 (Bad Request) if the collection is not valid,
     * or with status 500 (Internal Server Error) if the collection couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/collections")
    @Timed
    public ResponseEntity<Collection> updateCollection(@RequestBody Collection collection) throws URISyntaxException {
        log.debug("REST request to update Collection : {}", collection);
        if (collection.getId() == null) {
            return createCollection(collection);
        }
        Collection result = collectionRepository.save(collection);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, collection.getId().toString()))
            .body(result);
    }

    /**
     * GET  /collections : get all the collections.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of collections in body
     */
    @GetMapping("/collections")
    @Timed
    public List<Collection> getAllCollections() {
        log.debug("REST request to get all Collections");
        return collectionRepository.findAll();
        }

    /**
     * GET  /collections/:id : get the "id" collection.
     *
     * @param id the id of the collection to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the collection, or with status 404 (Not Found)
     */
    @GetMapping("/collections/{id}")
    @Timed
    public ResponseEntity<Collection> getCollection(@PathVariable Long id) {
        log.debug("REST request to get Collection : {}", id);
        Collection collection = collectionRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(collection));
    }

    /**
     * DELETE  /collections/:id : delete the "id" collection.
     *
     * @param id the id of the collection to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/collections/{id}")
    @Timed
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        log.debug("REST request to delete Collection : {}", id);
        collectionRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
