package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.Photo;
import ink.yunduan.nest.repo.PhotoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final PhotoRepository photoRepository;

    public GalleryController(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @GetMapping
    public List<Photo> list() {
        return photoRepository.findAllByOrderByTakenAtDesc();
    }
}
