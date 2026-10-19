package ink.yunduan.nest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final long MAX_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private final Path root;

    public UploadService(@Value("${app.upload-dir:./data/uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException error) {
            throw new IllegalStateException("无法创建图片上传目录：" + root, error);
        }
    }

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片。");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("图片不能超过 10MB，请压缩后再上传。");
        }

        String extension = extensionOf(file.getContentType(), file.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = root.resolve(filename).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("文件名不合法。");
        }

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            throw new IllegalStateException("图片保存失败，请稍后重试。", error);
        }
        return "/uploads/" + filename;
    }

    public void deleteByUrl(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }
        String filename = url.substring("/uploads/".length());
        if (filename.isBlank() || filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return;
        }
        try {
            Files.deleteIfExists(root.resolve(filename).normalize());
        } catch (IOException ignored) {
            // 删图失败不影响内容删除，最多留下一个孤立文件。
        }
    }

    private String extensionOf(String contentType, String originalName) {
        String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (type.equals("image/jpeg") || type.equals("image/jpg")) {
            return ".jpg";
        }
        if (type.equals("image/png")) {
            return ".png";
        }
        if (type.equals("image/webp")) {
            return ".webp";
        }
        if (type.equals("image/gif")) {
            return ".gif";
        }

        // 有些浏览器会传 application/octet-stream，此时退回看文件名后缀。
        String name = originalName == null ? "" : originalName.toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        String extension = dot >= 0 ? name.substring(dot) : "";
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("只支持 JPG、PNG、WebP、GIF 图片。");
        }
        return extension.equals(".jpeg") ? ".jpg" : extension;
    }
}
