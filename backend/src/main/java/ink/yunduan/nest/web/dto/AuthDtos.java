package ink.yunduan.nest.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "请输入用户名")
            String username,
            @NotBlank(message = "请输入密码")
            String password
    ) {
    }

    public record RegisterRequest(
            @NotBlank(message = "请输入用户名")
            @Size(min = 3, max = 24, message = "用户名需要 3-24 个字符")
            @Pattern(regexp = "[A-Za-z0-9_]+", message = "用户名只能包含字母、数字和下划线")
            String username,
            @NotBlank(message = "请输入密码")
            @Size(min = 6, max = 64, message = "密码至少 6 位")
            String password,
            @NotBlank(message = "请输入昵称")
            @Size(max = 20, message = "昵称最多 20 个字符")
            String nickname,
            @NotBlank(message = "请输入邮箱")
            @Email(message = "邮箱格式不正确")
            String email
    ) {
    }

    public record UserDto(String username, String nickname, String avatar, String role) {
    }

    public record AuthResponse(String token, UserDto user) {
    }
}
