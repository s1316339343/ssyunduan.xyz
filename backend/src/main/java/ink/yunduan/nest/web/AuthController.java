package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.SiteUser;
import ink.yunduan.nest.service.AuthService;
import ink.yunduan.nest.web.dto.AuthDtos.AuthResponse;
import ink.yunduan.nest.web.dto.AuthDtos.LoginRequest;
import ink.yunduan.nest.web.dto.AuthDtos.RegisterRequest;
import ink.yunduan.nest.web.dto.AuthDtos.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserDto me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        SiteUser user = authService.resolve(authorization);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已过期，请重新登录。");
        }
        return authService.toDto(user);
    }
}
