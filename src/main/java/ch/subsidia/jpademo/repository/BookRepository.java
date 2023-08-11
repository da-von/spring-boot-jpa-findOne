package ch.subsidia.jpademo.repository;

import ch.subsidia.jpademo.repository.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
