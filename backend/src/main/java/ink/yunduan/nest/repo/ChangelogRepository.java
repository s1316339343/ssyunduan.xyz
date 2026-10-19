package ink.yunduan.nest.repo;

import ink.yunduan.nest.domain.ChangelogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangelogRepository extends JpaRepository<ChangelogEntry, Long> {
    List<ChangelogEntry> findAllByOrderByDateDesc();
}
