package ink.yunduan.nest.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record ArticleRequest(
            @NotBlank(message = "文章标题不能为空")
            @Size(max = 200, message = "标题最多 200 个字符")
            String title,
            @Size(max = 120, message = "slug 最多 120 个字符")
            String slug,
            @Size(max = 500, message = "摘要最多 500 个字符")
            String excerpt,
            @NotBlank(message = "文章正文不能为空")
            String content,
            @NotBlank(message = "请选择分类")
            @Size(max = 40, message = "分类最多 40 个字符")
            String category,
            List<String> tags,
            @Size(max = 40, message = "封面标识最多 40 个字符")
            String cover,
            LocalDate publishedAt,
            boolean featured,
            boolean published
    ) {
    }

    public record MomentRequest(
            @NotBlank(message = "随笔内容不能为空")
            String content,
            List<String> images
    ) {
    }

    public record FriendRequest(
            @NotBlank(message = "友链名称不能为空")
            @Size(max = 60, message = "友链名称最多 60 个字符")
            String name,
            @NotBlank(message = "友链地址不能为空")
            @Size(max = 200, message = "友链地址最多 200 个字符")
            String url,
            @Size(max = 300, message = "友链描述最多 300 个字符")
            String description,
            @Size(max = 40, message = "头像标识最多 40 个字符")
            String avatar,
            @Size(max = 40, message = "封面标识最多 40 个字符")
            String art,
            LocalDate date
    ) {
    }

    public record PhotoRequest(
            @NotBlank(message = "照片标题不能为空")
            @Size(max = 80, message = "照片标题最多 80 个字符")
            String title,
            @NotBlank(message = "请选择照片分类")
            @Size(max = 40, message = "照片分类最多 40 个字符")
            String category,
            @Size(max = 80, message = "地点最多 80 个字符")
            String location,
            LocalDate takenAt,
            @Size(max = 40, message = "画面标识最多 40 个字符")
            String art,
            @Size(max = 500, message = "图片地址过长")
            String imageUrl
    ) {
    }

    public record ChangelogRequest(
            LocalDate date,
            @Size(max = 20, message = "版本号最多 20 个字符")
            String version,
            List<String> items
    ) {
    }

    public record PasswordRequest(
            @NotBlank(message = "请输入当前密码")
            String oldPassword,
            @NotBlank(message = "请输入新密码")
            @Size(min = 6, max = 64, message = "新密码至少 6 位")
            String newPassword
    ) {
    }
}
