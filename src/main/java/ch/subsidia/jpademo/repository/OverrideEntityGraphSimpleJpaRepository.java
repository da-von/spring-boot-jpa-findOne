package ch.subsidia.jpademo.repository;

import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphPagingAndSortingRepository;
import lombok.val;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface OverrideEntityGraphSimpleJpaRepository<T, ID> extends JpaRepository<T, ID>, EntityGraphPagingAndSortingRepository<T, ID>, EntityGraphJpaSpecificationExecutor<T> {

    @Override
    default Optional<T> findOne(Specification<T> spec) {
        return findOne(spec, (EntityGraph) null);
    }

    @Override
    default Optional<T> findOne(Specification<T> spec, EntityGraph entityGraph) {
        val items = findAll(spec, entityGraph);

        if (items.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1);
        }
        if (items.size() == 1) {
            return Optional.of(items.get(0));
        }
        return Optional.empty();
    }
}
