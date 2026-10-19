package ink.yunduan.nest.repo;

import ink.yunduan.nest.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByOrderByDateDesc();
}
