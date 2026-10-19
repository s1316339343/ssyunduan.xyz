package ink.yunduan.nest.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(
        @Size(max = 20, message = "称呼最多 20 个字符")
        String nickname,
        @NotBlank(message = "留言内容不能为空")
        @Size(max = 300, message = "留言不能超过 300 个字符")
        String content,
        @Size(max = 40, message = "回复对象不正确")
        String replyTo
) {
}
