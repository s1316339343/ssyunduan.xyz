package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.Friend;
import ink.yunduan.nest.repo.FriendRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendRepository friendRepository;

    public FriendController(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @GetMapping
    public List<Friend> list() {
        return friendRepository.findAllByOrderByDateDesc();
    }
}
