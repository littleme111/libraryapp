package fr.balthazar.library.web.rest;

import fr.balthazar.library.LibraryappApp;

import fr.balthazar.library.domain.Borrow;
import fr.balthazar.library.repository.BorrowRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static fr.balthazar.library.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BorrowResource REST controller.
 *
 * @see BorrowResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LibraryappApp.class)
public class BorrowResourceIntTest {

    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RETURN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBorrowMockMvc;

    private Borrow borrow;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BorrowResource borrowResource = new BorrowResource(borrowRepository);
        this.restBorrowMockMvc = MockMvcBuilders.standaloneSetup(borrowResource)
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
    public static Borrow createEntity(EntityManager em) {
        Borrow borrow = new Borrow()
            .borrowDate(DEFAULT_BORROW_DATE)
            .returnDate(DEFAULT_RETURN_DATE);
        return borrow;
    }

    @Before
    public void initTest() {
        borrow = createEntity(em);
    }

    @Test
    @Transactional
    public void createBorrow() throws Exception {
        int databaseSizeBeforeCreate = borrowRepository.findAll().size();

        // Create the Borrow
        restBorrowMockMvc.perform(post("/api/borrows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isCreated());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeCreate + 1);
        Borrow testBorrow = borrowList.get(borrowList.size() - 1);
        assertThat(testBorrow.getBorrowDate()).isEqualTo(DEFAULT_BORROW_DATE);
        assertThat(testBorrow.getReturnDate()).isEqualTo(DEFAULT_RETURN_DATE);
    }

    @Test
    @Transactional
    public void createBorrowWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = borrowRepository.findAll().size();

        // Create the Borrow with an existing ID
        borrow.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBorrowMockMvc.perform(post("/api/borrows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isBadRequest());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBorrows() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        // Get all the borrowList
        restBorrowMockMvc.perform(get("/api/borrows?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrow.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));
    }

    @Test
    @Transactional
    public void getBorrow() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);

        // Get the borrow
        restBorrowMockMvc.perform(get("/api/borrows/{id}", borrow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(borrow.getId().intValue()))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBorrow() throws Exception {
        // Get the borrow
        restBorrowMockMvc.perform(get("/api/borrows/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBorrow() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();

        // Update the borrow
        Borrow updatedBorrow = borrowRepository.findOne(borrow.getId());
        // Disconnect from session so that the updates on updatedBorrow are not directly saved in db
        em.detach(updatedBorrow);
        updatedBorrow
            .borrowDate(UPDATED_BORROW_DATE)
            .returnDate(UPDATED_RETURN_DATE);

        restBorrowMockMvc.perform(put("/api/borrows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBorrow)))
            .andExpect(status().isOk());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate);
        Borrow testBorrow = borrowList.get(borrowList.size() - 1);
        assertThat(testBorrow.getBorrowDate()).isEqualTo(UPDATED_BORROW_DATE);
        assertThat(testBorrow.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingBorrow() throws Exception {
        int databaseSizeBeforeUpdate = borrowRepository.findAll().size();

        // Create the Borrow

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBorrowMockMvc.perform(put("/api/borrows")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(borrow)))
            .andExpect(status().isCreated());

        // Validate the Borrow in the database
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBorrow() throws Exception {
        // Initialize the database
        borrowRepository.saveAndFlush(borrow);
        int databaseSizeBeforeDelete = borrowRepository.findAll().size();

        // Get the borrow
        restBorrowMockMvc.perform(delete("/api/borrows/{id}", borrow.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Borrow> borrowList = borrowRepository.findAll();
        assertThat(borrowList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Borrow.class);
        Borrow borrow1 = new Borrow();
        borrow1.setId(1L);
        Borrow borrow2 = new Borrow();
        borrow2.setId(borrow1.getId());
        assertThat(borrow1).isEqualTo(borrow2);
        borrow2.setId(2L);
        assertThat(borrow1).isNotEqualTo(borrow2);
        borrow1.setId(null);
        assertThat(borrow1).isNotEqualTo(borrow2);
    }
}
