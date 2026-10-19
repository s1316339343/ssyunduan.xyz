package ink.yunduan.nest.web;

import ink.yunduan.nest.service.StatsService;
import ink.yunduan.nest.web.dto.StatsResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return statsService.build();
    }

    @PostMapping("/visits/track")
    public Map<String, Object> track(HttpServletRequest request) {
        statsService.track(request);
        return Map.of("ok", true);
    }
}
