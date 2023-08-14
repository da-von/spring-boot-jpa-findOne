package ch.subsidia.jpademo.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static ch.subsidia.jpademo.repository.model.EntityGraphs.AUTHOR_BOOKS;
import static ch.subsidia.jpademo.repository.model.EntityGraphs.BOOK_AUTHOR;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = BOOK_AUTHOR,
                attributeNodes = {
                        @NamedAttributeNode(value = Book_.AUTHOR)
                }
        )
})
public class Book extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String genre;
}
