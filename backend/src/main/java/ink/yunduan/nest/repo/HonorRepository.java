package ink.yunduan.nest.repo;

import ink.yunduan.nest.domain.Honor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HonorRepository extends JpaRepository<Honor, Long> {
    List<Honor> findAllByOrderByPriorityDescIdAsc();

    List<Honor> findAllByVisibleTrueOrderByPriorityDescIdAsc();
}
