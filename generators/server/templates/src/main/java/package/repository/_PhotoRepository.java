package <%=packageName%>.repository;

import <%=packageName%>.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Area entity.
 */
@SuppressWarnings("unused")
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByFileIsNotNull();

    List<Photo> findByProcessedFalse();

}
