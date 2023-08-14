package ch.subsidia.jpademo.repository;

import ch.subsidia.jpademo.repository.model.Author_;
import ch.subsidia.jpademo.repository.model.Book;
import ch.subsidia.jpademo.repository.model.Book_;
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends OverrideEntityGraphSimpleJpaRepository<Book, Long>,
        EntityGraphJpaSpecificationExecutor<Book> {

    default Optional<Book> findOneFor(long authorId) {
        return findOne((b, cb, cq) -> cq.equal(b.get(Book_.AUTHOR).get(Author_.ID), authorId));
    }

    default List<Book> findAllFor(long authorId, EntityGraph entityGraph) {
        return findAll((b, cb, cq) -> cq.equal(b.get(Book_.AUTHOR).get(Author_.ID), authorId), entityGraph);
    }

}
