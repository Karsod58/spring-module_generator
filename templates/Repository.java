package {{ module|lower }}.repository;

import {{ module|lower }}.entity.{{ module }}Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface {{ module }}Repository extends JpaRepository<{{ module }}Entity, Long> {
}
