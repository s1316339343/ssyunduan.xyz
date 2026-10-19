package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.ChangelogEntry;
import ink.yunduan.nest.repo.ChangelogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/changelog")
public class ChangelogController {

    private final ChangelogRepository changelogRepository;

    public ChangelogController(ChangelogRepository changelogRepository) {
        this.changelogRepository = changelogRepository;
    }

    @GetMapping
    public List<ChangelogEntry> list() {
        return changelogRepository.findAllByOrderByDateDesc();
    }
}
