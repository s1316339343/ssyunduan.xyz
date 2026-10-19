package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.*;
import ink.yunduan.nest.repo.*;
import ink.yunduan.nest.service.AuthService;
import ink.yunduan.nest.service.UploadService;
import ink.yunduan.nest.web.dto.AdminDtos.*;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Transactional
public class AdminController {

    private final ArticleRepository articles;
    private final MomentRepository moments;
    private final FriendRepository friends;
    private final PhotoRepository photos;
    private final ChangelogRepository changelog;
    private final MessageRepository messages;
    private final AuthService authService;
    private final UploadService uploadService;

    public AdminController(
            ArticleRepository articles,
            MomentRepository moments,
            FriendRepository friends,
            PhotoRepository photos,
            ChangelogRepository changelog,
            MessageRepository messages,
            AuthService authService,
            UploadService uploadService
    ) {
        this.articles = articles;
        this.moments = moments;
        this.friends = friends;
        this.photos = photos;
        this.changelog = changelog;
        this.messages = messages;
        this.authService = authService;
        this.uploadService = uploadService;
    }

    /* ------------------------------ 文章 ------------------------------ */

    @GetMapping("/articles")
    public List<Article> allArticles(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(authorization);
        return articles.findAllByOrderByPublishedAtDesc();
    }

    @PostMapping("/articles")
    public Article createArticle(
            @Valid @RequestBody ArticleRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Article article = new Article();
        article.setViews(0);
        applyArticle(article, request);
        return articles.save(article);
    }

    @PutMapping("/articles/{id}")
    public Article updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Article article = articles.findById(id)
                .orElseThrow(() -> new NoSuchElementException("文章不存在。"));
        applyArticle(article, request);
        return articles.save(article);
    }

    @DeleteMapping("/articles/{id}")
    public Map<String, Object> deleteArticle(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        if (!articles.existsById(id)) {
            throw new NoSuchElementException("文章不存在。");
        }
        articles.deleteById(id);
        return Map.of("ok", true);
    }

    private void applyArticle(Article article, ArticleRequest request) {
        String content = request.content().trim();
        String plain = content.replaceAll("<[^>]+>", "").replaceAll("\\s+", "");

        String slug = request.slug() == null ? "" : request.slug().trim();
        if (slug.isEmpty()) {
            slug = "post-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 900 + 100);
        }
        if (slug.length() > 120) {
            throw new IllegalArgumentException("slug 最多 120 个字符。");
        }
        String finalSlug = slug;
        articles.findBySlug(finalSlug).ifPresent(existing -> {
            if (article.getId() == null || !existing.getId().equals(article.getId())) {
                throw new IllegalArgumentException("这个 slug 已经被另一篇文章使用了。");
            }
        });

        article.setSlug(finalSlug);
        article.setTitle(request.title().trim());
        article.setContent(content);
        article.setCategory(request.category().trim());
        article.setTags(cleanList(request.tags()));
        article.setCover(blankToDefault(request.cover(), "cloud"));
        article.setFeatured(request.featured());
        article.setPublished(request.published());
        article.setPublishedAt(request.publishedAt() == null ? LocalDate.now() : request.publishedAt());
        article.setUpdatedAt(LocalDate.now());
        article.setWords(plain.length());

        String excerpt = request.excerpt() == null ? "" : request.excerpt().trim();
        if (excerpt.isEmpty()) {
            excerpt = plain.length() > 120 ? plain.substring(0, 120) : plain;
        }
        article.setExcerpt(excerpt);
    }

    /* ------------------------------ 随笔 ------------------------------ */

    @GetMapping("/moments")
    public List<Moment> allMoments(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(authorization);
        return moments.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/moments")
    public Moment createMoment(
            @Valid @RequestBody MomentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Moment moment = new Moment();
        moment.setContent(request.content().trim());
        moment.setImages(cleanList(request.images()));
        moment.setCreatedAt(Instant.now());
        moment.setLikes(0);
        moment.setComments(0);
        return moments.save(moment);
    }

    @PutMapping("/moments/{id}")
    public Moment updateMoment(
            @PathVariable Long id,
            @Valid @RequestBody MomentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Moment moment = moments.findById(id)
                .orElseThrow(() -> new NoSuchElementException("随笔不存在。"));
        moment.setContent(request.content().trim());
        moment.setImages(cleanList(request.images()));
        return moments.save(moment);
    }

    @DeleteMapping("/moments/{id}")
    public Map<String, Object> deleteMoment(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        if (!moments.existsById(id)) {
            throw new NoSuchElementException("随笔不存在。");
        }
        moments.deleteById(id);
        return Map.of("ok", true);
    }

    /* ------------------------------ 友链 ------------------------------ */

    @GetMapping("/friends")
    public List<Friend> allFriends(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(authorization);
        return friends.findAllByOrderByDateDesc();
    }

    @PostMapping("/friends")
    public Friend createFriend(
            @Valid @RequestBody FriendRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Friend friend = new Friend();
        applyFriend(friend, request);
        return friends.save(friend);
    }

    @PutMapping("/friends/{id}")
    public Friend updateFriend(
            @PathVariable Long id,
            @Valid @RequestBody FriendRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Friend friend = friends.findById(id)
                .orElseThrow(() -> new NoSuchElementException("友链不存在。"));
        applyFriend(friend, request);
        return friends.save(friend);
    }

    @DeleteMapping("/friends/{id}")
    public Map<String, Object> deleteFriend(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        if (!friends.existsById(id)) {
            throw new NoSuchElementException("友链不存在。");
        }
        friends.deleteById(id);
        return Map.of("ok", true);
    }

    private void applyFriend(Friend friend, FriendRequest request) {
        friend.setName(request.name().trim());
        friend.setUrl(request.url().trim());
        friend.setDescription(request.description() == null ? "" : request.description().trim());
        friend.setAvatar(blankToDefault(request.avatar(), "cloud"));
        friend.setArt(blankToDefault(request.art(), "cloud"));
        friend.setDate(request.date() == null ? LocalDate.now() : request.date());
    }

    /* ------------------------------ 相册 ------------------------------ */

    @GetMapping("/photos")
    public List<Photo> allPhotos(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(authorization);
        return photos.findAllByOrderByTakenAtDesc();
    }

    @PostMapping("/photos")
    public Photo createPhoto(
            @Valid @RequestBody PhotoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Photo photo = new Photo();
        applyPhoto(photo, request);
        return photos.save(photo);
    }

    @PutMapping("/photos/{id}")
    public Photo updatePhoto(
            @PathVariable Long id,
            @Valid @RequestBody PhotoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Photo photo = photos.findById(id)
                .orElseThrow(() -> new NoSuchElementException("照片不存在。"));
        String oldImageUrl = photo.getImageUrl();
        applyPhoto(photo, request);
        Photo saved = photos.save(photo);
        if (oldImageUrl != null && !oldImageUrl.equals(saved.getImageUrl())) {
            uploadService.deleteByUrl(oldImageUrl);
        }
        return saved;
    }

    @DeleteMapping("/photos/{id}")
    public Map<String, Object> deletePhoto(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Photo photo = photos.findById(id)
                .orElseThrow(() -> new NoSuchElementException("照片不存在。"));
        photos.delete(photo);
        uploadService.deleteByUrl(photo.getImageUrl());
        return Map.of("ok", true);
    }

    private void applyPhoto(Photo photo, PhotoRequest request) {
        photo.setTitle(request.title().trim());
        photo.setCategory(request.category().trim());
        photo.setLocation(request.location() == null ? "" : request.location().trim());
        photo.setTakenAt(request.takenAt() == null ? LocalDate.now() : request.takenAt());
        photo.setArt(blankToDefault(request.art(), "mountain"));

        String imageUrl = request.imageUrl() == null ? "" : request.imageUrl().trim();
        if (!imageUrl.isEmpty() && !imageUrl.startsWith("/uploads/")) {
            throw new IllegalArgumentException("图片地址不合法。");
        }
        photo.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);
    }

    /* ------------------------------ 日志 ------------------------------ */

    @GetMapping("/changelog")
    public List<ChangelogEntry> allChangelog(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(authorization);
        return changelog.findAllByOrderByDateDesc();
    }

    @PostMapping("/changelog")
    public ChangelogEntry createChangelog(
            @Valid @RequestBody ChangelogRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        ChangelogEntry entry = new ChangelogEntry();
        applyChangelog(entry, request);
        return changelog.save(entry);
    }

    @PutMapping("/changelog/{id}")
    public ChangelogEntry updateChangelog(
            @PathVariable Long id,
            @Valid @RequestBody ChangelogRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        ChangelogEntry entry = changelog.findById(id)
                .orElseThrow(() -> new NoSuchElementException("更新日志不存在。"));
        applyChangelog(entry, request);
        return changelog.save(entry);
    }

    @DeleteMapping("/changelog/{id}")
    public Map<String, Object> deleteChangelog(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        if (!changelog.existsById(id)) {
            throw new NoSuchElementException("更新日志不存在。");
        }
        changelog.deleteById(id);
        return Map.of("ok", true);
    }

    private void applyChangelog(ChangelogEntry entry, ChangelogRequest request) {
        entry.setDate(request.date() == null ? LocalDate.now() : request.date());
        entry.setVersion(blankToDefault(request.version(), "v1.0.0"));
        entry.setItems(cleanList(request.items()));
    }

    /* ------------------------------ 留言 / 账号 ------------------------------ */

    @DeleteMapping("/messages/{id}")
    public Map<String, Object> deleteMessage(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        if (!messages.existsById(id)) {
            throw new NoSuchElementException("留言不存在。");
        }
        messages.deleteById(id);
        return Map.of("ok", true);
    }

    @PutMapping("/password")
    public Map<String, Object> changePassword(
            @Valid @RequestBody PasswordRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        SiteUser admin = authService.requireAdmin(authorization);
        authService.changePassword(admin, request.oldPassword(), request.newPassword());
        return Map.of("ok", true);
    }

    /* ------------------------------ 工具方法 ------------------------------ */

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .limit(30)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
