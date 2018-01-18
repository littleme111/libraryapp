package fr.balthazar.library.web.rest;

import fr.balthazar.library.LibraryappApp;

import fr.balthazar.library.domain.Copy;
import fr.balthazar.library.repository.CopyRepository;
import fr.balthazar.library.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static fr.balthazar.library.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CopyResource REST controller.
 *
 * @see CopyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryappApp.class)
public class CopyResourceIntTest {

    private static final Boolean DEFAULT_AVAILABLE = false;
    private static final Boolean UPDATED_AVAILABLE = true;

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCopyMockMvc;

    private Copy copy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CopyResource copyResource = new CopyResource(copyRepository);
        this.restCopyMockMvc = MockMvcBuilders.standaloneSetup(copyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Copy createEntity(EntityManager em) {
        Copy copy = new Copy()
            .available(DEFAULT_AVAILABLE);
        return copy;
    }

    @Before
    public void initTest() {
        copy = createEntity(em);
    }

    @Test
    @Transactional
    public void createCopy() throws Exception {
        int databaseSizeBeforeCreate = copyRepository.findAll().size();

        // Create the Copy
        restCopyMockMvc.perform(post("/api/copies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(copy)))
            .andExpect(status().isCreated());

        // Validate the Copy in the database
        List<Copy> copyList = copyRepository.findAll();
        assertThat(copyList).hasSize(databaseSizeBeforeCreate + 1);
        Copy testCopy = copyList.get(copyList.size() - 1);
        assertThat(testCopy.isAvailable()).isEqualTo(DEFAULT_AVAILABLE);
    }

    @Test
    @Transactional
    public void createCopyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = copyRepository.findAll().size();

        // Create the Copy with an existing ID
        copy.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCopyMockMvc.perform(post("/api/copies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(copy)))
            .andExpect(status().isBadRequest());

        // Validate the Copy in the database
        List<Copy> copyList = copyRepository.findAll();
        assertThat(copyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCopies() throws Exception {
        // Initialize the database
        copyRepository.saveAndFlush(copy);

        // Get all the copyList
        restCopyMockMvc.perform(get("/api/copies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(copy.getId().intValue())))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.booleanValue())));
    }

    @Test
    @Transactional
    public void getCopy() throws Exception {
        // Initialize the database
        copyRepository.saveAndFlush(copy);

        // Get the copy
        restCopyMockMvc.perform(get("/api/copies/{id}", copy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(copy.getId().intValue()))
            .andExpect(jsonPath("$.available").value(DEFAULT_AVAILABLE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCopy() throws Exception {
        // Get the copy
        restCopyMockMvc.perform(get("/api/copies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCopy() throws Exception {
        // Initialize the database
        copyRepository.saveAndFlush(copy);
        int databaseSizeBeforeUpdate = copyRepository.findAll().size();

        // Update the copy
        Copy updatedCopy = copyRepository.findOne(copy.getId());
        // Disconnect from session so that the updates on updatedCopy are not directly saved in db
        em.detach(updatedCopy);
        updatedCopy
            .available(UPDATED_AVAILABLE);

        restCopyMockMvc.perform(put("/api/copies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCopy)))
            .andExpect(status().isOk());

        // Validate the Copy in the database
        List<Copy> copyList = copyRepository.findAll();
        assertThat(copyList).hasSize(databaseSizeBeforeUpdate);
        Copy testCopy = copyList.get(copyList.size() - 1);
        assertThat(testCopy.isAvailable()).isEqualTo(UPDATED_AVAILABLE);
    }

    @Test
    @Transactional
    public void updateNonExistingCopy() throws Exception {
        int databaseSizeBeforeUpdate = copyRepository.findAll().size();

        // Create the Copy

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCopyMockMvc.perform(put("/api/copies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(copy)))
            .andExpect(status().isCreated());

        // Validate the Copy in the database
        List<Copy> copyList = copyRepository.findAll();
        assertThat(copyList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCopy() throws Exception {
        // Initialize the database
        copyRepository.saveAndFlush(copy);
        int databaseSizeBeforeDelete = copyRepository.findAll().size();

        // Get the copy
        restCopyMockMvc.perform(delete("/api/copies/{id}", copy.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Copy> copyList = copyRepository.findAll();
        assertThat(copyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Copy.class);
        Copy copy1 = new Copy();
        copy1.setId(1L);
        Copy copy2 = new Copy();
        copy2.setId(copy1.getId());
        assertThat(copy1).isEqualTo(copy2);
        copy2.setId(2L);
        assertThat(copy1).isNotEqualTo(copy2);
        copy1.setId(null);
        assertThat(copy1).isNotEqualTo(copy2);
    }
}
