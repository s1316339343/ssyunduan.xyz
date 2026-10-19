package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.Moment;
import ink.yunduan.nest.repo.MomentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moments")
public class MomentController {

    private final MomentRepository momentRepository;

    public MomentController(MomentRepository momentRepository) {
        this.momentRepository = momentRepository;
    }

    @GetMapping
    public List<Moment> list() {
        return momentRepository.findAllByOrderByCreatedAtDesc();
    }
}
