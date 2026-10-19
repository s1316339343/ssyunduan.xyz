package ink.yunduan.nest.repo;

import ink.yunduan.nest.domain.VisitDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitDailyRepository extends JpaRepository<VisitDaily, Long> {
    Optional<VisitDaily> findByDate(LocalDate date);

    List<VisitDaily> findAllByOrderByDateAsc();

    List<VisitDaily> findByDateBetweenOrderByDateAsc(LocalDate start, LocalDate end);
}
