package ch.subsidia.jpademo;

import ch.subsidia.jpademo.repository.AuthorRepository;
import ch.subsidia.jpademo.repository.BookRepository;
import ch.subsidia.jpademo.repository.model.Author;
import ch.subsidia.jpademo.repository.model.Book;
import ch.subsidia.jpademo.repository.model.EntityGraphs;
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import lombok.val;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.ArrayList;
import java.util.List;

import static ch.subsidia.jpademo.repository.model.EntityGraphs.AUTHOR_BOOKS;
import static ch.subsidia.jpademo.repository.model.EntityGraphs.BOOK_AUTHOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@SpringBootTest
class JpaDemoApplicationTests {

    @Autowired
    private DatabaseTruncator dbTruncator;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void resetDB() {
        dbTruncator.truncate();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void findById_withoutEntityGraph_expectLazyInitialization() {
        val authorId = storeSomeData();
        val optionalAuthor = authorRepository.findByAuthorId(authorId);

        assertFalse(optionalAuthor.isEmpty());
        assertThrowsExactly(LazyInitializationException.class, () -> optionalAuthor.get().getBooks().size());
    }

    @Test
    void findById_withEntityGraph_expectCompleteResolved() {
        val authorId = storeSomeData();
        val optionalAuthor = authorRepository.findByAuthorId(authorId, NamedEntityGraph.loading(AUTHOR_BOOKS));

        assertFalse(optionalAuthor.isEmpty());
        assertFalse(optionalAuthor.get().getBooks().isEmpty());
        assertEquals(3, optionalAuthor.get().getBooks().size());

    }

    @Test
    void findOne_withoutEntityGraph_expectLazyInitialization() {
        val authorId = storeSomeData();
        val optionalAuthor = authorRepository.findOne(authorId);

        assertFalse(optionalAuthor.isEmpty());
        assertThrowsExactly(LazyInitializationException.class, () -> optionalAuthor.get().getBooks().size());
    }

    @Test
    void findOne_withEntityGraph_expectCompleteResolved() {
        val authorId = storeSomeData();

        val expectedBooksCount = 3;

        // 3 existing books with valid author reference
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        assertEquals(expectedBooksCount, books.size());
        books.forEach(b -> assertEquals(authorId, b.getAuthor().getId()));

        // Access with findOne and EntityGraph
        val optionalAuthor = authorRepository.findOne(authorId, NamedEntityGraph.loading(AUTHOR_BOOKS));

        assertFalse(optionalAuthor.isEmpty());
        assertFalse(optionalAuthor.get().getBooks().isEmpty());
        assertEquals(expectedBooksCount, optionalAuthor.get().getBooks().size());

    }

    @Test
    void findOne_bookWithAuthorId_expectException() {
        val authorId = storeSomeData();

        assertThrowsExactly(org.springframework.dao.IncorrectResultSizeDataAccessException.class,
                () -> bookRepository.findOneFor(authorId));

    }

    @Test
    void findAll_booksWithAuthorId_expectException() {
        val authorId = storeSomeData();

        val expectedBooksCount = 3;
        val books = bookRepository.findAllFor(authorId, NamedEntityGraph.loading(BOOK_AUTHOR));

        assertEquals(expectedBooksCount, books.size());
        books.forEach(b -> assertEquals("Uncle Bob", b.getAuthor().getName()));

    }


    private long storeSomeData() {
        var authorWithBooks = Author.builder()
                .name("Uncle Bob")
                .build();

        authorWithBooks = authorRepository.save(authorWithBooks);

		bookRepository.saveAll(
				List.of(
						Book.builder().title("Clean Code").author(authorWithBooks).isbn("123").genre("Information Technology").build(),
						Book.builder().title("Clean Architecture").author(authorWithBooks).isbn("456").genre("Information Technology").build(),
						Book.builder().title("SOLID Software Design").author(authorWithBooks).isbn("789").genre("Information Technology").build()
				)
		);

        Assertions.assertTrue(authorWithBooks.getId() > 0);
        return authorWithBooks.getId();
    }

}
