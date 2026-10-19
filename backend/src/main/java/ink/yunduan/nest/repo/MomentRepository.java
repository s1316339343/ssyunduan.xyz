package ink.yunduan.nest.repo;

import ink.yunduan.nest.domain.Moment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MomentRepository extends JpaRepository<Moment, Long> {
    List<Moment> findAllByOrderByCreatedAtDesc();
}
