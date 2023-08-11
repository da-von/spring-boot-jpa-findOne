package ch.subsidia.jpademo.repository.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Data
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Transient
    private UUID uuid;

    public BaseEntity() {
        uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseEntity that = (BaseEntity) o;

        // compare uuid for unsaved entities
        if (id == that.id && id == 0) {
            return Objects.equals(uuid, that.uuid);
        }

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid);
    }
}
