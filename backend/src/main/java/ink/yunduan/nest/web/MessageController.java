package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.Message;
import ink.yunduan.nest.domain.SiteUser;
import ink.yunduan.nest.repo.MessageRepository;
import ink.yunduan.nest.service.AuthService;
import ink.yunduan.nest.web.dto.CreateMessageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final AuthService authService;

    public MessageController(MessageRepository messageRepository, AuthService authService) {
        this.messageRepository = messageRepository;
        this.authService = authService;
    }

    @GetMapping
    public List<Message> list() {
        return messageRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public Message create(
            @Valid @RequestBody CreateMessageRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            HttpServletRequest httpRequest
    ) {
        SiteUser user = authService.resolve(authorization);

        Message message = new Message();
        if (user != null) {
            message.setNickname(user.getNickname());
            message.setAdmin("ADMIN".equalsIgnoreCase(user.getRole()));
        } else {
            String nickname = request.nickname() == null ? "" : request.nickname().trim();
            message.setNickname(nickname.isEmpty() ? "匿名网友" : nickname);
            message.setAdmin(false);
        }
        message.setContent(request.content().trim());
        message.setCreatedAt(Instant.now());
        message.setLocation(isLocal(httpRequest) ? "本地 · 云端" : "中国 · 云端");
        message.setReplyTo(request.replyTo() == null || request.replyTo().isBlank() ? null : request.replyTo().trim());
        return messageRepository.save(message);
    }

    private boolean isLocal(HttpServletRequest request) {
        String remote = request.getRemoteAddr();
        return "127.0.0.1".equals(remote) || "0:0:0:0:0:0:0:1".equals(remote) || "::1".equals(remote);
    }
}
