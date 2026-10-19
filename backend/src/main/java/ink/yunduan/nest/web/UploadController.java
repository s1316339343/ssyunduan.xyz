package ink.yunduan.nest.web;

import ink.yunduan.nest.service.AuthService;
import ink.yunduan.nest.service.UploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/uploads")
public class UploadController {

    private final AuthService authService;
    private final UploadService uploadService;

    public UploadController(AuthService authService, UploadService uploadService) {
        this.authService = authService;
        this.uploadService = uploadService;
    }

    @PostMapping
    public Map<String, String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        return Map.of("url", uploadService.save(file));
    }
}
