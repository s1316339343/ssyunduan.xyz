package ink.yunduan.nest.repo;

import ink.yunduan.nest.domain.AboutProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AboutProfileRepository extends JpaRepository<AboutProfile, Long> {
    Optional<AboutProfile> findFirstByOrderByIdAsc();
}
