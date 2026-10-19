package ink.yunduan.nest.service;

import ink.yunduan.nest.domain.SiteUser;
import ink.yunduan.nest.repo.SiteUserRepository;
import ink.yunduan.nest.web.dto.AuthDtos.AuthResponse;
import ink.yunduan.nest.web.dto.AuthDtos.LoginRequest;
import ink.yunduan.nest.web.dto.AuthDtos.RegisterRequest;
import ink.yunduan.nest.web.dto.AuthDtos.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final SiteUserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public AuthService(SiteUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("这个用户名已经被使用了，换一个试试。");
        }

        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(request.password()));
        user.setNickname(request.nickname().trim());
        user.setEmail(request.email().trim());
        user.setRole("USER");
        userRepository.save(user);

        return session(user);
    }

    public AuthResponse login(LoginRequest request) {
        SiteUser user = userRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码不正确。"));
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码不正确。");
        }
        return session(user);
    }

    public SiteUser resolve(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        Long userId = tokens.get(token);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    public UserDto toDto(SiteUser user) {
        return new UserDto(user.getUsername(), user.getNickname(), "", user.getRole());
    }

    public boolean isAdmin(SiteUser user) {
        return user != null
                && "ADMIN".equalsIgnoreCase(user.getRole())
                && "yunduan".equals(user.getUsername());
    }

    public SiteUser requireAdmin(String authorization) {
        SiteUser user = resolve(authorization);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录站长账号。");
        }
        if (!isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有站长账号可以管理内容。");
        }
        return user;
    }

    public void changePassword(SiteUser user, String oldPassword, String newPassword) {
        if (!encoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("当前密码不正确。");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码至少需要 6 位。");
        }
        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);
        // 改密后让所有旧登录态失效，前端会重新登录。
        tokens.clear();
    }

    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    private AuthResponse session(SiteUser user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, user.getId());
        return new AuthResponse(token, toDto(user));
    }
}
